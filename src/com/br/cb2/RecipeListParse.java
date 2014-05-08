package com.br.cb2;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout;

import com.br.cb2.data.RecipeParse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class RecipeListParse extends ListActivity {
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView recipeList;
    private ActionBarDrawerToggle mDrawerToggle;
	private ParseQueryAdapter<RecipeParse> mainAdapter;
	public int drawerposition;
	private List<ParseObject> cats;
	public ListView lv;
	public static Context co;
	public ParseUser currentUser;
	public String filter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		co = this;
    	filter = "All";
    	drawerposition = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        currentUser = ParseUser.getCurrentUser();
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        lv = getListView();
		mainAdapter = new RecipeAdapter(this);

		// Default view is all meals
		setListAdapter(mainAdapter);
        //mainAdapter = new ParseQueryAdapter<Recipe>(this, Recipe.class);
        //mainAdapter.setTextKey("Name");
        //mainAdapter.setImageKey("Mainimage");
        //mainAdapter.
        //setListAdapter(mainAdapter);
		setOnclickActions();
		new CategoryDataTask().execute();
		registerForContextMenu(getListView());
	}
	
	public void setOnclickActions() {
		lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                View v = lv.getAdapter().getView(position, null, null);
                TextView otextview = (TextView) v.findViewById(R.id.oid);
                String rid = otextview.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString( "rid",rid);
                Intent intent=new Intent(RecipeListParse.this, ViewRecipeActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app_actions, menu);
		return true;
	}

	/*
	 * Posting meals and refreshing the list will be controlled from the Action
	 * Bar.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh: {
			updateRecipeList();
			break;
		}
		case R.id.action_new: {
			newMeal();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateRecipeList() {
		mainAdapter.loadObjects();
		setListAdapter(mainAdapter);
		setOnclickActions();
	}

	private void newMeal() {
		Intent i = new Intent(this, CreateRecipeActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			updateRecipeList();
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
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipeListParse.this,
					R.layout.todo_row);
			
			for (ParseObject cat : cats) {
				adapter.add((String) cat.get("Name"));
			}

			adapter.add("Mine");
			adapter.add("All");
			//setListAdapter(adapter);
			mDrawerList.setAdapter(adapter);
			//RecipeListActivity.this.progressDialog.dismiss();
			//TextView empty = (TextView) findViewById(android.R.id.empty);
			//empty.setVisibility(View.VISIBLE);
		}
	}
}