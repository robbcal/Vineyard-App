package com.example.vineyard_2;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.clans.fab.FloatingActionButton;
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
    DatabaseReference mIngredients = mRootRef.child("ingredients");
    DatabaseReference mContentsIngredients = mRootRef.child("contents_Ingredients");
    DatabaseReference mContentsDirections = mRootRef.child("contents_Directions");

    TextView recipeUrl, recipeTitle, recipeDescription, recipeKey;
    Button addRecipe;
    FloatingActionButton breakfast, lunch, snacks, dinner;
    ListView listView;
    MultiAutoCompleteTextView searchField;
    Button searchButton, clearButton, moreButton;
    List<Recipes> rowItems;
    ArrayList<String> searchedIngredients;
    FirebaseListAdapter<Recipe> mAdapter;
    int limit;

    boolean bf = false;
    boolean lu = false;
    boolean sn = false;
    boolean di = false;

    private static final String TAG = "Vineyard";

    public SearchFragment_User() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_user, container, false);

        mRecipeRef.keepSynced(true);

        breakfast = (FloatingActionButton) v.findViewById(R.id.Breakfast);
        lunch = (FloatingActionButton) v.findViewById(R.id.Lunch);
        snacks = (FloatingActionButton) v.findViewById(R.id.Snacks);
        dinner = (FloatingActionButton) v.findViewById(R.id.Dinner);

        searchField =(MultiAutoCompleteTextView) v.findViewById(R.id.search_field);
        searchButton = (Button) v.findViewById(R.id.search_button);
        clearButton = (Button) v.findViewById(R.id.clearSearch);
        listView = (ListView) v.findViewById(R.id.recipe_list);

        limit = 20;

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

        View footerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);
        listView.addFooterView(footerView);

        moreButton = (Button) footerView.findViewById(R.id.more_button);
        moreButton.setVisibility(footerView.VISIBLE);

        moreButton.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                moreButton.setVisibility(v.GONE);
                onClickMoreRecipes();
            }
        }));

        mIngredients.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> ingredients = new ArrayList<String>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String ingredient = postSnapshot.child("ingredient").getValue(String.class);
                    ingredients.add(ingredient);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.auto_complete, ingredients);
                searchField.setAdapter(adapter);
                searchField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bf == false) {
                    bf = true;
                    Toast.makeText(v.getContext(), "Breakfast filter enabled.", Toast.LENGTH_SHORT).show();
                    filter();
                }else{
                    bf = false;
                    Toast.makeText(v.getContext(), "Breakfast filter disabled.", Toast.LENGTH_SHORT).show();
                    if(lu == false && sn == false && di == false) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lu == false) {
                    lu = true;
                    Toast.makeText(v.getContext(), "Lunch filter enabled.", Toast.LENGTH_SHORT).show();
                    filter();
                }else{
                    lu = false;
                    Toast.makeText(v.getContext(), "Lunch filter disabled.", Toast.LENGTH_SHORT).show();
                    if(bf == false && sn == false && di == false) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        snacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sn == false) {
                    sn = true;
                    Toast.makeText(v.getContext(), "Snacks filter enabled.", Toast.LENGTH_SHORT).show();
                    filter();
                }else{
                    sn = false;
                    Toast.makeText(v.getContext(), "Snacks filter disabled.", Toast.LENGTH_SHORT).show();
                    if(lu == false && bf == false && di == false) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(di == false) {
                    di = true;
                    Toast.makeText(v.getContext(), "Dinner filter enabled.", Toast.LENGTH_SHORT).show();
                    filter();
                }else{
                    di = false;
                    Toast.makeText(v.getContext(), "Dinner filter disabled.", Toast.LENGTH_SHORT).show();
                    if(lu == false && sn == false && bf == false) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        return v;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void getData(){
        final AlertDialog loadingDialog = new AlertDialog.Builder(getActivity()).create();
        loadingDialog.setMessage("Recipe data currently loading. This may take a while...");
        //loadingDialog.show();

        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list, mRecipeRef.orderByChild("title").limitToFirst(20)) {
            @Override
            protected void populateView(View view, Recipe r, int position) {
                DatabaseReference recipeRef = getRef(position);
                final String recipe_key = recipeRef.getKey();

                final String url = r.getUrl();
                final String title = r.getTitle();
                final String imgUrl = r.getImage_url();
                final String description = r.getDescription();

                recipeTitle = (TextView) view.findViewById(R.id.recipe_title);
                recipeKey = (TextView) view.findViewById(R.id.recipe_key);
                recipeUrl = (TextView) view.findViewById(R.id.recipe_url);
                recipeDescription = (TextView) view.findViewById(R.id.recipe_description);
                addRecipe = (Button) view.findViewById(R.id.add);

                Picasso.with(getActivity().getApplicationContext()).load(imgUrl).error(R.drawable.placeholder_error).into((ImageView) view.findViewById(R.id.icon));
                recipeTitle.setText(title);
                recipeUrl.setText(url);
                recipeKey.setText(recipe_key);
                recipeDescription.setText(description);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
                        intent.putExtra("key", recipe_key);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                addRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user != null) {
                            if(haveNetworkConnection() == true) {
                                String uid = user.getUid();
                                final DatabaseReference mspecificUser = mUserRef.child(uid + "/recipes/" + recipe_key);

                                mRecipeRef.child(recipe_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mspecificUser.setValue(snapshot.getValue());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });
                                //new
                                mContentsIngredients.child(recipe_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mspecificUser.child("ingredients").setValue(snapshot.child("ingredients").getValue());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });
                                //new
                                mContentsDirections.child(recipe_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mspecificUser.child("directions").setValue(snapshot.child("directions").getValue());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });

                                Toast.makeText(v.getContext(), title + " has been added.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(v.getContext(), "Cannot add. Network connection is unavailable", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(v.getContext(), "Enjoy the full Vineyard experience through signing up/signing in.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                loadingDialog.dismiss();
            }
        };
        listView.setAdapter(mAdapter);

        mRecipeRef.orderByChild("title").limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public void searchIngredient(){
        hideKeypad();
        moreButton.setVisibility(listView.GONE);
        listView.setAdapter(null);
        getSearchedIngredient();

        mContentsIngredients.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rowItems = new ArrayList<Recipes>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String recipeKey = postSnapshot.getKey();
                    String ingr = postSnapshot.child("ingredients").getValue(String.class);
                    int size = searchedIngredients.size();
                    int count = 0;

                    for(int a = 0; a <size; a++){
                        String search = searchedIngredients.get(a);
                        String ing = ingr.toLowerCase();
                        if(ing.contains(search.toLowerCase())) {
                            count++;
                        }
                    }

                    if(count >= size) {
                        mRecipeRef.child(recipeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String title = dataSnapshot.child("title").getValue(String.class);
                                final String url = dataSnapshot.child("url").getValue(String.class);
                                final String image_url = dataSnapshot.child("image_url").getValue(String.class);
                                final String description = dataSnapshot.child("description").getValue(String.class);
                                String descSearch = description.toLowerCase();
                                final boolean filter[] = new boolean[4];

                                if (bf == true) {
                                    if (descSearch.contains("breakfast")) {
                                        filter[0] = true;
                                    }
                                }

                                if (lu == true) {
                                    if (descSearch.contains("lunch")) {
                                        filter[1] = true;
                                    }
                                }

                                if (sn == true) {
                                    if (descSearch.contains("snack")) {
                                        filter[2] = true;
                                    }
                                }

                                if (di == true) {
                                    if (descSearch.contains("dinner") || descSearch.contains("supper")) {
                                        filter[3] = true;
                                    }
                                }

                                if (bf == true || lu == true || sn == true || di == true) {
                                    if(filter[0] == true || filter[1] == true || filter[2] == true ||filter[3] == true) {
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
                                }else if(searchField.getText().toString().trim().length() == 0){
                                    getData();
                                }else if(bf == false && lu == false && sn == false && di == false){
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
        /*Log.d(TAG, "test: ArrayList size: "+searchedIngredients.size());
        for(int i = 0; i < searchedIngredients.size(); i++){
            Log.d(TAG, "test: ingredient - "+searchedIngredients.get(i));
        }*/
        int size = searchedIngredients.size();
        for(int i = 0; i < size; i++){
            String word = searchedIngredients.get(i);
            if(word.trim().isEmpty()){
                searchedIngredients.remove(i);
            }
        }
    }

    public void clearSearchText(){
        searchField.setText("");
        listView.setAdapter(null);
        bf = false;
        lu = false;
        sn = false;
        di = false;
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

    public void filter(){
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
                    String descSearch = description.toLowerCase();
                    final boolean filter[] = new boolean[4];

                    if(bf == true) {
                        if(descSearch.contains("breakfast")){
                            filter[0] = true;
                        }
                    }

                    if(lu == true) {
                        if(descSearch.contains("lunch")) {
                            filter[1] = true;
                        }
                    }

                    if(sn == true) {
                        if(descSearch.contains("snack")){
                            filter[2] = true;
                        }
                    }

                    if(di == true) {
                        if(descSearch.contains("dinner") || descSearch.contains("supper")){
                            filter[3] = true;
                        }
                    }

                    if(bf == true || lu == true || sn == true || di == true){
                        if(filter[0] == true || filter[1] == true || filter[2] == true ||filter[3] == true) {
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
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void onClickMoreRecipes() {

        limit += 20;

        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list, mRecipeRef.orderByChild("title").limitToFirst(limit)) {
            @Override
            protected void populateView(View view, Recipe r, int position) {
                DatabaseReference recipeRef = getRef(position);
                final String recipe_key = recipeRef.getKey();

                moreButton.setVisibility(view.VISIBLE);

                final String url = r.getUrl();
                final String title = r.getTitle();
                final String imgUrl = r.getImage_url();
                final String description = r.getDescription();

                recipeTitle = (TextView) view.findViewById(R.id.recipe_title);
                recipeKey = (TextView) view.findViewById(R.id.recipe_key);
                recipeUrl = (TextView) view.findViewById(R.id.recipe_url);
                recipeDescription = (TextView) view.findViewById(R.id.recipe_description);
                addRecipe = (Button) view.findViewById(R.id.add);

                Picasso.with(getActivity().getApplicationContext()).load(imgUrl).error(R.drawable.placeholder_error).into((ImageView) view.findViewById(R.id.icon));
                recipeTitle.setText(title);
                recipeUrl.setText(url);
                recipeKey.setText(recipe_key);
                recipeDescription.setText(description);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
                        intent.putExtra("key", recipe_key);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                addRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (user != null) {
                            if(haveNetworkConnection() == true) {
                                String uid = user.getUid();
                                final DatabaseReference mspecificUser = mUserRef.child(uid + "/recipes/" + recipe_key);

                                mRecipeRef.child(recipe_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mspecificUser.setValue(snapshot.getValue());

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });
                                //new
                                mContentsIngredients.child(recipe_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mspecificUser.child("ingredients").setValue(snapshot.child("ingredients").getValue());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });
                                //new
                                mContentsDirections.child(recipe_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        mspecificUser.child("directions").setValue(snapshot.child("directions").getValue());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                });

                                Toast.makeText(v.getContext(), title + " has been added.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(v.getContext(), "Cannot add. Network connection is unavailable", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(v.getContext(), "Enjoy the full Vineyard experience through signing up/signing in.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        };
        listView.setAdapter(mAdapter);

        mRecipeRef.orderByChild("title").limitToFirst(limit).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

}

