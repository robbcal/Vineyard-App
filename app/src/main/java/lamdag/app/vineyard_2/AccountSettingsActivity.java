package lamdag.app.vineyard_2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AccountSettingsActivity extends AppCompatActivity {

    private Button btnEdit;
    private Button btnDelete;
    private EditText inputEmail, inputPassword, inputName;
    private TextView editText;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseUser;

    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        progress = new ProgressDialog(this);

        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.name);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        editText = (TextView) findViewById(R.id.edit_header);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        databaseUser = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        databaseUser.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {

                //assigning values
                String name = dataSnapshot.child("name").getValue(String.class);
                String password = dataSnapshot.child("password").getValue(String.class);

                inputPassword.setText(password);
                inputName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = inputName.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter name.", Toast.LENGTH_SHORT).show();
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

                new AlertDialog.Builder(AccountSettingsActivity.this)
                        .setTitle("Edit Details")
                        .setMessage("Are you sure you want to edit your details?")
                        .setPositiveButton("No", null)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                databaseUser.child("name").setValue(name);
                                databaseUser.child("password").setValue(password);

                                user.updatePassword(password)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });

                                Toast.makeText(AccountSettingsActivity.this, "Successfully edited user details.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(AccountSettingsActivity.this, LandingpageActivity_User.class);
                                startActivity(intent);
                            }
                        }).create().show();



            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(AccountSettingsActivity.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete this account? All saved recipes will also be deleted.")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progress.setMessage("This may take a while...");
                                progress.setCancelable(false);
                                progress.show();

                                databaseUser.removeValue();
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent  = new Intent(AccountSettingsActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);

                                            Toast.makeText(AccountSettingsActivity.this, "Successfully deleted account.",
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
