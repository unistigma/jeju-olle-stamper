package com.prime.Factorizer;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrimeFactorizerActivity extends Activity {
	
	private static final String TAG = PrimeFactorizerActivity.class.toString();
	private static final int DIALOG_EXCEPTION_ERROR = 100;
	
	List<Long> primeFactor = new ArrayList<Long> ();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case RunPrimeFactorizer.ACCUMULATE_COMPLETE:
				// 스레드로 부터 소인수 리스트를 받음
				primeFactor = (List<Long>) msg.obj;
				
				// 결과값 출력
	        	String primeFactors = "";
	        	for(long r : primeFactor) {
		        	Log.i("primeFactor : ", Long.toString(r));
		        	primeFactors += Long.toString(r) + ",";
	        	}
	        	TextView tvResult = (TextView) findViewById(R.id.resultValue);
	        	tvResult.setText(primeFactors);
				break;
			case RunPrimeFactorizer.THREAD_ERROR:
				showDialog(DIALOG_EXCEPTION_ERROR);
				Exception e = (Exception) msg.obj;
				Log.w(TAG, e);
				break;
			}
			
			RelativeLayout rl = (RelativeLayout)findViewById(R.id.RL);
    		rl.setVisibility(rl.VISIBLE);
        	
        	ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
        	pb.setVisibility(pb.GONE);
		}
	};
    
    public void mOnClick_spec1(View v) {
    	TextView tvInput = (TextView) findViewById(R.id.inputValue);
    	Button tvSpec1 = (Button)findViewById(R.id.specificValue1);
    	tvInput.setText(tvSpec1.getText());
    }
    
    public void mOnClick_spec2(View v) {
    	TextView tvInput = (TextView) findViewById(R.id.inputValue);
    	Button tvSpec2 = (Button)findViewById(R.id.specificValue2);
    	tvInput.setText(tvSpec2.getText());
    }
    
    public void mOnClick_clear(View v) {
    	TextView tvInput = (TextView) findViewById(R.id.inputValue);
    	tvInput.setText("");
    }
    
    public void mOnClick(View v) {
    	TextView tv = (TextView) findViewById(R.id.inputValue);
    	
    	if(0 == tv.getText().length()) {
    		Toast.makeText(this, "숫자를 입력해주세요", 1).show();
    	} else {
        	//0. 입력값 받음 & 초기화
    		long inputNumber = Long.parseLong(tv.getText().toString());
    		
    		primeFactor.clear();
    		
    		RelativeLayout rl = (RelativeLayout)findViewById(R.id.RL);
    		rl.setVisibility(rl.GONE);

        	ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
        	pb.setVisibility(pb.VISIBLE);
        	
        	//1. 계산 스레드 시작
        	RunPrimeFactorizer rpf = new RunPrimeFactorizer(mHandler, inputNumber);
    		new Thread(rpf).start();
    	}
	}
}

	
