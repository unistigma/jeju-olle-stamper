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
	public static final String KEY_WHICH_STAMP = "which_stamp";
 
    private static final String TAG = "DbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DB_CREATE_1 =
    		"CREATE TABLE olle_course_my_records ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ "course_no INTEGER NOT NULL, "
    		+ "finished INTEGER DEFAULT 0, "
	      	+ "start_time TEXT DEFAULT NULL, "
	      	+ "mid_time TEXT DEFAULT NULL, "
	      	+ "end_time TEXT DEFAULT NULL );";
    private static final String DB_CREATE_2 =
    		"CREATE TABLE olle_course_geo_points ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
    		+ " course_no INTEGER NOT NULL,"
		    + " latitude TEXT NOT NULL,"
		    + " longitude TEXT NOT NULL,"
		    + " is_start INTEGER DEFAULT 0,"
		    + " is_mid INTEGER DEFAULT 0,"
		    + " is_end INTEGER DEFAULT 0 );";
    private static final String DB_CREATE_3 =
    		"CREATE TABLE olle_stamp_geo_points ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "course_no INTEGER NOT NULL, "
			+ "which_stamp INTEGER NOT NULL, "
			+ "latitude TEXT NOT NULL, "
			+ "longitude TEXT NOT NULL );";

    private static final String DATABASE_NAME = "jejuolle";
    private static final String DATABASE_TABLE_1 = "olle_course_my_records";
    private static final String DATABASE_TABLE_2 = "olle_course_geo_points";
    private static final String DATABASE_TABLE_3 = "olle_stamp_geo_points";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;
    

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mCtx = context;
        }
        
        private final Context mCtx;
        private GeoPointLoader gploader;

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_1);
            db.execSQL(DB_CREATE_2);
            db.execSQL(DB_CREATE_3);
            
            loadAllRecord(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
        
        /**
         * GeoPointLoader 객체를 생성하여 xml파일의 좌표를 로드한 후 DB 테이블에 INSERT.
         * @param db
         * @return
         */
        private boolean loadAllRecord(SQLiteDatabase db) {
        	gploader = new GeoPointLoader(mCtx, GeoPointLoader.LOAD_TYPE_COURSE_AND_STAMP);
        	
        	loadMyRecord(db);
        	loadGeoPoints(db);
        	loadStampPoints(db);
        	
        	return true;
        }
     
        /**
         * INSERT TO olle_course_my_records
         * @return
         */
        private boolean loadMyRecord(SQLiteDatabase db) {
        	for(int courseNo = 1; courseNo <= ALL_COURSE_COUNT; courseNo++) {
        		ContentValues initialValues = new ContentValues();
        		initialValues.put(KEY_COURSE_NO, courseNo);
        		if(0 > db.insert(DATABASE_TABLE_1, null, initialValues))
        			return false;
        	}
        	return true;
        }
        
        /**
         * INSERT TO olle_course_geo_points
         * @return
         */
        private boolean loadGeoPoints(SQLiteDatabase db) {
        	int coordsCount = 0;
        	
        	for(int courseNo = 1; courseNo <= ALL_COURSE_COUNT; courseNo++) {
        		List<MapPoint> specificCourseCoords = gploader.getCourseGeopoints(courseNo);
        		coordsCount = specificCourseCoords.size();
        		
        		for(int i = 0; i < coordsCount; i++) {
        			double latitude = specificCourseCoords.get(i).getMapPointGeoCoord().latitude;
        			double longitude = specificCourseCoords.get(i).getMapPointGeoCoord().longitude;
        			
        			ContentValues initialValues = new ContentValues();
            		initialValues.put(KEY_COURSE_NO, courseNo);
            		initialValues.put(KEY_LATITUDE, latitude);
            		initialValues.put(KEY_LONGITUDE, longitude);
            		
            		if(0 > db.insert(DATABASE_TABLE_2, null, initialValues))
            			return false;
        		}
        	}
        	return true;
        }
        
        /**
         * INSERT TO olle_stamp_geo_points
         * @return
         */
        private boolean loadStampPoints(SQLiteDatabase db) {
        	for(int courseNo = 1; courseNo <= ALL_COURSE_COUNT; courseNo++) {
        		List<MapPoint> specificStampCoords = gploader.getStampGeopoints(courseNo);
        		
        		for(int i = 0; i < 3; i++) {
        			double latitude = specificStampCoords.get(i).getMapPointGeoCoord().latitude;
        			double longitude = specificStampCoords.get(i).getMapPointGeoCoord().longitude;

        			ContentValues initialValues = new ContentValues();
            		initialValues.put(KEY_COURSE_NO, courseNo);
            		initialValues.put(KEY_WHICH_STAMP, i);
            		initialValues.put(KEY_LATITUDE, latitude);
            		initialValues.put(KEY_LONGITUDE, longitude);
            		
            		if(0 > db.insert(DATABASE_TABLE_3, null, initialValues))
            			return false;
        		}
        	}
        	return true;
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

    /**
     * @return 모든 코스의 코스번호와 완주여부를 반환
     */
    public Cursor fetchAllCourses() {
    	return mDb.query(DATABASE_TABLE_1, new String[] {KEY_COURSE_NO, KEY_FINISHED}, 
    			null, null, null, null, null);
    }

  
    /**
     * @param courseNo 코스넘버
     * @return 특정코스의 좌표들을 반환
     */
    public Cursor fetchGeoPoints(int courseNo) {
    	return mDb.query(DATABASE_TABLE_2, new String[] {KEY_LATITUDE, KEY_LONGITUDE}, 
    			KEY_COURSE_NO + "=" + courseNo, null, null, null, null, null);
    }
    
    /**
     * @param courseNo 코스넘버
     * @param stampNo 스탬프 종류(0: 시작, 1: 중간, 2: 끝)
     * @return 특정코스의 특정스탬프(시작,중간,끝) 좌표를 반환
     * @throws SQLException
     */
    public Cursor fetchStampPoint(int courseNo, int whichStamp) throws SQLException {
    	return mDb.query(true, DATABASE_TABLE_3, new String[] {KEY_LATITUDE, KEY_LONGITUDE}, 
    			KEY_COURSE_NO + "=" + courseNo + " AND " + KEY_WHICH_STAMP + "=" + whichStamp 
    			, null, null, null, null, null);
    }
      

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     */
    
    /**
     * @param courseNo 코스넘버
     * @param stampNo 스탬프 종류(0: 시작, 1: 중간, 2: 끝)
     * @return 특정코스의 특정스탬프가 찍힌 시간을 반환
     * @throws SQLException
     */
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

    

    /**
     * Update 
     * 
     */
    
    /**
     * 특정코스의 특정스탬프가 찍힌 시간을 업데이트
     * @param courseNo 코스넘버
     * @param stampNo 스탬프 종류(0: 시작, 1: 중간, 2: 끝)
     * @param time 스탬프를 찍는 시간
     * @return
     */
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
    
    /**
     * 특정코스의 완주여부를 업데이트
     * @param courseNo 코스넘버
     * @return
     */
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
