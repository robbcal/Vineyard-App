package com.example.vineyard_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordActivity extends AppCompatActivity {

    private Button btnReset;
    private TextView resetHeader, resetText;
    private EditText inputEmail;
    private ProgressDialog progress;

    private FirebaseAuth auth;
    private Typeface typeFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        typeFace = Typeface.createFromAsset(getAssets(), "HelveticaNeueLight.ttf");

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset);
        resetHeader = (TextView) findViewById(R.id.reset_header);
        resetText = (TextView) findViewById(R.id.reset_text);

        auth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        //set font typeface
        btnReset.setTypeface(typeFace);
        inputEmail.setTypeface(typeFace);
        resetHeader.setTypeface(typeFace);
        resetText.setTypeface(typeFace);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progress.setMessage("Sending reset password email...");
                progress.show();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password.", Toast.LENGTH_SHORT).show();
                                    redirectLogin();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                                }

                                progress.dismiss();
                            }
                        });
            }
        });
    }

    private void redirectLogin() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
