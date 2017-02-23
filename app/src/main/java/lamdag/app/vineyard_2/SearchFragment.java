package lamdag.app.vineyard_2;


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
    DatabaseReference mContentsIngredients = mRootRef.child("contents_Ingredients");

    TextView recipeUrl, recipeTitle, recipeDescription, recipeKey;
    ListView listView;
    MultiAutoCompleteTextView searchField;
    Button searchButton;
    Button clearButton;
    Button moreButton;
    List<Recipes> rowItems;
    ArrayList<String> searchedIngredients;
    FirebaseListAdapter<Recipe> mAdapter;
    int limit;
    AlertDialog loadingDialog;

    private static final String TAG = "Vineyard";

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        mRecipeRef.keepSynced(true);
        mContentsIngredients.keepSynced(true);

        searchField =(MultiAutoCompleteTextView)v.findViewById(R.id.search_field);
        searchButton = (Button)v.findViewById(R.id.search_button);
        clearButton = (Button)v.findViewById(R.id.clearSearch);
        listView = (ListView)v.findViewById(R.id.recipe_list);

        limit = 20;
        loadingDialog = new AlertDialog.Builder(getActivity()).create();
        loadingDialog.setMessage("Recipe data currently loading...");

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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(), R.layout.auto_complete, ingredients);
                searchField.setAdapter(adapter);
                searchField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    public void getData(){
        final AlertDialog loadDialog = new AlertDialog.Builder(getActivity()).create();
        loadDialog.setMessage("Recipe data currently loading...");
        loadDialog.show();

        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list_guest, mRecipeRef.orderByChild("title").limitToFirst(20)) {
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

                Picasso.with(getActivity().getApplicationContext()).load(imgUrl).error(R.drawable.placeholder_error).into((ImageView) view.findViewById(R.id.icon));
                recipeTitle.setText(title);
                recipeUrl.setText(url);
                recipeKey.setText(recipe_key);
                recipeDescription.setText(description);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity().getApplicationContext(), SpecificRecipe.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                });
                loadDialog.dismiss();
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

    public void searchIngredient(){
        hideKeypad();
        moreButton.setVisibility(listView.GONE);
        listView.setAdapter(null);
        getSearchedIngredient();

        String string = searchField.getText().toString().trim();

        if(string.isEmpty() || string.length() == 0){
            moreButton.setVisibility(listView.VISIBLE);
            getData();
        }else {
            loadingDialog.show();

            mContentsIngredients.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int flag = 0;
                    rowItems = new ArrayList<Recipes>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        final String recipeKey = postSnapshot.getKey();
                        String ingr = postSnapshot.child("ingredients").getValue(String.class);
                        int size = searchedIngredients.size();
                        int count = 0;

                        for (int a = 0; a < size; a++) {
                            String search = searchedIngredients.get(a);
                            String ing = ingr.toLowerCase();
                            if (ing.contains(search.toLowerCase())) {
                                count++;
                            }
                        }

                        if (count >= size) {
                            mRecipeRef.child(recipeKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final String title = dataSnapshot.child("title").getValue(String.class);
                                    final String url = dataSnapshot.child("url").getValue(String.class);
                                    final String image_url = dataSnapshot.child("image_url").getValue(String.class);
                                    final String description = dataSnapshot.child("description").getValue(String.class);


                                    Recipes item = new Recipes(title, url, image_url, recipeKey, description, "");
                                    rowItems.add(item);

                                    RecipeListAdapter_Guest adapter = new RecipeListAdapter_Guest(getActivity().getApplicationContext(), rowItems);
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

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                }
                            });
                            flag++;
                        }
                    }
                    loadingDialog.dismiss();
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
        moreButton.setVisibility(listView.VISIBLE);
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

    public void onClickMoreRecipes() {
        limit += 20;

        mAdapter = new FirebaseListAdapter<Recipe>(getActivity(), Recipe.class, R.layout.custom_list_guest, mRecipeRef.orderByChild("title").limitToFirst(limit)) {
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

                Picasso.with(getActivity().getApplicationContext()).load(imgUrl).error(R.drawable.placeholder_error).into((ImageView) view.findViewById(R.id.icon));
                recipeTitle.setText(title);
                recipeUrl.setText(url);
                recipeKey.setText(recipe_key);
                recipeDescription.setText(description);

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
}
