package com.hello.daumMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class tabCourseDBActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);        
		TextView textview = new TextView(this);        
		textview.setText("This is the Course DB tab");        
		setContentView(textview);    
	}
}
