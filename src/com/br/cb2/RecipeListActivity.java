
package com.br.cb2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ListFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;
import android.app.ProgressDialog;
import android.app.Dialog;

import com.br.cb2.data.RecipeParse;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RecipeListActivity extends ListActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView recipeList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mTitle;
    
    private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	public static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private List<ParseObject> recipes;
	private List<ParseObject> cats;
	private Dialog progressDialog;
	private ProgressBar mProgress;

	public static Context co;
	public ParseUser currentUser;
	public String filter;
	public int drawerposition;
	public SimpleAdapter adapter;
	public ListView lv;
	public List<HashMap<String, Object>> navHash;
	ProgressDialog pleaseWaitDialog;
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		// Override this method to do custom remote calls
		protected Void doInBackground(Void... params) {
			// Gets the current list of todos in sorted order
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Recipe");
			query.include("user");
			if(!filter.equals("All")) {
				if(filter.equals("Mine")) {
					query.whereEqualTo("user", currentUser);
				} else {
					query.whereEqualTo("Categories", cats.get(drawerposition));
				}
			}
			query.orderByDescending("_created_at");
			query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
			try {
				recipes = query.find();
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
			currentUser = ParseUser.getCurrentUser();
			// Put the list of todos into the list view

			//ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipeListActivity.this,
				//R.layout.recipe_row);
			navHash =  new ArrayList<HashMap<String,Object>>();
			int i=0;
			for (ParseObject recipe : recipes) {
				//adapter.add((String) recipe.get("Name"));(

				 HashMap<String, Object> m = new HashMap<String, Object>();
				 ParseObject user = recipe.getParseObject("user");
				 m.put("name", (String) recipe.get("Name"));
				 m.put("rid", (String) recipe.getObjectId());
				 m.put("user", (String) user.get("username"));
				 m.put("thumb", R.drawable.action_eating);
	             navHash.add(m);
	             ParseFile mimage = (ParseFile) recipe.get("Mainimage");
	             final int j = i;
	             if (mimage != null) {
		             mimage.getDataInBackground(new GetDataCallback() {
		            	  public void done(byte[] data, ParseException e) {
		            	    if (e == null) {
		            	      // data has the bytes for the resume
		            	    	Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
		            	    	HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(j);
		                        
		                        ImageLoaderTask imageLoaderTask = new ImageLoaderTask();
		                        HashMap<String, Object> hmDownload = new HashMap<String, Object>();
		                        hm.put("thumb",b);
		                        hm.put("position", j);
		         
		                        // Starting ImageLoaderTask to download and populate image in the listview
		                        imageLoaderTask.execute(hm);
		            	    } else {
		            	      // something went wrong
		            	    }
		            	  }
		            	});
	             }
	             i+=1;
			}
			adapter = new SimpleAdapter(co, navHash,R.layout.recipe_row, new String[] {"name","user","thumb"}, new int[] { R.id.item_title, R.id.item_description, R.id.icon});

			adapter.notifyDataSetChanged();
			lv.setAdapter(adapter);
	        lv.setOnItemClickListener(new OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                Context context = view.getContext();
	                HashMap<String,Object> ele = navHash.get(position);
	                String rid = (String)ele.get("rid");
	                Bundle bundle = new Bundle();
	                bundle.putString( "rid",rid);
	                Intent intent=new Intent(RecipeListActivity.this, ViewRecipeActivity.class);
	                intent.putExtras(bundle);
	                context.startActivity(intent);
	            }
	        });
			TextView empty = (TextView) findViewById(android.R.id.empty);
			empty.setVisibility(View.VISIBLE);
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
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipeListActivity.this,
					R.layout.todo_row);
			
			for (ParseObject cat : cats) {
				adapter.add((String) cat.get("Name"));
			}

			adapter.add("Mine");
			adapter.add("All");
			//setListAdapter(adapter);
			mDrawerList.setAdapter(adapter);
		    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
			//RecipeListActivity.this.progressDialog.dismiss();
			//TextView empty = (TextView) findViewById(android.R.id.empty);
			//empty.setVisibility(View.VISIBLE);
		}
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	co = this;
    	filter = "All";
    	drawerposition = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        currentUser = ParseUser.getCurrentUser();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        lv = getListView();
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
       

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	if(drawerposition < cats.size()) {
        	        ParseObject c = cats.get(drawerposition);
        	        setTitle((String) c.get("Name"));
        	        filter = (String) c.get("Name");
                } else if (drawerposition == cats.size()){
        	        setTitle("Mine");
        	        filter = "Mine";
                } else if (drawerposition == (cats.size()+1)){
                	setTitle("All");
        	        filter = "All";
                }
            }

            public void onDrawerOpened(View drawerView) {
            	if(drawerposition < cats.size()) {
        	        ParseObject c = cats.get(drawerposition);
        	        setTitle((String) c.get("Name"));
        	        filter = (String) c.get("Name");
                } else if (drawerposition == cats.size()){
        	        setTitle("Mine");
        	        filter = "Mine";
                } else if (drawerposition == (cats.size()+1)){
                	setTitle("All");
        	        filter = "All";
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            //selectItem(0);
        }
        new RemoteDataTask().execute();
        new CategoryDataTask().execute();
		registerForContextMenu(getListView());
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

			// Delete the remote object
			final ParseObject todo = recipes.get(info.position);

			new RemoteDataTask() {
				protected Void doInBackground(Void... params) {
					try {
						todo.delete();
					} catch (ParseException e) {
					}
					super.doInBackground();
					return null;
				}
			}.execute();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_search:
            Toast.makeText(this, "Search placeholder", Toast.LENGTH_LONG).show();
            return true;
        case R.id.action_compose:
            // create intent to perform web search for this planet
        	createRecipe();
            return true;
        case R.id.action_settings:
            // create intent to perform web search for this planet
        	//Intent i = new Intent(this, SettingsActivity.class);
    		//startActivityForResult(i, ACTIVITY_CREATE);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        mDrawerList.setItemChecked(position, true);
        drawerposition = position;
        if(drawerposition < cats.size()) {
	        ParseObject c = cats.get(drawerposition);
	        setTitle((String) c.get("Name"));
	        filter = (String) c.get("Name");
        } else if (drawerposition == cats.size()){
	        setTitle("Mine");
	        filter = "Mine";
        } else if (drawerposition == (cats.size()+1)){
        	setTitle("All");
	        filter = "All";
        }
    	mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        new RemoteDataTask().execute();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class ArrayListFragment extends ListFragment {

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
    private void createRecipe() {
		Intent i = new Intent(this, CreateRecipeActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		final Bundle extras = intent.getExtras();

		switch (requestCode) {
		case ACTIVITY_CREATE:
			break;
		}
	}
    
    public class RecipeList extends SimpleAdapter {

        private List <RecipeParse> recipes;

        private int[] colors = new int[] {
            0x30ffffff, 0x30808080
        };

        @SuppressWarnings("unchecked")
        public RecipeList(Context context, List <? extends Map < String, String >> recipes,
            int resource,
            String[] from,
            int[] to) {
            super(context, recipes, resource, from, to);
            this.recipes = (List < RecipeParse > ) recipes;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);

            int colorPos = position % colors.length;
            view.setBackgroundColor(colors[colorPos]);
            return view;
        }
    }
    /** AsyncTask to download and load an image in ListView */
    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>>{
 
        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {
 
            InputStream iStream=null;
            Bitmap bm = (Bitmap) hm[0].get("thumb");
            int position = (Integer) hm[0].get("position");
 
            URL url;
            try {
                // Getting Caching directory
                File cacheDirectory = getBaseContext().getCacheDir();
 
                // Temporary file to store the downloaded image
                File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"+position+".png");
 
                // The FileOutputStream to the temporary file
                FileOutputStream fOutStream = new FileOutputStream(tmpFile);
 
                // Writing the bitmap to the temporary file as png file
                bm.compress(Bitmap.CompressFormat.PNG,100, fOutStream);
 
                // Flush the FileOutputStream
                fOutStream.flush();
 
               //Close the FileOutputStream
               fOutStream.close();
 
                // Create a hashmap object to store image path and its position in the listview
                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();
 
                // Storing the path to the temporary image file
                hmBitmap.put("thumb",tmpFile.getPath());
 
                // Storing the position of the image in the listview
                hmBitmap.put("position",position);
 
                // Returning the HashMap object containing the image path and position
                return hmBitmap;
 
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            // Getting the path to the downloaded image
            String path = (String) result.get("thumb");
 
            // Getting the position of the downloaded image
            int position = (Integer) result.get("position");
 
            // Getting adapter of the listview
            SimpleAdapter adapter = (SimpleAdapter ) lv.getAdapter();
 
            // Getting the hashmap object at the specified position of the listview
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
 
            // Overwriting the existing path in the adapter
            hm.put("thumb",path);
 
            // Noticing listview about the dataset changes
            adapter.notifyDataSetChanged();
        }
    }
}