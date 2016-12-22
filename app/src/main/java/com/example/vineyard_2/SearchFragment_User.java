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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchFragment_User extends Fragment {
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();;
    DatabaseReference mRecipeRef = mRootRef.child("recipes");
    ListView listView;
    EditText searchField;
    Button searchButton;
    Button clearButton;
    List<Recipes> rowItems;
    ArrayList<String> searchedIngredients;
    private static final String TAG = "Chiz";

    public SearchFragment_User() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        searchField =(EditText)v.findViewById(R.id.search_field);
        searchButton = (Button)v.findViewById(R.id.search_button);
        clearButton = (Button)v.findViewById(R.id.clearSearch);
        listView = (ListView)v.findViewById(R.id.recipe_list);

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
        hideKeypad();
        mRecipeRef.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rowItems = new ArrayList<Recipes>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String recipeKey = postSnapshot.getKey();
                    final String title = postSnapshot.child("title").getValue(String.class);
                    final String url = postSnapshot.child("url").getValue(String.class);
                    final String image_url = postSnapshot.child("image_url").getValue(String.class);

                    Log.d(TAG, "recipeKey: "+recipeKey);
                    Log.d(TAG, "title: "+title);
                    Log.d(TAG, "url: "+url);
                    Log.d(TAG, "image_url: "+image_url);
                    Log.d(TAG, "-----");

                    Recipes item = new Recipes(title, url, image_url, recipeKey);
                    rowItems.add(item);

                    RecipeListAdapter adapter = new RecipeListAdapter(getActivity().getApplicationContext(), rowItems);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            TextView text = (TextView) view.findViewById(R.id.Text2);
                            String recipe_url = text.getText().toString().trim();

                            Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
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
                    DatabaseReference mIngredientRef = mRootRef.child("recipes/"+recipeKey+"/content/ingredients");

                    Log.d(TAG, "recipeKey: "+recipeKey);
                    Log.d(TAG, "title: "+title);
                    Log.d(TAG, "url: "+url);
                    Log.d(TAG, "image_url: "+image_url);
                    Log.d(TAG, "-----");

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
                                        Log.d(TAG, "test: ingredient found");
                                    }
                                }
                            }
                            Log.d(TAG, "title: "+title+"-size: "+size+"-count: "+count);
                            if(count >= size) {
                                Recipes item = new Recipes(title, url, image_url, recipeKey);
                                rowItems.add(item);

                                RecipeListAdapter adapter = new RecipeListAdapter(getActivity().getApplicationContext(), rowItems);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        TextView text = (TextView) view.findViewById(R.id.Text2);
                                        String recipe_url = text.getText().toString().trim();

                                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
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
        Log.d(TAG, "test: comma separated string: "+string);

        searchedIngredients = new ArrayList<String>(Arrays.asList(string.split(",|\\, |\\ , |\\ ,")));
        Log.d(TAG, "test: ArrayList size: "+searchedIngredients.size());
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
