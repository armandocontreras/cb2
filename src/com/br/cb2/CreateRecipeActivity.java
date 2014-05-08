package com.br.cb2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.br.cb2.ui.CustomButton;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
public class CreateRecipeActivity extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener  {
	private Context co;
	private String selectedCategory;
	private EditText nameText;
	private EditText ingredientsText;
	private EditText instructionsText;
	private Spinner spinner;
	private int position;
	private List<ParseObject> cats;
	//keep track of camera capture intent
	final int CAMERA_CAPTURE = 1;
	//keep track of cropping intent
	final int PIC_CROP = 2;
	//captured picture uri
	private Uri picUri;
	public ParseUser currentUser;
	public int mStackLevel;
	private List<HashMap<String, String>> catHash = new ArrayList<HashMap<String,String>>();
	public void addIngredient(View view) {
		mStackLevel++;

		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    DialogFragment newFragment = CustomDialogFragment.newInstance();
	    newFragment.show(ft, "dialog");

	}
	public void addInstruction(View view) {
		mStackLevel++;

		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    DialogFragment newFragment = CustomDialogFragment.newInstance();
	    newFragment.show(ft, "dialog");
		/*FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    DialogFragment newFragment = CustomDialogFragment.newInstance(mStackLevel);
	    newFragment.show(ft, "dialog");*/

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		co = this;
		setContentView(R.layout.activiy_create_recipe);
		currentUser = ParseUser.getCurrentUser();
		nameText = (EditText) findViewById(R.id.name);
		ingredientsText = (EditText) findViewById(R.id.ingredients);
		instructionsText = (EditText) findViewById(R.id.instructions);
		spinner = (Spinner) findViewById(R.id.category);
		
		Button confirmButton = (Button) findViewById(R.id.confirm);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				int loc = spinner.getSelectedItemPosition();
				HashMap<String,String> ele = catHash.get(loc);
				Log.d("CID", (String)ele.get("cid"));
				
				final ParseObject recipe = new ParseObject("Recipe");
				recipe.put("Name", nameText.getText().toString());
				recipe.put("Description", instructionsText.getText().toString());
				//recipe.put("Ingredients", ingredients);
				recipe.put("Categories", ParseObject.createWithoutData("Category", (String)ele.get("cid")));
				recipe.put("user", currentUser);
				
				final ParseObject ingredient1 = new ParseObject("Ingredient");
				ingredient1.put("Measurement", "cup");
				ingredient1.put("Qty", "1"); 
				ingredient1.put("Name", ingredientsText.getText().toString()); 
				//ingredient1.put("Ingredients", recipe);
				ingredient1.put("parent", recipe);
				ingredient1.saveInBackground(new SaveCallback() {
					public void done(ParseException e) {
						  ParseRelation<ParseObject> relation = recipe.getRelation("Ingredients");
						  relation.add(ingredient1);
						  recipe.saveInBackground(new SaveCallback() {
							  public void done(ParseException e) {
								  setInstructions(recipe);
							  }
							});
					  }
				});
			}
		});
		
		
        new CategoryDataTask().execute();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
	public void setInstructions(ParseObject recipe) {
		
		final ParseObject instruction = new ParseObject("Instruction");
		instruction.put("Description", instructionsText.getText().toString());
		final ParseObject rec = recipe;
		instruction.put("parent", recipe);
		instruction.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				  ParseRelation<ParseObject> relation = rec.getRelation("Instructions");
				  relation.add(instruction);
				  rec.saveInBackground(new SaveCallback() {
					  public void done(ParseException e) {
						  	String rid = rec.getObjectId();
			                Bundle bundle = new Bundle();
			                bundle.putString( "rid",rid);
			                Intent intent=new Intent(CreateRecipeActivity.this, EditRecipeActivity.class);
			                intent.putExtras(bundle);
			                co.startActivity(intent);
					  }
					});
			  }
		});
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
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateRecipeActivity.this,
			         android.R.layout.simple_spinner_item);
			
			for (ParseObject cat : cats) {
				HashMap<String, String> m = new HashMap<String, String>();
				adapter.add((String) cat.get("Name"));
				m.put("name", (String) cat.get("Name"));
				m.put("cid", (String) cat.getObjectId());
				catHash.add(m);
			}

			// Apply the adapter to the spinner
			//SimpleAdapter adapter = new SimpleAdapter(co, catHash,android.R.layout.simple_spinner_item, new String[] {"name"}, new int[] { R.id.item_title});
	        //getListView().setAdapter(adapter);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
		}
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long arg3) {
		
		selectedCategory= parent.getItemAtPosition(pos).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static class CustomDialogFragment extends DialogFragment {
        static CustomDialogFragment newInstance() {
            return new CustomDialogFragment();
        }
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            setStyle( DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light);
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.ingredients_overlay, container, false);
            CustomButton iv = (CustomButton) v.findViewById(R.id.submit_button);

    		final EditText emailb = (EditText) v.findViewById(R.id.emailtext);
    		
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                	Editable ed = emailb.getText();
                	String a = ed.toString();
                	
                	CustomDialogFragment.this.dismiss();
                }
            });
            this.setHasOptionsMenu(true);
            this.setMenuVisibility(true);
            this.setCancelable(true);
            //View tv = v.findViewById(R.id.imageOverlay);
            //((ImageView)tv).setText("This is an instance of MyDialogFragment");
            return v;
        }
    }
	

}
