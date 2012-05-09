package com.hello.naverMap;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;


public class GeoPointLoader {

	public static final int ALL_COURSE_COUNT = 17;
	public static final int COURSE_COORDS_START_INDEX = 2130968576;
	
	private final Context mCtx;
	
	public class PointData {
		double lat;
		double lng;
		
		public PointData() {
			lat = 0;
			lng = 0;
		}
		
		public PointData pointDataWithGeoCoord(Double latitude, double longitude) {
			lat = latitude;
			lng = longitude;
			return this;
		}
	}
	
	List<List<PointData>> allCourseCoords = new ArrayList<List<PointData>>();
	List<List<PointData>> dividedCourseCoords = new ArrayList<List<PointData>>();
	
	public GeoPointLoader(Context ctx) {
		mCtx = ctx;

		loadCourseCoords();
	}
	
	private boolean loadCourseCoords() {
		Resources res = mCtx.getResources();
		
		int resourceIndex = COURSE_COORDS_START_INDEX;
		
		for(int i = 0; i < ALL_COURSE_COUNT; i++) {
			String[] course_lat = res.getStringArray(resourceIndex++);
			String[] course_lng = res.getStringArray(resourceIndex++);
			int coords_count = course_lat.length;

			List<PointData> tempList = new ArrayList<PointData>();
			for(int j = 0; j < coords_count; j++) {
				PointData pd = new PointData();
				tempList.add(pd.pointDataWithGeoCoord(Double.parseDouble(course_lat[j]), Double.parseDouble(course_lng[j])));
			}
			allCourseCoords.add(tempList);
		}
		
		return true;
	}
	
	public List<PointData> getCourseGeopoints(int courseNo) {
		return allCourseCoords.get(courseNo);
	}
	
	
	/**
	 * Ư�� �÷� �ڽ��� ��� ��ǥ�� divisor��ŭ ������ ������ List�迭��  ����
	 * @param courseNo �÷� �ڽ� �ѹ�
	 * @param divisor ��� ��ǥ�� ������ ������ ��
	 * @return '(�����ǥ����/divisor) ������ List'���� ���� List
	 */
	public List<List<PointData>> getCourseGeopointsWithDiv(int courseNo, int divisor) {
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
	 * Ư�� �÷� �ڽ��� ��� ��ǥ�� ���� amount��ŭ �������� ���� �� ������ List�迭��  ����
	 * @param courseNo �÷� �ڽ� �ѹ�
	 * @param amount �ϳ��� List�� ���� ��ǥ ����
	 * @return 'amount ������ List'���� ���� List
	 */
	public List<List<PointData>> getCourseGeopointsWithAmount(int courseNo, int amount) {
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
