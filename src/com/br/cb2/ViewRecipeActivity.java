package com.br.cb2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRecipeActivity extends Activity  {
	private Context co;
	private TextView nameText;
	private TextView ingredientsText;
	private TextView instructionsText;
	private TextView categoryText;
	private TextView spinner;
	private Button writeReview;
	private Button viewReviews;
	private ImageView pic;
	private int position;
	public ParseUser currentUser;
	public ParseObject recipe;
	public String recipeId;
	public void createReview(View view) {
        Bundle bundle = new Bundle();
        bundle.putString( "rid",recipeId);
        Log.d("Reci", recipeId);
        Intent intent=new Intent(co, CreateReviewActivity.class);
        intent.putExtras(bundle);
        co.startActivity(intent);
		
	}
	public void viewReviews(View view) {
		Intent i = new Intent(co, ReviewListActivity.class);
		startActivityForResult(i, 0);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		co = this;
		setContentView(R.layout.activity_view_recipe);

		nameText = (TextView) findViewById(R.id.name);
		ingredientsText = (TextView) findViewById(R.id.ingredients);
		instructionsText = (TextView) findViewById(R.id.instructions);
		categoryText = (TextView) findViewById(R.id.category);
		pic = (ImageView) findViewById(R.id.picture);
		currentUser = ParseUser.getCurrentUser();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String rid = extras.getString("rid");
			recipeId = rid;
			if (rid != null) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
				query.getInBackground(rid, new GetCallback<ParseObject>() {
				  public void done(ParseObject object, ParseException e) {
				    if (e == null) {
				      recipe = object;
				      nameText.setText(object.getString("Name"));
				      ParseFile mimage = (ParseFile) recipe.get("Mainimage");
				      mimage.getDataInBackground(new GetDataCallback() {
		            	  public void done(byte[] data, ParseException e) {
		            	    if (e == null) {
		            	    	 Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		            	         pic.setImageBitmap(bitmap);
		            	         ParseQuery<ParseObject> q = ParseQuery.getQuery("Ingredient");
		         				 q.whereEqualTo("parent", recipe);
		         				 q.findInBackground(new FindCallback<ParseObject>() {
		         					        public void done(List<ParseObject> ingredientsList, ParseException e) {
		         					        	if (ingredientsList != null) {
			         					        	for (int i =0; i < ingredientsList.size() ; i++) {
			         				        			ingredientsText.setText(ingredientsList.get(i).getString("Qty")+ " " +ingredientsList.get(i).getString("Measurement")+ " "+ingredientsList.get(i).getString("Name"));
			         				        		}
		         					        	}
		         					        }
		         				 });
		         				 
		         				ParseQuery<ParseObject> q2 = ParseQuery.getQuery("Instruction");
		        			    q2.whereEqualTo("parent", recipe);
		        			    q2.findInBackground(new FindCallback<ParseObject>() {
		        					  public void done(List<ParseObject> instructionList, ParseException e) {
		        					          // commentList now has the comments for myPost
		        					        	if (instructionList != null) {
		        					        		for (int i =0; i < instructionList.size() ; i++) {
		        					        			instructionsText.setText(instructionList.get(i).getString("Description"));
		        					        		}
		        					        	}
		        					  }
		        				});
		            	    }
		            	  }
				      });
				      
				    }
				  }
				});
				 
			}
			
		}

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch(item.getItemId()) {
        case android.R.id.home:
        	Intent i = new Intent(this, RecipeListActivity.class);
    		startActivityForResult(i, 0);
            return true;
        default:
        	return true;
        }
    }
}
