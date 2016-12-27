package com.example.vineyard_2;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chiz on 12/27/16.
 */

public class offline extends Application{
    @Override
    public void onCreate(){
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
