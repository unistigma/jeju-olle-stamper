package com.hello.Executerservice;

import android.os.Handler;


public class RunChild implements Runnable {
	Handler handler;
	Item pak;

	public RunChild(Handler handler, Item pk) {
		this.handler = handler;
		this.pak = pk;
	}

	// @Override
	public void run() {
		try {
			Thread.sleep(100);

			if ((pak.mainValue != 0) 
					&& (pak.subValue[0]) != 0
					&& (pak.subValue[1]) != 0
					&& (pak.subValue[2]) != 0
					&& (pak.subValue[3]) != 0
					&& (pak.subValue[4]) != 0) {

				pak.lastKeyValue = Integer.toString(pak.mainValue)
						+ Integer.toString(pak.subValue[0])
						+ Integer.toString(pak.subValue[1])
						+ Integer.toString(pak.subValue[2])
						+ Integer.toString(pak.subValue[3])
						+ Integer.toString(pak.subValue[4]);

			} else {
				pak.lastKeyValue = "noMainValue";
			}

			handler.sendMessage(handler.obtainMessage(RunParent.PAK_DATA, pak));

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}