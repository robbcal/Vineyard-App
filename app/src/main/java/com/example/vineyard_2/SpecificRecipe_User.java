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

import java.util.ArrayList;
import java.util.Arrays;

public class SpecificRecipe_User extends AppCompatActivity {

    private ImageView recipeImage;
    private TextView recipeTitle, recipeDescription, recipeIngredients, recipeDirections, directionHeader, ingredientHeader;
    ArrayList<String> Ingredients;
    ArrayList<String> Directions;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("users/"+uid+"/recipes");
    DatabaseReference mContentsRef = mRootRef.child("contents");

    private static final String TAG = "Vineyard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_recipe_user);

        mRecipeRef.keepSynced(true);

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

                mRecipeRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String title = snapshot.child("title").getValue(String.class);
                        final String desc = snapshot.child("description").getValue(String.class);
                        String img = snapshot.child("image_url").getValue(String.class);
                        String ingr = snapshot.child("ingredients").getValue(String.class);
                        String dir = snapshot.child("directions").getValue(String.class);

                        Ingredients = new ArrayList<String>(Arrays.asList(ingr.split("\\|")));
                        Directions = new ArrayList<String>(Arrays.asList(dir.split("\\|")));

                        Picasso.with(getApplicationContext()).load(img).error(R.drawable.placeholder_error).into(recipeImage);
                        recipeTitle.setText(title);
                        recipeDescription.setText(desc);

                        for(int i = 0; i < Ingredients.size(); i++){
                            recipeIngredients.append("> " + Ingredients.get(i) + "\n");
                        }

                        for(int a = 0; a < Directions.size(); a++){
                            recipeDirections.append("> " + Directions.get(a) + "\n");;
                        }

//                        mContentsRef.child(key).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot snapshot) {
//                                String ingr = snapshot.child("ingredients").getValue(String.class);
//                                String dir = snapshot.child("directions").getValue(String.class);
//
//                                Ingredients = new ArrayList<String>(Arrays.asList(ingr.split("\\|")));
//                                for(int i = 0; i < Ingredients.size(); i++){
//                                    recipeIngredients.append("> " + Ingredients.get(i) + "\n");
//                                }
//
//                                Directions = new ArrayList<String>(Arrays.asList(dir.split("\\|")));
//                                for(int a = 0; a < Directions.size(); a++){
//                                    recipeDirections.append("> " + Directions.get(a) + "\n");;
//                                }
//                            }
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                            }
//                        });

                        /*mRecipeRef.child(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                String ingr = snapshot.child("ingredients").getValue(String.class);
                                String dir = snapshot.child("directions").getValue(String.class);

                                Ingredients = new ArrayList<String>(Arrays.asList(ingr.split("\\|")));
                                for(int i = 0; i < Ingredients.size(); i++){
                                    recipeIngredients.append("> " + Ingredients.get(i) + "\n");
                                }

                                Directions = new ArrayList<String>(Arrays.asList(dir.split("\\|")));
                                for(int a = 0; a < Directions.size(); a++){
                                    recipeDirections.append("> " + Directions.get(a) + "\n");;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        });*/
                        /*Ingredients = new ArrayList<String>(Arrays.asList(ingr.split("\\|")));
                        for(int i = 0; i < Ingredients.size(); i++){
                            recipeIngredients.append("> " + Ingredients.get(i) + "\n");
                        }

                        Directions = new ArrayList<String>(Arrays.asList(dir.split("\\|")));
                        for(int a = 0; a < Directions.size(); a++){
                            recipeDirections.append("> " + Directions.get(a) + "\n");;
                        }*/


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
