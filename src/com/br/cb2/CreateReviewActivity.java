package com.br.cb2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateReviewActivity extends Activity   {
	private Context co;
	private String selectedCategory;
	private EditText nameText;
	private EditText reviewText;
	private EditText rateText;
	private int position;
	private ParseObject recipe;
	
	private Uri picUri;
	public ParseUser currentUser;
	private List<HashMap<String, String>> catHash = new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		co = this;
		setContentView(R.layout.activity_write_review);
		currentUser = ParseUser.getCurrentUser();
		reviewText = (EditText) findViewById(R.id.review_text);
		rateText = (EditText) findViewById(R.id.rate_text);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String rid = extras.getString("rid");

			if (rid != null) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
				query.getInBackground(rid, new GetCallback<ParseObject>() {
				  public void done(ParseObject object, ParseException e) {
				    if (e == null) {
				      recipe = object;
				    }
				  }
				});
			}
			
		}
		Button confirmButton = (Button) findViewById(R.id.save);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				final ParseObject review = new ParseObject("Review");
				review.put("Content", reviewText.getText().toString());
				review.put("Rating", Integer.parseInt(rateText.getText().toString()));
				review.put("user", currentUser);
				review.put("parent", recipe);
				review.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						  if(e != null) {
							  Log.e("SaveReview", e.toString());
						  }
						  ParseRelation<ParseObject> relation = recipe.getRelation("Reviews");
						  relation.add(review);
						  recipe.saveInBackground(new SaveCallback() {
							  public void done(ParseException e) {
								  Intent i = new Intent(co, RecipeListActivity.class);
						    	  startActivityForResult(i, 0);
							  }
						  });
					  }
				});
			}
		});
		
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
