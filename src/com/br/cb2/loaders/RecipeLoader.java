package com.br.cb2.loaders;

import java.util.ArrayList;

import com.br.cb2.data.Recipe;
import com.br.cb2.data.RecipeParse;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class RecipeLoader extends AsyncTaskLoader<ArrayList<RecipeParse>> {
	
	private static final String TAG = "Loader";
	
	private int mSize;
	private String mId;
	private boolean mIsSuccess = true;

	private boolean mIsFinishedLoading;
	
	private boolean mSimulateNetworkDelay = true;

    private boolean mFirstLoad;

	public RecipeLoader(Context context, int size) {
		super(context);
		mSize = size;

	}
	
	
	@Override
	public void forceLoad() {
	    Log.v(TAG, mId + ": forceLoad()");
	    super.forceLoad();	    
	}
	
	@Override
	protected void onReset() {
	    Log.v(TAG, mId + ": onReset()");
	    super.onReset();
	}
	
	@Override
	protected void onStartLoading() {
	    Log.v(TAG, mId + ": onStartLoading()");
	    super.onStartLoading();
	    if (mFirstLoad) {
	        forceLoad();
	        mFirstLoad = false;
	    }
	}
	
	@Override
	protected void onStopLoading() {
	    Log.v(TAG, mId + ": onStopLoading()");
	    super.onStopLoading();    
	}
	
	@Override
	public boolean takeContentChanged() {
	    Log.v(TAG, mId + ": takeContentChanged()");
	    return super.takeContentChanged();
	}

	@Override
	public ArrayList<RecipeParse> loadInBackground() {
		
		Log.v(TAG, mId + ": loadInBackground() -- Loading new data");
		
		// Send an update callback
		deliverResult(null);
		
		ArrayList<RecipeParse> names = new ArrayList<RecipeParse>();
		
		
		if (mSimulateNetworkDelay) {
			try {
			    Log.v(TAG, mId + ": delaying load");
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		return names;
	}
	
	@Override
	public void deliverResult(ArrayList<RecipeParse> data) {
		if (data != null) {
			mIsFinishedLoading = true;
		}
		super.deliverResult(data);
	}
	
	public boolean isFinished() {
		return mIsFinishedLoading;
	}
	
	public boolean isSuccess() {
		return mIsSuccess;
	}

}
