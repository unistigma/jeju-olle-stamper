/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hello.daumMap;

import java.util.List;

import net.daum.mf.map.api.MapPoint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class DbAdapter {

	public static final int ALL_COURSE_COUNT = 17;
	public static final String KEY_COURSE_NO = "course_no";
	public static final String KEY_FINISHED = "finished";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_MID_TIME = "mid_time";
	public static final String KEY_END_TIME = "end_time";	
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_IS_START = "is_start";
	public static final String KEY_IS_MID = "is_mid";
	public static final String KEY_IS_END = "is_end";
 
    private static final String TAG = "DbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_1 =
    		"CREATE TABLE olle_course_my_records ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ "course_no INTEGER NOT NULL, "
    		+ "finished INTEGER DEFAULT 0, "
	      	+ "start_time TEXT DEFAULT NULL, "
	      	+ "mid_time TEXT DEFAULT NULL, "
	      	+ "end_time TEXT DEFAULT NULL );";
    private static final String DATABASE_CREATE_2 =
    		"CREATE TABLE olle_course_geo_points ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+" course_no INTEGER NOT NULL,"
		    +" latitude TEXT NOT NULL,"
		    +" longitude TEXT NOT NULL,"
		    +" is_start INTEGER DEFAULT 0,"
		    +" is_mid INTEGER DEFAULT 0,"
		    +" is_end INTEGER DEFAULT 0 );";

    private static final String DATABASE_NAME = "jejuolle";
    private static final String DATABASE_TABLE_1 = "olle_course_my_records";
    private static final String DATABASE_TABLE_2 = "olle_course_geo_points";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE_1);
            db.execSQL(DATABASE_CREATE_2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     */
//    public long createNote(String title, String body) {
//        ContentValues initialValues = new ContentValues();
//        initialValues.put(KEY_TITLE, title);
//        initialValues.put(KEY_BODY, body);
//
//        return mDb.insert(DATABASE_TABLE, null, initialValues);
//    }
    /*
     * INSERT INTO olle_course_my_records (course_no) VALUES 
     * ('1'), ('2'), ('3'), ('4'), ('5'), ('6'), ('7'), ('8'), ('9'), ('10'), 
	 * ('11'), ('12'), ('13'), ('14'), ('15'), ('16'), ('17');
     */
    public boolean createMyRecord() {
    	for(int courseNo = 1; courseNo <= ALL_COURSE_COUNT; courseNo++) {
    		ContentValues initialValues = new ContentValues();
    		initialValues.put(KEY_COURSE_NO, courseNo);
    		if(0 > mDb.insert(DATABASE_TABLE_1, null, initialValues))
    			return false;
    	}
    	return true;
    }
    
    public boolean createGeoPoints() {
    	GeoPointLoader gploader = new GeoPointLoader(mCtx);
    	int coordsCount = 0;
    	
    	for(int courseNo = 1; courseNo <= ALL_COURSE_COUNT; courseNo++) {
//    		coordsCount = gploader.getCourseGeopoints(courseNo).Coord.size();
    		
    		List<MapPoint> specificCourseCoords = gploader.getCourseGeopoints(courseNo);
    		coordsCount = specificCourseCoords.size();
    		
    		for(int i = 0; i < coordsCount; i++) {
//    			double latitude = gploader.getCourseGeopoints(courseNo).Coord.elementAt(0).getMapPointGeoCoord().latitude;
//    			double longitude = gploader.getCourseGeopoints(courseNo).Coord.elementAt(0).getMapPointGeoCoord().longitude;
    			double latitude = specificCourseCoords.get(i).getMapPointGeoCoord().latitude;
    			double longitude = specificCourseCoords.get(i).getMapPointGeoCoord().longitude;
    			
    			ContentValues initialValues = new ContentValues();
        		initialValues.put(KEY_COURSE_NO, courseNo);
        		initialValues.put(KEY_LATITUDE, latitude);
        		initialValues.put(KEY_LONGITUDE, longitude);
        		if(i == 0)
        			initialValues.put(KEY_IS_START, 1);
        		else if(i == coordsCount - 1)
        			initialValues.put(KEY_IS_END, 1);
        		
        		if(0 > mDb.insert(DATABASE_TABLE_2, null, initialValues))
        			return false;
    		}
    	}
    	return true;
    }
    

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
//    public boolean deleteNote(long rowId) {
//
//        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
//    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     */
    //모든 코스의 코스번호와 완주여부를 반환
    public Cursor fetchAllCourses() {
    	return mDb.query(DATABASE_TABLE_1, new String[] {KEY_COURSE_NO, KEY_FINISHED}, 
    			null, null, null, null, null);
    }

    //특정코스의 좌표들을 반환
    public Cursor fetchGeoPoints(int courseNo) {
    	return mDb.query(DATABASE_TABLE_2, new String[] {KEY_LATITUDE, KEY_LONGITUDE}, 
    			KEY_COURSE_NO + "=" + courseNo, null, null, null, null, null);
    }
      

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     */
    //특정코스의 특정스탬프가 찍힌 시간을 반환
    public Cursor fetchStampedTime(int courseNo, int stampNo) throws SQLException {
    	String isWhen = "";
    	switch(stampNo) {
    	case 0 : 
    		isWhen = KEY_START_TIME;
    		break;
    	case 1 : 
    		isWhen = KEY_MID_TIME;
    		break;
    	case 2 : 
    		isWhen = KEY_END_TIME;
    		break;
    	default :
    		break;
    	}

    	Cursor mCursor = mDb.query(true, DATABASE_TABLE_1, new String[] {isWhen}, 
    			KEY_COURSE_NO + "=" + courseNo + " AND " + isWhen + "IS NOT NULL", 
    			null, null, null, null, null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }

    //특정코스의 특정스탬프(시작,중간,끝) 좌표를 반환
    public Cursor fetchStampPoint(int courseNo, int stampNo) throws SQLException {
    	String isWhat = "";
    	switch(stampNo) {
    	case 0 : 
    		isWhat = KEY_IS_START;
    		break;
    	case 1 : 
    		isWhat = KEY_IS_MID;
    		break;
    	case 2 : 
    		isWhat = KEY_IS_END;
    		break;
    	default :
    		break;
    	}

    	Cursor mCursor = mDb.query(true, DATABASE_TABLE_2, new String[] {KEY_LATITUDE, KEY_LONGITUDE}, 
    			KEY_COURSE_NO + "=" + courseNo + " AND " + isWhat + "='1'", null, null, null, null, null);
    	if (mCursor != null) {
    		mCursor.moveToFirst();
    	}
    	return mCursor;
    }

    /**
     * Update 
     * 
     */ 
    //특정코스의 특정스탬프가 찍힌 시간을 업데이트
    public boolean updateStampedTime(int courseNo, int stampNo, String time) {
    	String isWhen = "";
    	switch(stampNo) {
    	case 0 : 
    		isWhen = KEY_START_TIME;
    		break;
    	case 1 : 
    		isWhen = KEY_MID_TIME;
    		break;
    	case 2 : 
    		isWhen = KEY_END_TIME;
    		break;
    	default :
    		break;
    	}
    	
    	ContentValues args = new ContentValues();
    	args.put(isWhen, time);

    	return mDb.update(DATABASE_TABLE_1, args, KEY_COURSE_NO + "=" + courseNo, null) > 0;
    }
    
    //특정코스의 완주여부를 업데이트
    public boolean updateCourseFinished(int courseNo) {
    	Cursor mCursor = mDb.query(true, DATABASE_TABLE_1, new String[] {KEY_COURSE_NO}, 
    			KEY_COURSE_NO + "=" + courseNo + 
    			" AND start_time IS NOT NULL AND mid_time IS NOT NULL AND end_time IS NOT NULL", 
    			null, null, null, null, null);
    	if (mCursor != null) {
    		ContentValues args = new ContentValues();
    		args.put(KEY_FINISHED, 1);
    		return mDb.update(DATABASE_TABLE_1, args, KEY_COURSE_NO + "=" + courseNo, null) > 0;
    	}
    	return false;
    }
}
