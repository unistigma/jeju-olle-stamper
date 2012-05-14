package com.hello.daumMap;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

public class TabManageActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);    
    	setContentView(R.layout.db_tabwidget);
		
    	Resources res = getResources();
    	
    	// Resource object to get Drawables    
    	TabHost tabHost = getTabHost();
    	
    	// The activity TabHost
    	TabHost.TabSpec spec; 
    	
    	// Resusable TabSpec for each tab    
    	Intent intent;
    	
    	// Reusable Intent for each tab    
    	// Create an Intent to launch an Activity for the tab (to be reused)    
    	intent = new Intent().setClass(this, DBMyRecordSelectActivity.class);
    	
    	// Initialize a TabSpec for each tab and add it to the TabHost    
    	spec = tabHost.newTabSpec("myrecords").setIndicator("MyRecords",                      
    			res.getDrawable(R.drawable.ic_tab_myrecords)).setContent(intent);    
    	tabHost.addTab(spec);
    	
    	// Do the same for the other tabs    
    	intent = new Intent().setClass(this, DBCourseSelectActivity.class);    
    	spec = tabHost.newTabSpec("courses").setIndicator("Courses", 
    			res.getDrawable(R.drawable.ic_tab_courses)).setContent(intent);    
    	tabHost.addTab(spec);
    	
    	intent = new Intent().setClass(this, DBStampSelectActivity.class);    
    	spec = tabHost.newTabSpec("stamps").setIndicator("Stamps",                      
    			res.getDrawable(R.drawable.ic_tab_stamps)).setContent(intent);    
    	tabHost.addTab(spec);
    	
    	tabHost.setCurrentTab(1);
    	/////////////////////////////////////////////////////////////////////////////////

	}
	
	public void mOnClick(View v) {
		setResult(RESULT_OK);
		finish();
	}

}
