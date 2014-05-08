
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
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import java.util.List;
import android.app.ProgressDialog;
import android.app.Dialog;

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

public class ReviewListActivity extends ListActivity {
    private DrawerLayout mDrawerLayout;
    private ListView reviewList;

    private CharSequence mTitle;

	private List<ParseObject> reviews;
	public static Context co;
	public ParseUser currentUser;
	public String filter;
	public SimpleAdapter adapter;
	public ListView lv;
	public List<HashMap<String, Object>> navHash;
	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		// Override this method to do custom remote calls
		protected Void doInBackground(Void... params) {
			// Gets the current list of todos in sorted order
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
			query.include("user");
			query.orderByDescending("_created_at");
			query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
			try {
				reviews = query.find();
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
			
			navHash =  new ArrayList<HashMap<String,Object>>();
			int i=0;
			for (ParseObject review : reviews) {
				//adapter.add((String) recipe.get("Name"));(
				 HashMap<String, Object> m = new HashMap<String, Object>();
				 ParseObject user = review.getParseObject("user");
				 m.put("content", (String) review.get("Content"));
				 String rating = "";
				 try{
					int r = (Integer) review.get("Rating");
					rating = String.valueOf(r);
				 } catch (Exception e) {}
				 m.put("rating", rating);
				 m.put("user", (String) user.get("username"));
	             navHash.add(m);
	           
			}
			adapter = new SimpleAdapter(co, navHash,R.layout.reviews_row, new String[] {"content","rating","user"}, new int[] { R.id.item_description, R.id.rate, R.id.username});

			adapter.notifyDataSetChanged();
			getListView().setAdapter(adapter);
	       
			TextView empty = (TextView) findViewById(android.R.id.empty);
			empty.setVisibility(View.VISIBLE);
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	co = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);
        currentUser = ParseUser.getCurrentUser();
        

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            //selectItem(0);
        }
        new RemoteDataTask().execute();
		registerForContextMenu(getListView());
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
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		final Bundle extras = intent.getExtras();

	}
    
 
}