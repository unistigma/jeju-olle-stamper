package com.hello.Executerservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.util.Log;

public class RunParent extends Thread {
	private final String TAG = RunParent.class.toString();
	
	private static final int THREAD_POOL_SIZE = 7;
	private static final int LIST_SIZE = 20;
	
	public static final int PAK_DATA = 0;
	public static final int QUERYASYNC_ERROR = 1;
	public static final int QUERYASYNC_NODATA = 2;
	
	public static int runCount = 1;
	
	int mBackValue = 0;
	Handler mHandler;
	private final ExecutorService pool;

	RunParent(Handler handler) {
		mHandler = handler;
		pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	public void run() {
		Log.i(TAG, "Start");
		
		try {
			List<Item> results;

			if((null != (results = getResults())) && (results.size() > 0)){
				for (Item p : results) {
					pool.execute(new RunChild(mHandler, p));
				}
				pool.shutdown();	//TEST
			} else {
				pool.shutdown();
				mHandler.sendMessage(mHandler.obtainMessage(QUERYASYNC_NODATA));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pool.shutdown();
			mHandler.sendMessage(mHandler.obtainMessage(QUERYASYNC_ERROR, e));
		}

		Log.i(TAG, "Finish");
	}	
	
	public List<Item> getResults() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Item> pList = new ArrayList<Item>(LIST_SIZE);
		
		for(int i = 0; i < LIST_SIZE; i++) {
			Item tempPak = new Item();
			tempPak.mainValue = runCount++;
			for(int j = 0; j < 5; j++) {
				tempPak.subValue[j] = (i*2) + j;
			}
			pList.add(tempPak);
		}
		
		return pList;
	}
}