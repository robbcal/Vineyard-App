package com.example.vineyard_2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AccountSettingsActivity_Google extends AppCompatActivity {

    private Button btnDelete;
    private ProgressBar progressBar;
    private TextView editText, instructions;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseUser;

    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_google);

        progress = new ProgressDialog(this);

        btnDelete = (Button) findViewById(R.id.btn_delete);
        editText = (TextView) findViewById(R.id.edit_header);
        instructions = (TextView) findViewById(R.id.instructions);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(AccountSettingsActivity_Google.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete this account? All saved recipes will also be deleted.")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                databaseUser.removeValue();
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progress.setMessage("This may take a while...");
                                            progress.show();

                                            Intent intent  = new Intent(AccountSettingsActivity_Google.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                            Toast.makeText(AccountSettingsActivity_Google.this, "Successfully deleted account.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }).create().show();
            }
        });
    }
}
