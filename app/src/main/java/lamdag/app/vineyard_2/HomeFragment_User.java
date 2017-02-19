package lamdag.app.vineyard_2;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment_User extends Fragment{

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("users/"+ LandingpageActivity_User.uid+"/recipes");
    DatabaseReference mRecipeCounterRef = mRootRef.child("users/"+ LandingpageActivity_User.uid+"/recipeCounter");

    TextView recipeUrl, recipeTitle, recipeDescription, recipekey, savedRecipeCount;
    Button removeRecipe;
    FloatingActionMenu meal;
    FloatingActionButton breakfast, lunch, snacks, dinner, others;
    ListView listView;
    EditText searchField;
    Button searchButton, clearButton;
    List<Recipes> rowItems;
    RecipeListAdapter_Home adapter;
    FirebaseListAdapter<Recipe> mAdapter = null;
    android.app.AlertDialog loadingDialog;

    boolean bf = false;
    boolean lu = false;
    boolean sn = false;
    boolean di = false;
    boolean other = false;

    private static final String TAG = "Vineyard";

    View v;

    public HomeFragment_User() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_user, container, false);

        mRecipeRef.keepSynced(true);

        savedRecipeCount = (TextView)v.findViewById(R.id.savedRecipesCount);

        searchField =(EditText)v.findViewById(R.id.search_field);
        searchButton = (Button)v.findViewById(R.id.search_button);
        clearButton = (Button)v.findViewById(R.id.clearSearch);
        listView = (ListView)v.findViewById(R.id.recipe_list);

        meal = (FloatingActionMenu) v.findViewById(R.id.fab);
        breakfast = (FloatingActionButton) v.findViewById(R.id.Breakfast);
        lunch = (FloatingActionButton) v.findViewById(R.id.Lunch);
        snacks = (FloatingActionButton) v.findViewById(R.id.Snacks);
        dinner = (FloatingActionButton) v.findViewById(R.id.Dinner);
        others = (FloatingActionButton) v.findViewById(R.id.Others);

        loadingDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        loadingDialog.setMessage("Recipe data currently loading...");

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recipeCounter();
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

        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bf) {
                    bf = true;
                    Toast.makeText(v.getContext(), "Breakfast filter enabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    filter();
                }else{
                    bf = false;
                    Toast.makeText(v.getContext(), "Breakfast filter disabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    if(!lu && !sn && !di && !other) {
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
                if(!lu) {
                    lu = true;
                    Toast.makeText(v.getContext(), "Lunch filter enabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    filter();
                }else{
                    lu = false;
                    Toast.makeText(v.getContext(), "Lunch filter disabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    if(!bf && !sn && !di && !other) {
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
                if(!sn) {
                    sn = true;
                    meal.close(true);
                    filter();
                }else{
                    sn = false;
                    Toast.makeText(v.getContext(), "Snacks filter disabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    if(!lu && !bf && !di && !other) {
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
                if(!di) {
                    di = true;
                    Toast.makeText(v.getContext(), "Dinner filter enabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    filter();
                }else{
                    di = false;
                    Toast.makeText(v.getContext(), "Dinner filter disabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    if(!lu && !sn && !bf && !other) {
                        listView.setAdapter(null);
                        getData();
                    }else{
                        filter();
                    }
                }
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!other) {
                    other = true;
                    Toast.makeText(v.getContext(), "Other recipes filter enabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    filter();
                }else{
                    other = false;
                    Toast.makeText(v.getContext(), "Other recipes filter disabled.", Toast.LENGTH_SHORT).show();
                    meal.close(true);
                    if(!lu && !sn && !bf && !di) {
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

    public void recipeCounter(){
        mRecipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    cnt++;
                }
                Log.d(TAG, "Recipes saved: "+cnt);
                mRecipeCounterRef.setValue(cnt);
                savedRecipeCount.setText("Recipes saved: "+cnt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
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

                recipeTitle = (TextView) view.findViewById(R.id.recipe_title);
                recipekey = (TextView) view.findViewById(R.id.recipe_key);
                recipeUrl = (TextView) view.findViewById(R.id.recipe_url);
                recipeDescription = (TextView) view.findViewById(R.id.recipe_description);
                removeRecipe = (Button) view.findViewById(R.id.remove);

                Picasso.with(getActivity().getApplicationContext()).load(imgUrl).error(R.drawable.placeholder_error).into((ImageView) view.findViewById(R.id.icon));
                recipeTitle.setText(title);
                recipeUrl.setText(url);
                recipekey.setText(recipeKey);
                recipeDescription.setText(description);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe_User.class);
                        intent.putExtra("key", recipeKey);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });

                removeRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete Saved Recipe")
                                .setMessage("Are you sure you want to delete this recipe?")
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        mRecipeRef.child(recipeKey).removeValue();
                                        Toast.makeText(getActivity().getApplicationContext(), title+" removed.",Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .setPositiveButton("No", null).create().show();
                    }
                });

            }
        };
        listView.setAdapter(mAdapter);
    }

    public void filter(){

        listView.setAdapter(null);

        mRecipeRef.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int flag = 0;
                rowItems = new ArrayList<Recipes>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String recipeKey = postSnapshot.getKey();
                    final String title = postSnapshot.child("title").getValue(String.class);
                    final String url = postSnapshot.child("url").getValue(String.class);
                    final String image_url = postSnapshot.child("image_url").getValue(String.class);
                    final String description = postSnapshot.child("description").getValue(String.class);
                    String descSearch = description.toLowerCase();
                    final boolean filter[] = new boolean[5];

                    if(bf) {
                        if(descSearch.contains("breakfast")){
                            filter[0] = true;
                        }
                    }

                    if(lu) {
                        if(descSearch.contains("lunch")) {
                            filter[1] = true;
                        }
                    }

                    if(sn) {
                        if(descSearch.contains("snack")){
                            filter[2] = true;
                        }
                    }

                    if(di) {
                        if(descSearch.contains("dinner") || descSearch.contains("supper")){
                            filter[3] = true;
                        }
                    }

                    if(other) {
                        if(!descSearch.contains("dinner") && !descSearch.contains("supper") && !descSearch.contains("snack") && !descSearch.contains("lunch") && !descSearch.contains("breakfast")){
                            filter[4] = true;
                        }
                    }

                    if(bf || lu || sn || di || other){
                        if(filter[0] || filter[1] || filter[2] ||filter[3] || filter[4]) {
                            Recipes item = new Recipes(title, url, image_url, recipeKey, description);
                            rowItems.add(item);

                            adapter = new RecipeListAdapter_Home(getActivity().getApplicationContext(), rowItems);
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
                            flag++;
                        }
                    }
                }
                Log.d(TAG, "FLAG: "+flag);
                if(flag == 0){
                    Toast.makeText(getContext(), "No Result/s Found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void searchRecipe(){

        hideKeypad();
        listView.setAdapter(null);
        final String search = searchField.getText().toString();

        mRecipeRef.orderByChild("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int flag = 0;
                rowItems = new ArrayList<Recipes>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String recipeKey = postSnapshot.getKey();
                    final String title = postSnapshot.child("title").getValue(String.class);
                    final String url = postSnapshot.child("url").getValue(String.class);
                    final String image_url = postSnapshot.child("image_url").getValue(String.class);
                    final String description = postSnapshot.child("description").getValue(String.class);
                    String descSearch = description.toLowerCase();
                    String t = title.toLowerCase();

                    if(t.contains(search.toLowerCase())){
                        final boolean filter[] = new boolean[5];

                        if (bf) {
                            if (descSearch.contains("breakfast")) {
                                filter[0] = true;
                            }
                        }

                        if (lu) {
                            if (descSearch.contains("lunch")) {
                                filter[1] = true;
                            }
                        }

                        if (sn) {
                            if (descSearch.contains("snack")) {
                                filter[2] = true;
                            }
                        }

                        if (di) {
                            if (descSearch.contains("dinner") || descSearch.contains("supper")) {
                                filter[3] = true;
                            }
                        }

                        if(other) {
                            if(!descSearch.contains("dinner") && !descSearch.contains("supper") && !descSearch.contains("snack") && !descSearch.contains("lunch") && !descSearch.contains("breakfast")){
                                filter[4] = true;
                            }
                        }

                        if (bf || lu || sn || di || other) {
                            if (filter[0] || filter[1] || filter[2] || filter[3] || filter[4]) {
                                Recipes item = new Recipes(title, url, image_url, recipeKey, description);
                                rowItems.add(item);

                                adapter = new RecipeListAdapter_Home(getActivity().getApplicationContext(), rowItems);
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
                        }else if (!bf && !lu && !sn && !di) {
                            Recipes item = new Recipes(title, url, image_url, recipeKey, description);
                            rowItems.add(item);

                            adapter = new RecipeListAdapter_Home(getActivity().getApplicationContext(), rowItems);
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
                        flag++;
                    }
                }
                Log.d(TAG, "FLAG: "+flag);
                if(flag == 0){
                    Toast.makeText(getContext(), "No Result/s Found.", Toast.LENGTH_LONG).show();
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
        bf = false;
        lu = false;
        sn = false;
        di = false;
        other = false;
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
