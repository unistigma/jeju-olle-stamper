package com.hello.Json;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

public class QueryAsync extends QueryHandler implements Runnable {
	private static final String TAG = QueryAsync.class.toString();
	private final ExecutorService pool;
	private Handler handler;
	public static final int POI_DATA = 0;
	public static final int QUERYASYNC_ERROR = 1;
	public static final int QUERYASYNC_NODATA = 2;
	private static final int THREAD_POOL_SIZE = 5;
	private boolean alive;

	public synchronized boolean isAlive() {
		return alive;
	}

	public synchronized void setAlive(boolean alive) {
		this.alive = alive;
	}

	public QueryAsync(Context context, Location loc, String keyword, Handler handler) {
		super(context, loc, keyword);
		
		alive = true;
		this.handler = handler;
		pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	// @Override
	public void run() {
		Log.i(TAG, "Start");
		try {
			LocalAgent agent = new LocalAgent(getContext(), getLocation(), getKeyword());
			List<PointOfInterest> results;
			
			if((null != (results = agent.getSearchResults())) && results.size() > 0) {
				for (PointOfInterest r : results) {
					Log.i(TAG, "before pool execute");
					pool.execute(new ImageExtractor(handler, r));
					Log.i(TAG, "after pool execute");
				}
			} else {
				pool.shutdown();
				handler.sendMessage(handler.obtainMessage(QUERYASYNC_NODATA));
			}
//			int counter = 0;
//			while (alive && (counter < LocalAgent.POI_LIMIT)
//						 && ((results = agent.getSearchResults()) !=  null)
//						 && results.size() > 0) {				
//				for (PointOfInterest r : results) {
//					pool.execute(new ImageExtractor(handler, r));
//					counter++;
//				}
//			}
//			if (counter == 0) {
//				pool.shutdown();
//				handler.sendMessage(handler.obtainMessage(QUERYASYNC_NODATA));
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pool.shutdown();
			handler.sendMessage(handler.obtainMessage(QUERYASYNC_ERROR, e));
		}
		Log.i(TAG, "Finished");
	}

	protected class ImageExtractor implements Runnable {
		Handler handler;
		PointOfInterest poi;

		public ImageExtractor(Handler handler, PointOfInterest poi) {
			this.handler = handler;
			this.poi = poi;
		}

		// @Override
		public void run() {
			Log.i(TAG, "ImageExtractor Start");
			URL url = poi.getPhotoUrl();
			if (url != null) {
				try {
					Log.i("Image Request", url.toString());
					InputStream is = (InputStream) url.getContent();

					if (is != null) {
						Drawable drw = Drawable.createFromStream(is, url
								.toExternalForm());
						poi.setDrawable(drw);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				poi.setDrawable(getContext().getResources().getDrawable(
						R.drawable.noimage));
			}
			if(handler.sendMessage(handler.obtainMessage(POI_DATA, poi)))
				Log.i(TAG, "sendMessage Success!!");
			else
				Log.i(TAG, "sendMessage Failed!!");
			Log.i(TAG, "ImageExtractor Finished");
		}
	}
}
