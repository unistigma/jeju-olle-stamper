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
	 * 프로그래스 다이얼로그를 생성하여 show 한후 스레드를 시작한다.
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
	 * 별도의 작업스레드를 생성하고 Activity, 다이얼로그를 닫기 위한 핸들러 메시지를 보내는 부모 스레드 클래스
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
			//DbAdapter 스레드 객체 생성하고 시작.
			DbAdapter mDbHelper = new DbAdapter(mCtx);
			mDbHelper.start();
			
			//DbAdapter 스레드 객체가 살아있으면 루프문을 돌면서 기다린다.
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




