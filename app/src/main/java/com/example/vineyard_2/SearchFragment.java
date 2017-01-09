package com.example.vineyard_2;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

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


public class SearchFragment extends Fragment {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("recipes");
    DatabaseReference mIngredients = mRootRef.child("ingredients");

    CheckBox breakfast;
    CheckBox lunch;
    CheckBox snacks;
    CheckBox dinner;
    ListView listView;
    MultiAutoCompleteTextView searchField;
    Button searchButton;
    Button clearButton;
    List<Recipes> rowItems;
    ArrayList<String> searchedIngredients;
    FirebaseListAdapter<Recipe> mAdapter;

    private Typeface typeFace;

    private static final String TAG = "Vineyard";

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        typeFace = Typeface.createFromAsset(getActivity().getAssets(), "HelveticaNeueLight.ttf");

        breakfast = (CheckBox) v.findViewById(R.id.Breakfast);
        lunch = (CheckBox) v.findViewById(R.id.Lunch);
        snacks = (CheckBox) v.findViewById(R.id.Snacks);
        dinner = (CheckBox) v.findViewById(R.id.Dinner);

        searchField =(MultiAutoCompleteTextView)v.findViewById(R.id.search_field);
        searchButton = (Button)v.findViewById(R.id.search_button);
        clearButton = (Button)v.findViewById(R.id.clearSearch);
        listView = (ListView)v.findViewById(R.id.recipe_list);

        //set font typeface
        searchButton.setTypeface(typeFace);
        searchField.setTypeface(typeFace);
        breakfast.setTypeface(typeFace);
        lunch.setTypeface(typeFace);
        snacks.setTypeface(typeFace);
        dinner.setTypeface(typeFace);

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

        breakfast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    filter();
                }else{
                    if(!lunch.isChecked() && !snacks.isChecked() && !dinner.isChecked()) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        lunch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    filter();
                }else{
                    if(!breakfast.isChecked() && !snacks.isChecked() && !dinner.isChecked()) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        snacks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    filter();
                }else{
                    if(!lunch.isChecked() && !breakfast.isChecked() && !dinner.isChecked()) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        dinner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    filter();
                }else{
                    if(!lunch.isChecked() && !snacks.isChecked() && !breakfast.isChecked()) {
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

    public void getData(){
        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list_guest, mRecipeRef.orderByChild("title")) {
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
                        intent.putExtra("url", url);
                        startActivity(intent);
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
                    String descSearch = description.toLowerCase();
                    final boolean filter[] = new boolean[4];

                    if(breakfast.isChecked()) {
                        if(descSearch.contains("breakfast")){
                            filter[0] = true;
                        }
                    }

                    if(lunch.isChecked()) {
                        if(descSearch.contains("lunch")) {
                            filter[1] = true;
                        }
                    }

                    if(snacks.isChecked()) {
                        if(descSearch.contains("snack")){
                            filter[2] = true;
                        }
                    }

                    if(dinner.isChecked()) {
                        if(descSearch.contains("dinner") || descSearch.contains("supper")){
                            filter[3] = true;
                        }
                    }

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
                                if(breakfast.isChecked() || lunch.isChecked() || snacks.isChecked() || dinner.isChecked()){
                                    int x;
                                    for(x = 0; x < 4 && filter[x] != true; x++){}
                                    if(x < 4){
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
                                }else{
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
        getData();
        breakfast.setChecked(false);
        lunch.setChecked(false);
        snacks.setChecked(false);
        dinner.setChecked(false);
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

                    if(breakfast.isChecked()) {
                        if(descSearch.contains("breakfast")){
                            filter[0] = true;
                        }
                    }

                    if(lunch.isChecked()) {
                        if(descSearch.contains("lunch")) {
                            filter[1] = true;
                        }
                    }

                    if(snacks.isChecked()) {
                        if(descSearch.contains("snack")){
                            filter[2] = true;
                        }
                    }

                    if(dinner.isChecked()) {
                        if(descSearch.contains("dinner") || descSearch.contains("supper")){
                            filter[3] = true;
                        }
                    }

                    if(breakfast.isChecked() || lunch.isChecked() || snacks.isChecked() || dinner.isChecked()){
                        int x;
                        for(x = 0; x < 4 && filter[x] != true; x++){}
                        if(x < 4) {
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

}
