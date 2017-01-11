package com.example.vineyard_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LandingpageActivity_Google extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    public static String uid;
    private DatabaseReference database;

    private Button btnLogout;
    private String defaultImg = "https://lh3.googleusercontent.com/-BggmCt8LnM4/WHX9NbrLN8I/AAAAAAAAADU/PhdBGCpCyYwxp3K_XIWCM3JusfUgOImSgCEw/w139-h140-p/icon.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        btnLogout = (Button) findViewById(R.id.btn_logout);
        database = FirebaseDatabase.getInstance().getReference().child("users");

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) {
                    // User is not signed in
                    Intent loginIntent = new Intent(LandingpageActivity_Google.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        };

        if (user != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() != defaultImg) {
                String userid = auth.getCurrentUser().getUid();
                String username = auth.getCurrentUser().getDisplayName();
                String email = auth.getCurrentUser().getEmail();
                String image = auth.getCurrentUser().getPhotoUrl().toString();

                DatabaseReference currentUserDB =  database.child(userid);
                currentUserDB.child("name").setValue(username);
                currentUserDB.child("image").setValue(image);
                currentUserDB.child("email").setValue(email);
                currentUserDB.child("usertype").setValue("user");
            }

            toolbar = (Toolbar)findViewById(R.id.toolBar);
            setSupportActionBar(toolbar);
            tabLayout = (TabLayout) findViewById(R.id.tabLayout);
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapter.addFragments(new HomeFragment_Google());
            viewPagerAdapter.addFragments(new SearchFragment_User());
            viewPagerAdapter.addFragments(new ProfileFragment_Google());
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.getTabAt(0).setIcon(R.drawable.home);
            tabLayout.getTabAt(1).setIcon(R.drawable.search);
            tabLayout.getTabAt(2).setIcon(R.drawable.menu);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(mAuthListener);
    }

    public void onClickLogout (View view) {
        new AlertDialog.Builder(LandingpageActivity_Google.this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out from your account?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        auth.signOut();

                        Intent intent  = new Intent(LandingpageActivity_Google.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).create().show();
    }

}