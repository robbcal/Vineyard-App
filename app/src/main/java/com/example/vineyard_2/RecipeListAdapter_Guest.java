package com.example.vineyard_2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.List;

public class RecipeListAdapter_Guest extends BaseAdapter {
    Context context;
    List<Recipes> rowItems;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mRecipeRef = mRootRef.child("recipes");
    DatabaseReference mUserRef = mRootRef.child("users");
    private static final String TAG = "Vineyard";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private Typeface typeFace;

    public RecipeListAdapter_Guest ( Context context, List<Recipes> items ) {
        this.context = context;
        this.rowItems = items;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtUrl;
        TextView txtID;
        TextView txtDescription;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        typeFace = Typeface.createFromAsset(context.getAssets(), "HelveticaNeueLight.ttf");

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_list_guest, null);
            holder = new ViewHolder();
            holder.txtUrl = (TextView) convertView.findViewById(R.id.recipe_url);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.recipe_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtDescription = (TextView) convertView.findViewById(R.id.recipe_description);
            holder.txtID = (TextView) convertView.findViewById(R.id.recipe_key);

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

        //set font typeface
        holder.txtTitle.setTypeface(typeFace);
        holder.txtDescription.setTypeface(typeFace);

        return convertView;
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