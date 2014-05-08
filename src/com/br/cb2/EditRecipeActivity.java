package com.br.cb2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.GetCallback;
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
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class EditRecipeActivity extends Activity implements OnClickListener  {
	private Context co;
	private EditText nameText;
	private EditText ingredientsText;
	private EditText instructionsText;
	private Spinner spinner;
	private int position;
	private List<ParseObject> cats;
	//keep track of camera capture intent
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	final int CAMERA_CAPTURE = 3;
	final int PIC_CROP = 4;
	private Uri picUri;
	private List<HashMap<String, String>> catHash = new ArrayList<HashMap<String,String>>();
	public ParseUser currentUser;
	public ParseObject recipe;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		co = this;
		setContentView(R.layout.activity_edit_recipe);

		nameText = (EditText) findViewById(R.id.name);
		ingredientsText = (EditText) findViewById(R.id.ingredients);
		instructionsText = (EditText) findViewById(R.id.instructions);
		spinner = (Spinner) findViewById(R.id.category);
		currentUser = ParseUser.getCurrentUser();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String name = extras.getString("name");
			String rid = extras.getString("rid");

			if (rid != null) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Recipe");
				query.getInBackground(rid, new GetCallback<ParseObject>() {
				  public void done(ParseObject object, ParseException e) {
				    if (e == null) {
				      recipe = object;
				      nameText.setText(object.getString("Name"));
				      instructionsText.setText(object.getString("Description"));
				    } else {
				      // something went wrong
				    }
				  }
				});
			}
			
		}

		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Bundle bundle = new Bundle();
				bundle.putString("name", nameText.getText().toString());
				bundle.putString("ingredients", ingredientsText.getText().toString());
				bundle.putString("instructions", instructionsText.getText().toString());
				int loc = spinner.getSelectedItemPosition();
				HashMap<String,String> ele = catHash.get(loc);
				Log.d("CID", String.valueOf(loc));
				Log.d("CID", (String)ele.get("cid"));
				bundle.putString("category", (String)ele.get("cid"));

				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
        Button captureBtn = (Button)findViewById(R.id.capture_btn);
        //handle button clicks
        captureBtn.setOnClickListener(this);
        new CategoryDataTask().execute();
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
	/**
     * Click method to handle user pressing button to launch camera
     */
    public void onClick(View v) {
        if (v.getId() == R.id.capture_btn) {     
        	try {
	        	//use standard intent to capture an image
	        	Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        	//we will handle the returned data in onActivityResult
	            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        	}
            catch(ActivityNotFoundException anfe){
        		//display an error message
        		String errorMessage = "Whoops - your device doesn't support capturing images!";
        		Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
        		toast.show();
        	}
        }
    }
    
    /**
     * Handle user returning from both capturing and cropping the image
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		final Bundle extras = data.getExtras();

    	if (resultCode == RESULT_OK) {
    		//user is returning from capturing an image using the camera
    		if(requestCode == CAMERA_CAPTURE){
    			//get the Uri for the captured image
    			picUri = data.getData();
    			Bitmap imageReturned = (Bitmap) data.getExtras().get("data");
    			ByteArrayOutputStream blob = new ByteArrayOutputStream();
    			imageReturned.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
    			byte[] bitmapdata = blob.toByteArray();
    			
    			final ParseFile file = new ParseFile("test.png", bitmapdata);
    			//final ParseObject image = new ParseObject("Picture");
    			//recipe.put("M", file);
    			//image.save();
				//image.saveInBackground()
				file.saveInBackground(new SaveCallback() {
						public void done(ParseException e) {
						    // Now let's update it with some new data. In this case, only cheatMode and score
						    // will get sent to the Parse Cloud. playerName hasn't changed.
							  //Toast.makeText(co, "Save E"+e.getMessage(), Toast.LENGTH_LONG).show();
							  //new RemoteDataTask().execute();
							  recipe.put("Mainimage", file);
							  recipe.saveInBackground(new SaveCallback() {
									public void done(ParseException e) {
										Intent i = new Intent(co, RecipeListActivity.class);
								    	startActivityForResult(i, 0);
									}
								});
						 }
					});
    			//carry out the crop operation
    		}
    		//user is returning from cropping the image
    		else if(requestCode == PIC_CROP){
    			//get the returned data
    			//get the cropped bitmap
    			Bitmap thePic = extras.getParcelable("data");
    			ByteArrayOutputStream blob = new ByteArrayOutputStream();
    			thePic.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, blob);
    			byte[] bitmapdata = blob.toByteArray();
    			
    			ParseFile file = new ParseFile("test.png", bitmapdata);
    			ParseObject image = new ParseObject("Pictures");
    			image.put("Image", file);
				//todo.put("Categories", ParseObject.createWithoutData("Category", "NMcNZpO82E"));
				//ParseRelation<ParseObject> relation = new ParseRelation(ParseObject.createWithoutData("Category", "NMcNZpO82E"), "Category");
				//todo.put("Categories", cats.get(0));
				
				try {
					image.save();
				} catch (ParseException e) {
					Log.d("ParseException", e.getLocalizedMessage());
				}
    		} else if(requestCode == ACTIVITY_CREATE){
					String name = extras.getString("name");
					String instructions = extras.getString("instructions");
					String ingredients = extras.getString("ingredients");
					String category = extras.getString("category");
					ParseObject recipe = new ParseObject("Recipe");
					recipe.put("Name", name);
					recipe.put("Instructions", instructions);
					recipe.put("Categories", ParseObject.createWithoutData("Category", category));
					recipe.put("user", currentUser);
					recipe.saveInBackground();
    		} 
    	}
    }
    
    private class CategoryDataTask extends AsyncTask<Void, Void, Void> {
		// Override this method to do custom remote calls
		protected Void doInBackground(Void... params) {
			// Gets the current list of todos in sorted order
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Category");
			query.orderByDescending("_created_at");
			query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
			try {
				cats = query.find();
			} catch (ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			// Put the list of todos into the list view
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditRecipeActivity.this,
			         android.R.layout.simple_spinner_item);
			HashMap<String, String> m = new HashMap<String, String>();
			for (ParseObject cat : cats) {
				adapter.add((String) cat.get("Name"));
				m.put("name", (String) cat.get("Name"));
				m.put("cid", (String) cat.getObjectId());
				catHash.add(m);
			}

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			
		}
	}
    
}
