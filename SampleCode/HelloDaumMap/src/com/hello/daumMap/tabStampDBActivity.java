package com.hello.daumMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class tabStampDBActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);        
		TextView textview = new TextView(this);        
		textview.setText("This is the Stamp DB tab");        
		setContentView(textview);    
	}
}
