package com.br.cb2;

import java.util.List;

import com.br.cb2.data.RecipeParse;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

public class CB2App extends Application {
    private static ImageLoader imgDownloader;
	@Override
	public void onCreate() {
		super.onCreate();
		ParseObject.registerSubclass(RecipeParse.class);
		
		Parse.initialize(this, getResources().getString(R.string.parse_app_id), getResources().getString(R.string.parse_client_key));

		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		ParseFacebookUtils.initialize(getResources().getString(R.string.parse_client_key));
		initImageLoader(getApplicationContext());
	}
	public static void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error)
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
									.threadPriority(Thread.NORM_PRIORITY - 2)
									.defaultDisplayImageOptions(options)
									.denyCacheImageMultipleSizesInMemory()
									.discCacheFileNameGenerator(new Md5FileNameGenerator())
									.tasksProcessingOrder(QueueProcessingType.LIFO)
									.writeDebugLogs() // Remove for release app
									.build();
		CB2App.imgDownloader = ImageLoader.getInstance();
		CB2App.imgDownloader.init(config);
		CB2App.getImgDownloader().clearDiscCache();
		CB2App.getImgDownloader().clearMemoryCache();
	}
	public static ImageLoader getImgDownloader() {
        return CB2App.imgDownloader;
    }
}
