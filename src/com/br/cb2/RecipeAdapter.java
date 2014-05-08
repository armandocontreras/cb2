package com.br.cb2;

import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.br.cb2.data.RecipeParse;

public class RecipeAdapter extends ParseQueryAdapter<RecipeParse> {
	private LayoutInflater layoutInflater;
	private Context co;
	public RecipeAdapter(Context context) {
		
		super(context, new ParseQueryAdapter.QueryFactory<RecipeParse>() {
			public ParseQuery<RecipeParse> create() {
				// Here we can configure a ParseQuery to display
				// only top-rated meals.
				ParseQuery query = new ParseQuery("Recipe");
				return query;
			}
		});
		co = context;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getItemView(RecipeParse recipe, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.parse_recipe_item, null);
		}

		super.getItemView(recipe, v, parent);

		ImageView recipeImage = (ImageView) v.findViewById(R.id.icon);
		Drawable def = co.getResources().getDrawable(R.drawable.ic_empty);
		recipeImage.setImageDrawable(def);
		ParseFile photoFile = (ParseFile) recipe.getParseFile("Mainimage");
		if (photoFile != null) {
			try {
				Log.d("Image", photoFile.getUrl());
  	    	   CB2App.getImgDownloader().displayImage(photoFile.getUrl(), recipeImage);
			} catch (Exception e ) {
				//Log.e("DownloadImgException", e.getMessage());
			}
		}

		TextView titleTextView = (TextView) v.findViewById(R.id.text1);
		titleTextView.setText(recipe.getName());
		TextView ratingTextView = (TextView) v
				.findViewById(R.id.servings);
		ratingTextView.setText(recipe.getServings());
		TextView oid = (TextView) v
				.findViewById(R.id.oid);
		oid.setText(recipe.getObjectId());
		/*ViewHolder holder;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.parse_recipe_item, null);
            holder = new ViewHolder();
            holder.itemName = (TextView) v.findViewById(R.id.text1);
            holder.itemDescription = (TextView) v.findViewById(R.id.text2);
            holder.itemServings = (TextView) v.findViewById(R.id.text2);
            holder.imageView = (ImageView) v.findViewById(R.id.icon);
            Drawable def = co.getResources().getDrawable(R.drawable.ic_empty);
            holder.imageView.setImageDrawable(def);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
            Drawable def = co.getResources().getDrawable(R.drawable.ic_empty);
            holder.imageView.setImageDrawable(def);
        }
        Store s = (Store) listData.get(position);
        holder.itemName.setText(s.getName());
        holder.itemPhone.setText(s.getPhone());
        holder.itemAddress.setText(s.getAddress());
        BRApplication.getImgDownloader().displayImage(s.getStore_logo_url(), holder.imageView);
        return convertView;*/
		return v;
	}
	static class ViewHolder {
    	TextView itemName;
        TextView itemDescription;
        TextView itemServings;
        ImageView imageView;
        public int position;
    }
}
