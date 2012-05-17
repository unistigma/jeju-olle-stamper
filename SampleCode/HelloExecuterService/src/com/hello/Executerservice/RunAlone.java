package com.hello.Executerservice;

import android.os.Handler;

public class RunAlone extends Thread {
	
	public static final int RUN_ALONE_DATA = 3;
	
	public static final int DATA_LIMIT = 100000;
	
	Handler mHandler;
	int mData = 0;
	
	public RunAlone(Handler mHandler) {
		super();
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		super.run();
		
		while(mData <= DATA_LIMIT) {
			try {
				Thread.sleep(100);
				mData++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mHandler.sendMessage(mHandler.obtainMessage(RUN_ALONE_DATA, mData, 0));
		}
	}
}
