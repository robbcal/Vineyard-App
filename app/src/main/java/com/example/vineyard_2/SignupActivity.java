package com.example.vineyard_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private TextView signupText;
    private Button btnSignUp, btnCancel, btnGuest;
    private SignInButton btnGoogle;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Typeface typeFace;

    private DatabaseReference database;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "SIGNUP_ACTIVITY";
    private ProgressDialog progress;

    private String defaultImg = "https://lh3.googleusercontent.com/-BggmCt8LnM4/WHX9NbrLN8I/AAAAAAAAADU/PhdBGCpCyYwxp3K_XIWCM3JusfUgOImSgCEw/w139-h140-p/icon.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        typeFace = Typeface.createFromAsset(getAssets(), "HelveticaNeueLight.ttf");

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user != null) {
                    for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                        if (user.getProviderId().equals("google.com")) {
                            Intent loginIntent = new Intent(SignupActivity.this, LandingpageActivity_Google.class);
                            startActivity(loginIntent);
                        } else {
                            Intent loginIntent = new Intent(SignupActivity.this, LandingpageActivity_User.class);
                            startActivity(loginIntent);
                        }
                    }
                }
            }
        };

        database = FirebaseDatabase.getInstance().getReference().child("users");

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnGuest = (Button) findViewById(R.id.btn_guest);
        btnGoogle = (SignInButton) findViewById(R.id.google);
        inputEmail = (EditText) findViewById(R.id.email);
        inputName = (EditText) findViewById(R.id.name);
        inputPassword = (EditText) findViewById(R.id.password);
        signupText = (TextView) findViewById(R.id.signup_text);

        //set font typeface
        btnSignUp.setTypeface(typeFace);
        btnCancel.setTypeface(typeFace);
        btnGuest.setTypeface(typeFace);
        inputEmail.setTypeface(typeFace);
        inputName.setTypeface(typeFace);
        inputPassword.setTypeface(typeFace);
        signupText.setTypeface(typeFace);

        progress = new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignupActivity.this, "Error signing up with google", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
                progress.setMessage("Signing Up...");
                progress.show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LandingpageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signUp() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void startRegister() {
        final String name = inputName.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter a minimum of 6 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Signing Up...");
        progress.show();
        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progress.dismiss();
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String userid = auth.getCurrentUser().getUid();

                            DatabaseReference currentUserDB =  database.child(userid);
                            currentUserDB.child("name").setValue(name);
                            currentUserDB.child("image").setValue(defaultImg);
                            currentUserDB.child("email").setValue(email);
                            currentUserDB.child("password").setValue(password);
                            currentUserDB.child("usertype").setValue("user");

                            progress.dismiss();
                            Intent mainIntent = new Intent(SignupActivity.this, LandingpageActivity_User.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

                Intent intent = new Intent(SignupActivity.this, LandingpageActivity_Google.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(SignupActivity.this, "Error signing in with google", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
