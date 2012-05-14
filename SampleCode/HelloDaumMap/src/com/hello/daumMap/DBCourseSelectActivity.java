package com.hello.daumMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class DBCourseSelectActivity extends Activity {

	ExpandableListView mList;
	private DbAdapter mDbHelper;
	
	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);        
		
		setManyItemView(mDbHelper);
//		setExpandableListView();
	}

	/**
	 * ExpandableListView�� DB ������ �����ش�.
	 */
	public void setExpandableListView() {
		setContentView(R.layout.db_expandable); 
		
		mDbHelper = new DbAdapter(this);
		mDbHelper.open(); 

		mList = (ExpandableListView)findViewById(R.id.list);
		List<Map<String, String>> courseData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> courseCoordsData = new ArrayList<List<Map<String, String>>>();

		for (int i = 0; i < GeoPointLoader.ALL_COURSE_COUNT; i++) {
			Map<String, String> course = new HashMap<String, String>();
			course.put("course", String.valueOf(i+1) + String.valueOf("�ڽ�"));
			courseData.add(course);

			List<Map<String, String>> children = new ArrayList<Map<String, String>>();

			Cursor courseCursor = mDbHelper.fetchGeoPoints(i+1); 
			startManagingCursor(courseCursor);

			for (int j = 0; j < courseCursor.getCount(); j++) {
				Map<String, String> coords = new HashMap<String, String>();

				courseCursor.moveToNext();
				String coordsString = "lat:" + courseCursor.getString(1) + ", lng:" + courseCursor.getString(2);
				coords.put("coords", coordsString);
				children.add(coords);
			}
			courseCoordsData.add(children);
		}
		
		mDbHelper.close();
		
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

	/**
	 * ManyAdapter Ŭ������ �̿��ؼ� ��뷮�� ������ ������ �����ش�.
	 * @param dbHelper
	 */
	public void setManyItemView(DbAdapter dbHelper) {
		setContentView(R.layout.db_course_list);
		
		/////////////////////////////////////DB
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		
		Cursor courseCursor = mDbHelper.fetchGeoPoints(); 
		startManagingCursor(courseCursor);
		/////////////////////////////////////
		
		((ListView)findViewById(R.id.list)).setAdapter(new ManyAdapter(this, courseCursor));
		
		mDbHelper.close();
	}
}

class ManyAdapter extends BaseAdapter {
	Context maincon;
	LayoutInflater Inflater;
	
	Cursor mDbCursor;

	public ManyAdapter(Context context, Cursor dbCursor) {
		maincon = context;
		Inflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mDbCursor = dbCursor;
	}

	public int getCount() {
		return mDbCursor.getCount();
	}

	public String getItem(int position) {
		return "" + position;
	}

	public long getItemId(int position) {
		return position;
	}

	// �� �׸��� �� ����
	public View getView(int position, View convertView, ViewGroup parent) {
		String log = "position = " + position + ", ";
		if (convertView == null) {
			convertView = Inflater.inflate(android.R.layout.simple_list_item_1, 
					parent, false);
			log += "convertView is null";
		} else {
			log += "convertView is not null";
		}
		Log.d("ManyItem", log);
		TextView txt = (TextView)convertView.findViewById(android.R.id.text1);
		
		mDbCursor.moveToPosition(position);
		
		txt.setText(mDbCursor.getString(1) +"�ڽ�-lat:" + mDbCursor.getString(2) +
				    " lng:" + mDbCursor.getString(3));
		return convertView;
	}
}
