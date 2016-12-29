package com.example.vineyard_2;

import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeListAdapterHome extends BaseAdapter {
    Context context;
    List<Recipes> rowItems;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("users");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public RecipeListAdapterHome (Context context, List<Recipes> items ) {
        this.context = context;
        this.rowItems = items;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtUrl;
        TextView txtID;
        Button addRecipe;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        RecipeListAdapterHome.ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_list_home, null);
            holder = new RecipeListAdapterHome.ViewHolder();
            holder.txtUrl = (TextView) convertView.findViewById(R.id.recipe_url);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.recipe_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.txtID = (TextView) convertView.findViewById(R.id.recipe_key);
            holder.addRecipe = (Button) convertView.findViewById(R.id.add);
            convertView.setTag(holder);
        }
        else {
            holder = (RecipeListAdapterHome.ViewHolder) convertView.getTag();
        }

        final Recipes rowItem = (Recipes) getItem(position);

        final String url = rowItem.getUrl();
        final String title = rowItem.getTitle();
        final String img_url = rowItem.getImage_url();
        final String id = rowItem.getID();

        holder.txtUrl.setText(url);
        holder.txtTitle.setText(title);
        Picasso.with(context).load(img_url).error(R.drawable.placeholder_error).into(holder.imageView);
        holder.txtID.setText(id);

        holder.addRecipe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String uid = user.getUid();
                DatabaseReference mUserRecipe = mUserRef.child(uid+"/recipes");
                mUserRecipe.child(id).removeValue();
                rowItems.remove(rowItem);
                RecipeListAdapterHome.this.notifyDataSetChanged();
                Toast.makeText(v.getContext(), title+" removed.", Toast.LENGTH_SHORT).show();
            }
        });

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
