package com.example.vineyard_2;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment_User extends Fragment {

    private TextView userProfileName, userProfileEmail;
    private ImageView userProfilePhoto;

    private Button btnLogout, btnEdit;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseUser;

    private Typeface typeFace;

    public ProfileFragment_User() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_user, container, false);

        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "HelveticaNeueLight.ttf");

        userProfileName = (TextView) v.findViewById(R.id.user_profile_name);
        userProfileEmail = (TextView) v.findViewById(R.id.user_email);
        userProfilePhoto = (ImageView) v.findViewById(R.id.user_profile_photo);

        btnLogout = (Button) v.findViewById(R.id.btn_logout);
        btnEdit = (Button) v.findViewById(R.id.btn_edit);

        //set font typeface
        userProfileName.setTypeface(typeFace);
        userProfileEmail.setTypeface(typeFace);
        btnLogout.setTypeface(typeFace);
        btnEdit.setTypeface(typeFace);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        databaseUser.keepSynced(true);

        databaseUser.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                //assigning values
                String userName = dataSnapshot.child("name").getValue(String.class);
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                String userPhoto = dataSnapshot.child("image").getValue(String.class);

                userProfileName.setText(userName);
                userProfileEmail.setText(userEmail);
                Glide.with(getActivity().getApplicationContext()).load(userPhoto)
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(userProfilePhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

}
