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
import java.util.List;


public class HomeFragment_User extends Fragment{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = user.getUid();
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("users/"+uid+"/recipes");

    ListView listView;
    EditText searchField;
    Button searchButton;
    Button clearButton;
    List<Recipes> rowItems;
    RecipeListAdapterHome adapter;
    FirebaseListAdapter<Recipe> mAdapter = null;
    private static final String TAG = "Vineyard";
    View v;

    public HomeFragment_User() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_user, container, false);

        mRecipeRef.keepSynced(true);

        searchField =(EditText)v.findViewById(R.id.search_field);
        searchButton = (Button)v.findViewById(R.id.search_button);
        clearButton = (Button)v.findViewById(R.id.clearSearch);
        listView = (ListView)v.findViewById(R.id.recipe_list);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getData();

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchRecipe();
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

        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list_home, mRecipeRef.orderByChild("title")) {
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
                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe_User.class);
                        intent.putExtra("key", recipeKey);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                ((ImageButton) view.findViewById(R.id.remove)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mRecipeRef.child(recipeKey).removeValue();
                        Toast.makeText(getActivity().getApplicationContext(), title+" removed.",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        listView.setAdapter(mAdapter);
    }

    public void searchRecipe(){

        hideKeypad();
        listView.setAdapter(null);
        final String search = searchField.getText().toString();

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
                    String t = title.toLowerCase();

                    if(t.contains(search.toLowerCase())){
                        Recipes item = new Recipes(title, url, image_url, recipeKey);
                        rowItems.add(item);

                        adapter = new RecipeListAdapterHome(getActivity().getApplicationContext(), rowItems);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView text = (TextView) view.findViewById(R.id.recipe_url);
                                String recipe_url = text.getText().toString().trim();

                                Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe_User.class);
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
