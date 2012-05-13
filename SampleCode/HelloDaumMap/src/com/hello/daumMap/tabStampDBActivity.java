package com.hello.daumMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class tabStampDBActivity extends Activity {
	
	ExpandableListView mList;
	private DbAdapter mDbHelper;
	
	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.db_expandable); 
		
		////////////////////////////////////////////////////DB OPEN
		mDbHelper = new DbAdapter(this);
        mDbHelper.open(); 
		/////////////////////////////////////////////////////////////
		
		/////////////////////////////////////////////////////EXPANDABLE LIST ADAPTER
		mList = (ExpandableListView)findViewById(R.id.list);
		List<Map<String, String>> courseData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> courseCoordsData = new ArrayList<List<Map<String, String>>>();
		
		for (int i = 0; i < GeoPointLoader.ALL_COURSE_COUNT; i++) {
			Map<String, String> course = new HashMap<String, String>();
			course.put("course", String.valueOf(i+1) + String.valueOf("ÄÚ½º ½ºÅÆÇÁ"));
			courseData.add(course);

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			
			/////////////////////////////DB
			Cursor courseCursor = mDbHelper.fetchStampPoints(i+1); 
			startManagingCursor(courseCursor);
			////////////////////////////////
			for (int j = 0; j < courseCursor.getCount(); j++) {

				Map<String, String> coords = new HashMap<String, String>();

				////////////////////////////////DB
				courseCursor.moveToNext();
				String coordsString = "lat:" + courseCursor.getString(1) + ", lng:" + courseCursor.getString(2);
				/////////////////////////////
				coords.put("coords", coordsString);
				children.add(coords);
			}
			courseCoordsData.add(children);
		}
		///////////////////////DB CLOSE
		mDbHelper.close();
		/////////////////////////

		ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
				this,
				courseData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { "course" },
				new int[] { android.R.id.text1 },
				courseCoordsData,
				android.R.layout.simple_expandable_list_item_1,
				new String[] { "coords" },
				new int[] { android.R.id.text1 }
		);
		mList.setAdapter(adapter);   
	}
}
