package com.example.vineyard_2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
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
import java.util.List;


public class SearchFragment_User extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("users");
    DatabaseReference mRecipeRef = mRootRef.child("recipes");

    CheckBox breakfast;
    CheckBox lunch;
    CheckBox snacks;
    CheckBox dinner;
    ListView listView;
    EditText searchField;
    Button searchButton;
    Button clearButton;
    List<Recipes> rowItems;
    ArrayList<String> searchedIngredients;
    FirebaseListAdapter<Recipe> mAdapter;
    private static final String TAG = "Vineyard";

    public SearchFragment_User() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_user, container, false);

        breakfast = (CheckBox) v.findViewById(R.id.Breakfast);
        lunch = (CheckBox) v.findViewById(R.id.Lunch);
        snacks = (CheckBox) v.findViewById(R.id.Snacks);
        dinner = (CheckBox) v.findViewById(R.id.Dinner);

        searchField =(EditText) v.findViewById(R.id.search_field);
        searchButton = (Button) v.findViewById(R.id.search_button);
        clearButton = (Button) v.findViewById(R.id.clearSearch);
        listView = (ListView) v.findViewById(R.id.recipe_list);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getData();

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchIngredient();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearSearchText();
            }
        });

        return v;
    }

    public void getData(){
        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list, mRecipeRef.orderByChild("title")) {
            @Override
            protected void populateView(View view, Recipe r, int position) {
                DatabaseReference recipeRef = getRef(position);
                final String recipeKey = recipeRef.getKey();

                final String url = r.getUrl();
                final String title = r.getTitle();
                final String imgUrl = r.getImage_url();
                final String description = r.getDescription();

                Picasso.with(getActivity().getApplicationContext()).load(imgUrl).error(R.drawable.placeholder_error).into((ImageView) view.findViewById(R.id.icon));
                ((TextView)view.findViewById(R.id.recipe_title)).setText(title);
                ((TextView) view.findViewById(R.id.recipe_url)).setText(url);
                ((TextView) view.findViewById(R.id.recipe_key)).setText(recipeKey);
                ((TextView) view.findViewById(R.id.recipe_description)).setText(description);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
                        intent.putExtra("key", recipeKey);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                ((ImageButton) view.findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user != null) {
                            String uid = user.getUid();
                            final DatabaseReference mspecificUser = mUserRef.child(uid+"/recipes/"+recipeKey);

                            mRecipeRef.child(recipeKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    mspecificUser.setValue(snapshot.getValue());

                                }
                                @Override public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                }
                            });

                            Toast.makeText(v.getContext(), title+" has been added.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "Enjoy the full Vineyard experience through signing up/signing in.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        };
        listView.setAdapter(mAdapter);
    }

    public void searchIngredient(){
        hideKeypad();
        listView.setAdapter(null);
        getSearchedIngredient();

        mRecipeRef.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rowItems = new ArrayList<Recipes>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String recipeKey = postSnapshot.getKey();
                    final String title = postSnapshot.child("title").getValue(String.class);
                    final String url = postSnapshot.child("url").getValue(String.class);
                    final String image_url = postSnapshot.child("image_url").getValue(String.class);
                    final String description = postSnapshot.child("description").getValue(String.class);

                    DatabaseReference mIngredientRef = mRootRef.child("recipes/"+recipeKey+"/content/ingredients");

                    mIngredientRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int size = searchedIngredients.size();
                            int count = 0;
                            for (DataSnapshot ingSnapshot: dataSnapshot.getChildren()) {
                                String ingr = ingSnapshot.getValue().toString().toLowerCase();
                                ingr = ingr.replace("{ingredient=","");
                                ingr = ingr.replaceAll("\\}", "");

                                for(int a = 0; a <size; a++){
                                    String search = searchedIngredients.get(a);
                                    if(ingr.contains(search.toLowerCase())) {
                                        count++;
                                    }
                                }
                            }
                            if(count >= size) {
                                Recipes item = new Recipes(title, url, image_url, recipeKey, description);
                                rowItems.add(item);

                                RecipeListAdapter adapter = new RecipeListAdapter(getActivity().getApplicationContext(), rowItems);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        TextView text = (TextView) view.findViewById(R.id.recipe_url);
                                        String recipe_url = text.getText().toString().trim();

                                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
                                        intent.putExtra("key", recipeKey);
                                        intent.putExtra("url", recipe_url);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void getSearchedIngredient() {
        String string = searchField.getText().toString();
        //Log.d(TAG, "test: comma separated string: "+string);

        searchedIngredients = new ArrayList<String>(Arrays.asList(string.split(",|\\, |\\ , |\\ ,")));
        //Log.d(TAG, "test: ArrayList size: "+searchedIngredients.size());
    }

    public void clearSearchText(){
        searchField.setText("");
        listView.setAdapter(null);
        getData();
    }

    public void hideKeypad(){
        try {
            InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            input.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}

