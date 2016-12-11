package com.example.vineyard_2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


    }

    public void LandingPage (View view) {
        Intent startLandingPage = new Intent (this, LandingpageActivity.class);
        startActivity(startLandingPage);
    }

    public void LogIn (View view) {
        Intent startLogIn = new Intent (this, LoginActivity.class);
        startActivity(startLogIn);
    }
}
