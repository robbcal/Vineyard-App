package lamdag.app.vineyard_2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.graphics.Typeface.BOLD;

public class RecipeListAdapter extends BaseAdapter {
    Context context;
    List<Recipes> rowItems;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("recipes");
    DatabaseReference mUserRef = mRootRef.child("users");
    DatabaseReference mContentsIngredients = mRootRef.child("contents_Ingredients");
    DatabaseReference mContentsDirections = mRootRef.child("contents_Directions");
    private static final String TAG = "Vineyard";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public RecipeListAdapter ( Context context, List<Recipes> items ) {
        this.context = context;
        this.rowItems = items;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtUrl;
        TextView txtID;
        Button addRecipe;
        TextView txtDescription;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_list, null);
            holder = new ViewHolder();
            holder.txtUrl = (TextView) convertView.findViewById(R.id.recipe_url);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.recipe_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.recipe_description);
            holder.txtID = (TextView) convertView.findViewById(R.id.recipe_key);

            holder.addRecipe = (Button) convertView.findViewById(R.id.add);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Recipes rowItem = (Recipes) getItem(position);

        final String url = rowItem.getUrl();
        final String title = rowItem.getTitle();
        final String img_url = rowItem.getImage_url();
        final String id = rowItem.getID();
        final String description = rowItem.getDescription();

        holder.txtUrl.setText(url);
        holder.txtTitle.setText(title);
        Picasso.with(context).load(img_url).error(R.drawable.placeholder_error).into(holder.imageView);
        holder.txtID.setText(id);
        holder.txtDescription.setText(description);
        holder.addRecipe.setTextSize(14);
        holder.addRecipe.setTypeface(null, BOLD);

        holder.addRecipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (user != null) {
                    if(haveNetworkConnection() == true) {
                        String uid = user.getUid();
                        final DatabaseReference mspecificUser = mUserRef.child(uid+"/recipes/"+id);
                        mspecificUser.child("title").setValue(title);
                        mspecificUser.child("url").setValue(url);
                        mspecificUser.child("image_url").setValue(img_url);
                        mspecificUser.child("description").setValue(description);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                        final String format = simpleDateFormat.format(new Date());

                        mRecipeRef.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                mspecificUser.setValue(snapshot.getValue());
                                mspecificUser.child("timeStamp").setValue(format);
                            }
                            @Override public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        });
                        //new
                        mContentsIngredients.child(id).addValueEventListener(new ValueEventListener() {
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
                        mContentsDirections.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                mspecificUser.child("directions").setValue(snapshot.child("directions").getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        });

                        Toast.makeText(v.getContext(), title+" has been added.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(v.getContext(), "Cannot add. Network connection is unavailable", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), "Enjoy the full Vineyard experience through signing up/signing in.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return convertView;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

}