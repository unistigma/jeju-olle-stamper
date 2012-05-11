package com.hello.daumMap;

import java.util.ArrayList;
import java.util.List;

import net.daum.mf.map.api.MapPoint;
import android.content.Context;
import android.content.res.Resources;


public class GeoPointLoader {

	public static final int LOAD_TYPE_COURSE_AND_STAMP = 0;
	public static final int LOAD_TYPE_COURSE_COORDS = 1;
	public static final int LOAD_TYPE_STAMP_COORDS = 2;
	
	public static final int ALL_COURSE_COUNT = 17;
	public static final int COURSE_COORDS_START_INDEX = 2130968576;
	public static final int STAMP_COORDS_START_INDEX = 2130968610;
	
	private final Context mCtx;
	
	List<List<MapPoint>> allCourseCoords = new ArrayList<List<MapPoint>>();
	List<List<MapPoint>> stampCoords = new ArrayList<List<MapPoint>>();
	List<List<MapPoint>> dividedCourseCoords = new ArrayList<List<MapPoint>>();
	
	public GeoPointLoader(Context ctx, int loadDataType) {
		mCtx = ctx;

		if(LOAD_TYPE_COURSE_AND_STAMP == loadDataType) {
			loadCoords();
		} else if(LOAD_TYPE_COURSE_COORDS == loadDataType) {
			loadCourseCoords();
		} else if(LOAD_TYPE_STAMP_COORDS == loadDataType) {
			loadStampCoords();
		}
	}
	
	/**
	 * 불필요한 메모리 낭비를 막기위해 모든 List 객체들의 clean 메소드 실행.
	 */
	public void cleanMyInventory() {
		allCourseCoords.clear();
		stampCoords.clear();
		dividedCourseCoords.clear();
	}
	
	
	
	/**
	 * 모든 올레 코스 좌표 + 모든 스탬프 위치 좌표를 한번에 로드.
	 * @return
	 */
	private boolean loadCoords() {
		Resources res = mCtx.getResources();
		int courseResIndex = COURSE_COORDS_START_INDEX;
		int stampResIndex = STAMP_COORDS_START_INDEX;

		for(int i = 0; i < ALL_COURSE_COUNT; i++) {
			///////////////////////////////////////////////////////////ALL COURSE COORDS LOADING
			String[] course_lat = res.getStringArray(courseResIndex++);
			String[] course_lng = res.getStringArray(courseResIndex++);
			int coords_count = course_lat.length;

			List<MapPoint> tempCourseList = new ArrayList<MapPoint>();
			for(int j = 0; j < coords_count; j++) {
				tempCourseList.add(MapPoint.mapPointWithGeoCoord(Double.parseDouble(course_lat[j]), Double.parseDouble(course_lng[j])));
			}
			allCourseCoords.add(tempCourseList);

			///////////////////////////////////////////////////////////STAMP COORDS LOADING
			course_lat = res.getStringArray(stampResIndex++);
			course_lng = res.getStringArray(stampResIndex++);
			List<MapPoint> tempStampList = new ArrayList<MapPoint>();
			for(int k = 0; k < 3; k++) {
				tempStampList.add(MapPoint.mapPointWithGeoCoord(Double.parseDouble(course_lat[k]), Double.parseDouble(course_lng[k])));
			}
			stampCoords.add(tempStampList);
		}
		return true;
	}
	
	/**
	 * 모든 올레 코스 좌표를 로드.
	 * @return
	 */
	private boolean loadCourseCoords() {
		Resources res = mCtx.getResources();
		int resourceIndex = COURSE_COORDS_START_INDEX;
		
		for(int i = 0; i < ALL_COURSE_COUNT; i++) {
			String[] course_lat = res.getStringArray(resourceIndex++);
			String[] course_lng = res.getStringArray(resourceIndex++);
			int coords_count = course_lat.length;

			List<MapPoint> tempList = new ArrayList<MapPoint>();
			for(int j = 0; j < coords_count; j++) {
				tempList.add(MapPoint.mapPointWithGeoCoord(Double.parseDouble(course_lat[j]), Double.parseDouble(course_lng[j])));
			}
			allCourseCoords.add(tempList);
		}
		return true;
	}
	
	/**
	 * 모든 스탬프 위치 좌표를 로드.
	 * @return
	 */
	private boolean loadStampCoords() {
		Resources res = mCtx.getResources();
		int resourceIndex = STAMP_COORDS_START_INDEX;
		
		for(int i = 0; i < ALL_COURSE_COUNT; i++) {
			String[] course_lat = res.getStringArray(resourceIndex++);
			String[] course_lng = res.getStringArray(resourceIndex++);

			List<MapPoint> tempList = new ArrayList<MapPoint>();
			for(int j = 0; j < 3; j++) {
				tempList.add(MapPoint.mapPointWithGeoCoord(Double.parseDouble(course_lat[j]), Double.parseDouble(course_lng[j])));
			}
			stampCoords.add(tempList);
		}
		return true;
	}
	
	public List<MapPoint> getCourseGeopoints(int courseNo) {
		return allCourseCoords.get(--courseNo);
	}
	
	public List<MapPoint> getStampGeopoints(int courseNo) {
		return stampCoords.get(--courseNo);
	}
	
	
	/**
	 * 특정 올레 코스의 모든 좌표를 divisor만큼 나눈뒤 생성된 List배열을  리턴
	 * @param courseNo 올레 코스 넘버
	 * @param divisor 모든 좌표의 갯수를 나누는 값
	 * @return '(모든좌표갯수/divisor) 길이의 List'들을 가진 List
	 */
	public List<List<MapPoint>> getCourseGeopointsWithDiv(int courseNo, int divisor) {
		dividedCourseCoords.clear();
		
		int coordsSize = allCourseCoords.get(courseNo).size();
		int dividedCoordsSize = coordsSize / divisor;
		int startIndex, endIndex = 0;
		
		for(int i = 0; i < divisor; i++) {
			startIndex = i * dividedCoordsSize;
			if(i == divisor - 1) {
				endIndex = coordsSize - 1;
			} else {
				endIndex = (i * dividedCoordsSize) + (dividedCoordsSize - 1);
			}
			dividedCourseCoords.add(allCourseCoords.get(courseNo).subList(startIndex, endIndex));
		}
		
		return dividedCourseCoords;
	}
	

	/**
	 * 특정 올레 코스의 모든 좌표를 각각 amount만큼 가지도록 나눈 뒤 생성된 List배열을  리턴
	 * @param courseNo 올레 코스 넘버
	 * @param amount 하나의 List에 넣을 좌표 갯수
	 * @return 'amount 길이의 List'들을 가진 List
	 */
	public List<List<MapPoint>> getCourseGeopointsWithAmount(int courseNo, int amount) {
		dividedCourseCoords.clear();
		
		int coordsSize = allCourseCoords.get(courseNo).size();
		int quotient = coordsSize / amount;
		int startIndex, endIndex = 0;
		
		for(int i = 0; i <= quotient; i++) {
			startIndex = i * amount;
			if(i == quotient) {
				endIndex = coordsSize - 1;
			} else {
				endIndex = ((i + 1) * amount) - 1;
			}
			dividedCourseCoords.add(allCourseCoords.get(courseNo).subList(startIndex, endIndex));
		}
		
		return dividedCourseCoords;
	}
	
}
