package com.hello.daumMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BootActivity extends Activity {

	public static final int BOOT_IMAGE_SHOW_TIME = 1200;
	
	ManagerThread mThread;
	ProgressDialog mProgress;
	
	Handler mAfterThread = new Handler() {
		public void handleMessage(Message msg) {
			mProgress.dismiss();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.boot);
				
        showProgressAndStartThread();	
	}
	
	/**
	 * ���α׷��� ���̾�α׸� �����Ͽ� show ���� �����带 �����Ѵ�.
	 */
	public void showProgressAndStartThread() {

		mProgress = new ProgressDialog(this);
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setTitle("Calculating");
		mProgress.setMessage("Wait...");
		mProgress.setCancelable(false);
		mProgress.show();

		//Create Thread !!
		mThread = new ManagerThread(mAfterThread, this);
		mThread.start();
	}
	
	/**
	 * ������ �۾������带 �����ϰ� Activity, ���̾�α׸� �ݱ� ���� �ڵ鷯 �޽����� ������ �θ� ������ Ŭ����
	 * @author tasmania
	 *
	 */
	class ManagerThread extends Thread {
		int mResult;
		Handler mAfter;
		Context mCtx;

		ManagerThread(Handler after, Context ctx) {
			mAfter = after;
			mCtx = ctx;
		}

		public void run() {
			//do work
			doMyWork();
			
			//close progress dialog.
			mAfter.sendEmptyMessage(0);	
			
			//after BOOT_IMAGE_SHOW_TIME(ms), finish TitleActivity
			mAfter.postDelayed(new Runnable() {             
				public void run() {   
					setResult(RESULT_OK);
					finish();
				}
			}
			, BOOT_IMAGE_SHOW_TIME);
		}
		
		public void doMyWork() {
			//DbAdapter ������ ��ü �����ϰ� ����.
			DbAdapter mDbHelper = new DbAdapter(mCtx);
			mDbHelper.start();
			
			//DbAdapter ������ ��ü�� ��������� �������� ���鼭 ��ٸ���.
			while(mDbHelper.isAlive()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					;
				}
			}
		}
	}

	

}




