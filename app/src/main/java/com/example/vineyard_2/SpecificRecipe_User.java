package com.example.vineyard_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
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

    private ImageView recipeImage;
    private TextView recipeTitle, recipeDescription, recipeIngredients, recipeDirections, directionHeader, ingredientHeader;
    private Typeface typeFace;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("users/"+uid+"/recipes");

    private static final String TAG = "Vineyard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_recipe_user);

        typeFace = Typeface.createFromAsset(getAssets(), "HelveticaNeueLight.ttf");

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

                recipeImage = (ImageView) findViewById(R.id.recipe_image);
                recipeTitle = (TextView) findViewById(R.id.recipe_title);
                recipeDescription = (TextView) findViewById(R.id.recipe_description);
                recipeIngredients = (TextView) findViewById(R.id.recipe_ingredients);
                recipeDirections = (TextView) findViewById(R.id.recipe_directions);
                directionHeader = (TextView) findViewById(R.id.directions_header);
                ingredientHeader = (TextView) findViewById(R.id.ingredient_header);

                //set font typeface
                recipeTitle.setTypeface(typeFace);
                recipeDescription.setTypeface(typeFace);
                recipeIngredients.setTypeface(typeFace);
                recipeDirections.setTypeface(typeFace);
                directionHeader.setTypeface(typeFace);
                ingredientHeader.setTypeface(typeFace);

                mRecipeRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String title = snapshot.child("title").getValue(String.class);
                        final String desc = snapshot.child("description").getValue(String.class);
                        String img = snapshot.child("image_url").getValue(String.class);

                        Picasso.with(getApplicationContext()).load(img).error(R.drawable.placeholder_error).into(recipeImage);
                        recipeTitle.setText(title);
                        recipeDescription.setText(desc);

                        mRecipeRef.child(key+"/content/ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                TextView tv3 = new TextView(getApplicationContext());
//                                tv3.setText("\nIngredients:");
//                                tv3.setTextSize(20);
//                                tv3.setTextColor(Color.BLACK);
//                                linearLayout.addView(tv3);

                                for (DataSnapshot ingSnapshot: dataSnapshot.getChildren()) {
                                    String ingr = ingSnapshot.getValue().toString().toLowerCase();
                                    ingr = ingr.replace("{ingredient=","");
                                    ingr = ingr.replaceAll("\\}", "");

                                    recipeIngredients.append("> " + ingr + "\n");
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

                                for (DataSnapshot dirSnapshot: dataSnapshot.getChildren()) {
                                    String dir = dirSnapshot.getValue().toString().toLowerCase();
                                    dir = dir.replace("{step=","");
                                    dir = dir.replaceAll("\\}", "");

                                    if(dir.matches("[\\n]+")){

                                    } else {
                                        recipeDirections.append("> " + dir + "\n");
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
