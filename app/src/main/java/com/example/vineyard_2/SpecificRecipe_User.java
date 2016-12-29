package com.example.vineyard_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SpecificRecipe_User extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("users/"+uid+"/recipes");
    private static final String TAG = "Chiz";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_recipe_user);

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

        Intent intent = getIntent();
        final String url = intent.getStringExtra("url");
        final String key = intent.getStringExtra("key");

        Log.d(TAG, "key passed: "+key);

        if(haveNetworkConnection() == true){
            WebView webview = new WebView(this);
            setContentView(webview);
            webview.loadUrl(url);
        }else{
            Log.d(TAG, "not connected");

            if (user != null) {
                mRecipeRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String title = snapshot.child("title").getValue(String.class);
                        final String desc = snapshot.child("description").getValue(String.class);
                        String img = snapshot.child("image_url").getValue(String.class);

                        ImageView img1 = new ImageView(getApplicationContext());
                        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(600,600);
                        parms.gravity= Gravity.CENTER;
                        img1.setLayoutParams(parms);
                        Picasso.with(getApplicationContext()).load(img).error(R.drawable.placeholder_error).into(img1);
                        linearLayout.addView(img1);

                        TextView tv1 = new TextView(getApplicationContext());
                        tv1.setText(title);
                        tv1.setTextSize(30);
                        tv1.setTypeface(null, Typeface.BOLD);
                        tv1.setTextColor(Color.BLACK);
                        linearLayout.addView(tv1);

                        TextView tv2 = new TextView(getApplicationContext());
                        tv2.setText(desc);
                        tv2.setTextSize(20);
                        tv2.setTextColor(Color.BLACK);
                        linearLayout.addView(tv2);

                        mRecipeRef.child(key+"/content/ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                TextView tv3 = new TextView(getApplicationContext());
                                tv3.setText("\nIngredients:");
                                tv3.setTextSize(20);
                                tv3.setTextColor(Color.BLACK);
                                linearLayout.addView(tv3);

                                for (DataSnapshot ingSnapshot: dataSnapshot.getChildren()) {
                                    String ingr = ingSnapshot.getValue().toString().toLowerCase();
                                    ingr = ingr.replace("{ingredient=","");
                                    ingr = ingr.replaceAll("\\}", "");

                                    TextView text= new TextView(getApplicationContext());
                                    text.setText("> "+ingr);
                                    text.setTextSize(15);
                                    text.setTextColor(Color.BLACK);
                                    linearLayout.addView(text);
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        });

                        mRecipeRef.child(key+"/content/directions").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                TextView tv4 = new TextView(getApplicationContext());
                                tv4.setText("\nDirections:");
                                tv4.setTextSize(20);
                                tv4.setTextColor(Color.BLACK);
                                linearLayout.addView(tv4);

                                for (DataSnapshot dirSnapshot: dataSnapshot.getChildren()) {
                                    String dir = dirSnapshot.getValue().toString().toLowerCase();
                                    dir = dir.replace("{step=","");
                                    dir = dir.replaceAll("\\}", "");

                                    if(dir.matches("[\\n]+")){

                                    }else {
                                        TextView text = new TextView(getApplicationContext());
                                        text.setText("> " + dir);
                                        text.setTextSize(15);
                                        text.setTextColor(Color.BLACK);
                                        linearLayout.addView(text);
                                    }
                                }

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "Enjoy the full Vineyard experience through signing up/signing in.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
