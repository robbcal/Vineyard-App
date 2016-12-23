package com.example.vineyard_2;


import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment_User extends Fragment {

    private TextView userProfileName, userProfileEmail;
    private ImageButton userProfilePhoto;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseUser;

    public ProfileFragment_User() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_user, container, false);

        userProfileName = (TextView) v.findViewById(R.id.user_profile_name);
        userProfileEmail = (TextView) v.findViewById(R.id.user_email);
        userProfilePhoto = (ImageButton) v.findViewById(R.id.user_profile_photo);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        databaseUser.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                //assigning values
                String userName = dataSnapshot.child("name").getValue(String.class);
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                //String userPhoto = dataSnapshot.child("image").getValue(String.class);

                userProfileName.setText(userName);
                userProfileEmail.setText(userEmail);
                //userProfilePhoto.setImageURI(userPhoto.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

}
