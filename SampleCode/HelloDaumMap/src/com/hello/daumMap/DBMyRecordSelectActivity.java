package com.hello.daumMap;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class DBMyRecordSelectActivity extends ListActivity {
	
	private DbAdapter mDbHelper;
	
	public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_myrecord_list);
		
		setSimpleCursorAdapter();
	}
	
	private void setSimpleCursorAdapter() {
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
		SimpleCursorAdapter dbData = new SimpleCursorAdapter(this, R.layout.db_myrecord_row, dataCursor, from, to);
		setListAdapter(dbData);
	}
}
