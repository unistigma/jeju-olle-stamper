package com.hello.daumMap;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class tabMyRecordDBActivity extends ListActivity {
	
	private DbAdapter mDbHelper;
	
	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_list);
		
		fillData();
	}
	
	private void fillData() {
		//////////////////////////////////DB SELECT
		//mDbHelper = new DbAdapter(this);
		//mDbHelper.open();
		//
		//for(int i = 1; i <= DbAdapter.ALL_COURSE_COUNT; i++) {
		//Cursor note = mDbHelper.fetchGeoPoints(i);
		//startManagingCursor(note);
		//
		//String result = "";
		//while(note.moveToNext()){
		//String dbData1 = note.getString(0);
		//String dbData2 = note.getString(1);
		//result += "(" + dbData1 + "," + dbData2 + ")";
		//}
		//}
		////////////////////////////////////////	


		///////////////////////////////SIMPLE_CURSOR_ADAPTER		
		// Get all of the rows from the database and create the item list
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		Cursor dataCursor = mDbHelper.fetchMyRecords();
		startManagingCursor(dataCursor);

		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{DbAdapter.KEY_COURSE_NO, DbAdapter.KEY_FINISHED};

		// and an array of the fields we want to bind those fields to (in this case just text1)
		int[] to = new int[]{R.id.text1, R.id.text2};

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter dbData = new SimpleCursorAdapter(this, R.layout.db_row, dataCursor, from, to);
		setListAdapter(dbData);
		
//		TextView tv = (ListView)findViewById(R.layout.db_row);
//		String temp = tv.getText() + "코스";
//		tv.setText(temp);
//		//tv.append("코스");
	}
}
