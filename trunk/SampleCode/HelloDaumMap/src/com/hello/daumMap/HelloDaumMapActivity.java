package com.hello.daumMap;


import java.util.List;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPoint.GeoCoordinate;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;


public class HelloDaumMapActivity extends Activity 
implements MapView.OpenAPIKeyAuthenticationResultListener,
MapView.MapViewEventListener,                       
MapView.CurrentLocationEventListener,                   
MapView.POIItemEventListener
{
	public static final String DAUM_MAPS_APIKEY = "59ecd91767ce681f0a6051aa7af6f03cf65e9f54";
	public static final String LOG_TAG = "DaumMap^_^";
	
	private static final int ACTIVITY_DB_LIST = 0;
	private static final int ACTIVITY_BOOT = 1;
	
	MapView mapView;
	private DbAdapter mDbHelper;
	
	GeoPointLoader gpl;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        createBootActivity();	//show title image and DB initialize, loading 
         
        LinearLayout linearLayout = new LinearLayout(this);    
        mapView = new MapView(this);
        
        mapView.setDaumMapApiKey(DAUM_MAPS_APIKEY);
        mapView.setOpenAPIKeyAuthenticationResultListener(this);         
        mapView.setMapViewEventListener(this);       
        mapView.setCurrentLocationEventListener(this);       
        mapView.setPOIItemEventListener(this);
                     
        mapView.setMapType(MapView.MapType.Hybrid);
      //mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.314939, 126.819316), 2, true);
        
        //1. 지도 중심점, 마커추가 
//        setMapPointAndMark();
        
        //2. 선 그리기  
//        drawPolylineWithOnePoint();
//        drawPolylineWithPoints(7);
//        drawPolylineWithDivPoints();
//        drawPolylineWithAmountPoints();
        
        //3. 현위치, 나침반 모드 
        //----> onMapViewInitialized 이벤트에서 처리
        
        linearLayout.addView(mapView);      
        setContentView(linearLayout);
    }
	
	public void onDaumMapOpenAPIKeyAuthenticationResult(MapView arg0, int arg1,
			String arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onCalloutBalloonOfPOIItemTouched(MapView arg0, MapPOIItem arg1) {
		// TODO Auto-generated method stub
		GeoCoordinate geoCoord = arg1.getMapPoint().getMapPointGeoCoord();
		Toast.makeText(HelloDaumMapActivity.this, "위도: " + geoCoord.latitude + ", 경도: " + geoCoord.longitude,
				Toast.LENGTH_SHORT).show();
	}

	public void onDraggablePOIItemMoved(MapView arg0, MapPOIItem arg1,
			MapPoint arg2) {
		// TODO Auto-generated method stub
		
	}

	public void onPOIItemSelected(MapView arg0, MapPOIItem arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float headingAngle) {
		Log.i(LOG_TAG, String.format("MapView onCurrentLocationDeviceHeadingUpdate: device heading = %f degrees", headingAngle)); 
	}

	public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
		MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();    
		Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters)); 
	}

	public void onCurrentLocationUpdateCancelled(MapView arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onCurrentLocationUpdateFailed(MapView arg0) {
		// TODO Auto-generated method stub
	}

	public void onMapViewCenterPointMoved(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onMapViewDoubleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onMapViewInitialized(MapView arg0) {
//		setCurrentLocationTracking();
	}

	public void onMapViewLongPressed(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onMapViewSingleTapped(MapView arg0, MapPoint arg1) {
		// TODO Auto-generated method stub
		
	}

	public void onMapViewZoomLevelChanged(MapView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 특정좌표를 지도중심점으로 설정하고 마커를 추가.
	 */
	public void setMapPointAndMark() {
		MapPOIItem poiItem1 = new MapPOIItem(); 
		poiItem1.setItemName("집"); 
		poiItem1.setMapPoint(MapPoint.mapPointWithGeoCoord(33.314939,126.819316)); 
		poiItem1.setMarkerType(MapPOIItem.MarkerType.BluePin); 
		poiItem1.setShowAnimationType(MapPOIItem.ShowAnimationType.DropFromHeaven); 
		poiItem1.setShowCalloutBalloonOnTouch(true);        
		mapView.addPOIItem(poiItem1);

		MapPOIItem poiItem2 = new MapPOIItem(); 
		poiItem2.setItemName("표선면사무소"); 
		poiItem2.setUserObject(String.format("item%d", 2)); 
		poiItem2.setMapPoint(MapPoint.mapPointWithGeoCoord(33.326463, 126.831557));
		poiItem2.setMarkerType(MapPOIItem.MarkerType.BluePin);
		//poiItem2.setMarkerType(MapPOIItem.MarkerType.CustomImage); 
		poiItem2.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround); 
		//poiItem2.setCustomImageResourceId(R.drawable.custom_poi_marker); 
		//poiItem2.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(22,0)); 
		poiItem2.setShowCalloutBalloonOnTouch(true);

		mapView.addPOIItem(poiItem2); 
		mapView.fitMapViewAreaToShowAllPOIItems();
	}
	
    /**
     * 좌표를 하나씩 추가하여 선을 그린다. 
     */
	public void drawPolylineWithOnePoint() {
		MapPolyline polyline2 = new MapPolyline(21); 
		polyline2.setTag(2000); 
		polyline2.setLineColor(Color.argb(128, 0, 0, 255));

		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49990177899599, 126.51317484676838));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.50000961218029, 126.5125311166048));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49996430799365, 126.51167524047196));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49985546898097, 126.51110149919987));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49996975623071, 126.51064233854413));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49987545982003, 126.51003548875451));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.499939162284136, 126.50840344838798));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.500116523355246, 126.5060365665704));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.50008836016059, 126.50577287189662));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.4999271761626, 126.50505613535643));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.499886062927544, 126.50414292700589));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49953138269484, 126.50357622653246));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49903794005513, 126.5029296465218));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49866536445916, 126.50223436765373));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49842627067119, 126.50177596136928));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49775747861713, 126.50006379000843));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.497493574395776, 126.49827643297613));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49767747335136, 126.49762214161456));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.497506943531334, 126.49738233536482));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49668384063989, 126.49707463569939));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49613897502422, 126.49701059795916));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.4955770522356, 126.49697782471776));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49458115641028, 126.4970429521054));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49383072461933, 126.49693331681192));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49355114623904, 126.49692250415683));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.493465818464756, 126.49621800519526));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.493440840393305, 126.49508887901902));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.493582494556904, 126.49405622854829));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.493585721589625, 126.49255184456706));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49349804688245, 126.49187793955207));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49282799754292, 126.49089415557683));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.492036242969334, 126.490009278059));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49134913645685, 126.48907578550279));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.491350184194744, 126.48857119493186));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49116201046854, 126.48755178786814));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49080624058843, 126.48724819533527));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.49040927365422, 126.4875228703022));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.490048265084624, 126.48757006041706));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48986394703388, 126.48640816099942));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48930378444493, 126.48525388911366));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48864844534546, 126.48425878956914));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48819871433079, 126.4833542983979));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48820386920124, 126.48327760398388));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.487956980243325, 126.48245156742632));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.4872734779492, 126.48145504295826));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48669466562569, 126.48080200888216));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.48629220854491, 126.48032231256366));
		polyline2.addPoint(MapPoint.mapPointWithGeoCoord(33.486063801683486, 126.47992618381977));

		mapView.addPolyline(polyline2);                       
		mapView.fitMapViewAreaToShowAllPolylines();
	}
    
    /**
     * XML파일에서 특정코스의 좌표들을 로드한 후 선을 그린다.
     * 좌표가 510개를 초과하면 fitMapViewAreaToShowAllPolylines() 호출시 JNI 오버플로우 발생함.
	 * @param courseNo 코스번호
	 */
	public void drawPolylineWithPoints(int courseNo) {
		List<MapPoint> tempList = gpl.getCourseGeopoints(courseNo);
		MapPoint[] mp = new MapPoint[tempList.size()];
		mp = (MapPoint[])tempList.toArray(mp);
		
		MapPolyline existingPolyline = mapView.findPolylineByTag(777);
		if (existingPolyline != null) {
			mapView.removePolyline(existingPolyline);
		}
		MapPolyline polyline = new MapPolyline(tempList.size());
		polyline.setTag(777); 
		//polyline.setLineColor(Color.argb(128, 0, 0, 255));

		polyline.addPoints(mp);
		mapView.addPolyline(polyline);
		mapView.fitMapViewAreaToShowAllPolylines();
	}
    
    /**
     * XML파일에서 특정코스의 좌표들을 특정 값으로 쪼개어 로드한 후 여러개의 polyline으로 선을 그린다.
     * 좌표가 510개를 초과하면 fitMapViewAreaToShowAllPolylines() 호출시 JNI 오버플로우 발생함.
     * @param courseNo 코스번호
     */
    public void drawPolylineWithDivPoints(int courseNo) {
    	MapPolyline polyline1 = new MapPolyline(200);
    	polyline1.setLineColor(Color.argb(128, 255, 0, 0));
    	MapPolyline polyline2 = new MapPolyline(200); 
    	polyline2.setLineColor(Color.argb(128, 0, 255, 0));
    	MapPolyline polyline3 = new MapPolyline(200); 
    	polyline3.setLineColor(Color.argb(128, 0, 0, 255));
    	MapPolyline polyline4 = new MapPolyline(200); 
    	polyline4.setLineColor(Color.argb(128, 255, 255, 255));

    	List<List<MapPoint>> divided = gpl.getCourseGeopointsWithDiv(courseNo, 4);

    	MapPoint[] mp = new MapPoint[divided.get(0).size()];
    	mp = (MapPoint[])divided.get(0).toArray(mp);
    	polyline1.addPoints(mp);

    	mp = new MapPoint[divided.get(1).size()];
    	mp = (MapPoint[])divided.get(1).toArray(mp);
    	polyline2.addPoints(mp);

    	mp = new MapPoint[divided.get(2).size()];
    	mp = (MapPoint[])divided.get(2).toArray(mp);
    	polyline3.addPoints(mp);

    	mp = new MapPoint[divided.get(3).size()];
    	mp = (MapPoint[])divided.get(3).toArray(mp);
    	polyline4.addPoints(mp);

    	mapView.addPolyline(polyline1);
    	mapView.addPolyline(polyline2);
    	mapView.addPolyline(polyline3);
    	mapView.addPolyline(polyline4);
    	mapView.fitMapViewAreaToShowPolyline(polyline1);
    	//mapView.fitMapViewAreaToShowAllPolylines();
    }

    /**
     * XML파일에서 특정코스의 좌표들을 특정갯수를 가진 리스트로 나누어 로드한 후 여러개의 polyline으로 선을 그린다.
     * 좌표가 510개를 초과하면 fitMapViewAreaToShowAllPolylines() 호출시 JNI 오버플로우 발생함.
     * @param courseNo 코스번호
     */
    public void drawPolylineWithAmountPoints(int courseNo) {
    	MapPolyline polyline1 = new MapPolyline(1000);
    	polyline1.setLineColor(Color.argb(128, 255, 0, 0));
    	MapPolyline polyline2 = new MapPolyline(200); 
    	polyline2.setLineColor(Color.argb(128, 0, 255, 0));
    	MapPolyline polyline3 = new MapPolyline(200); 
    	polyline3.setLineColor(Color.argb(128, 0, 0, 255));

    	List<List<MapPoint>> divided = gpl.getCourseGeopointsWithAmount(courseNo, 300);

    	MapPoint[] mp = new MapPoint[divided.get(0).size()];
    	mp = (MapPoint[])divided.get(0).toArray(mp);
    	polyline1.addPoints(mp);

    	mp = new MapPoint[divided.get(1).size()];
    	mp = (MapPoint[])divided.get(1).toArray(mp);
    	polyline2.addPoints(mp);

    	mp = new MapPoint[divided.get(2).size()];
    	mp = (MapPoint[])divided.get(2).toArray(mp);
    	polyline3.addPoints(mp);

    	mapView.addPolyline(polyline1);
    	mapView.addPolyline(polyline2);
    	mapView.addPolyline(polyline3);
    	mapView.fitMapViewAreaToShowPolyline(polyline1);
//    	mapView.fitMapViewAreaToShowAllPolylines();
//    	mapView.fitMapViewAreaToShowMapPoints(mp);
    }
    
    /**
     * 현위치를 표시하고 나침반 모드를 설정.
     */
    public void setCurrentLocationTracking() {
    	mapView.setCurrentLocationEventListener(this); 
    	mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
    }
    
    /**
     * DB에서 해당코스의 모든좌표들을 SELECT한 후 지도에 그린다.
     * @param courseNo 코스번호
     */
    public void drawCourseFromDB(int courseNo) {
    	mDbHelper = new DbAdapter(this);
    	mDbHelper.open();
    	
		Cursor note = mDbHelper.fetchGeoPoints(courseNo);
		startManagingCursor(note);
		
		int coordsCount = note.getCount();
		MapPoint[] mp = new MapPoint[coordsCount];
		int addIndex = 0;
		
		while(note.moveToNext()){
			mp[addIndex++] = MapPoint.mapPointWithGeoCoord(note.getDouble(1), note.getDouble(2));
		}
		
		MapPolyline existingPolyline = mapView.findPolylineByTag(777);
		if (existingPolyline != null) {
			mapView.removePolyline(existingPolyline);
		}
		MapPolyline polyline = new MapPolyline(coordsCount);
		polyline.setTag(777); 
		//polyline.setLineColor(Color.argb(128, 0, 0, 255));

		polyline.addPoints(mp);
		mapView.addPolyline(polyline);
		mapView.fitMapViewAreaToShowAllPolylines();
    }
    
    public void drawStampPointFromDB(int courseNo) {
    	mDbHelper = new DbAdapter(this);
    	mDbHelper.open();
    	
    	Cursor note = mDbHelper.fetchStampPoints(courseNo);
		startManagingCursor(note);
		note.moveToNext();
    	
    	MapPOIItem existingPOIItemStart = mapView.findPOIItemByTag(778);
		if (existingPOIItemStart != null) {
			mapView.removePOIItem(existingPOIItemStart);
		}
		
		MapPOIItem existingPOIItemMid = mapView.findPOIItemByTag(779);
		if (existingPOIItemMid != null) {
			mapView.removePOIItem(existingPOIItemMid);
		}
		
		MapPOIItem existingPOIItemEnd = mapView.findPOIItemByTag(780);
		if (existingPOIItemEnd != null) {
			mapView.removePOIItem(existingPOIItemEnd);
		}
    	
    	MapPOIItem poiItemStart = new MapPOIItem();
		poiItemStart.setItemName("Start");
		poiItemStart.setTag(778);
		poiItemStart.setMapPoint(MapPoint.mapPointWithGeoCoord(note.getDouble(1), note.getDouble(2)));
		poiItemStart.setMarkerType(MapPOIItem.MarkerType.CustomImage);
		poiItemStart.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
		poiItemStart.setShowCalloutBalloonOnTouch(false);
		poiItemStart.setCustomImageResourceId(R.drawable.custom_poi_marker_start);
		poiItemStart.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2));
		mapView.addPOIItem(poiItemStart);
		
		note.moveToNext();
		
		MapPOIItem poiItemMid = new MapPOIItem();
		poiItemMid.setItemName("Mid");
		poiItemMid.setTag(779);
		poiItemMid.setMapPoint(MapPoint.mapPointWithGeoCoord(note.getDouble(1), note.getDouble(2)));
		poiItemMid.setMarkerType(MapPOIItem.MarkerType.CustomImage);
		poiItemMid.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
		poiItemMid.setShowCalloutBalloonOnTouch(false);
		poiItemMid.setCustomImageResourceId(R.drawable.custom_poi_marker);
		poiItemMid.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2));
		mapView.addPOIItem(poiItemMid);
		
		note.moveToNext();
		
		MapPOIItem poiItemEnd = new MapPOIItem();
		poiItemEnd.setItemName("End");
		poiItemEnd.setTag(780);
		poiItemEnd.setMapPoint(MapPoint.mapPointWithGeoCoord(note.getDouble(1), note.getDouble(2)));
		poiItemEnd.setMarkerType(MapPOIItem.MarkerType.CustomImage);
		poiItemEnd.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
		poiItemEnd.setShowCalloutBalloonOnTouch(false);
		poiItemEnd.setCustomImageResourceId(R.drawable.custom_poi_marker_end);
		poiItemEnd.setCustomImageAnchorPointOffset(new MapPOIItem.ImageOffset(29,2));
		mapView.addPOIItem(poiItemEnd);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();		
		inflater.inflate(R.menu.map_menu, menu);
		
		MenuItem menuItem = menu.getItem(0);
		menuItem.setIcon(android.R.drawable.ic_menu_mylocation);
		
		menuItem = menu.getItem(1);
		menuItem.setIcon(android.R.drawable.ic_menu_directions);
		
		menuItem = menu.getItem(2);
		menuItem.setIcon(android.R.drawable.ic_menu_manage);
		
		return true; 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_location: 
				setCurrentLocationTracking();
				return true;
			case R.id.submenu_course1:
				drawCourseFromDB(1);
				drawStampPointFromDB(1);
				return true;
			case R.id.submenu_course2:
				drawCourseFromDB(2);
				drawStampPointFromDB(2);
				return true;
			case R.id.submenu_course3:
				drawCourseFromDB(3);
				drawStampPointFromDB(3);
				return true;
			case R.id.submenu_course4:
				drawCourseFromDB(4);
				drawStampPointFromDB(4);
				return true;
			case R.id.submenu_course5:
				drawCourseFromDB(5);
				drawStampPointFromDB(5);
				return true;
			case R.id.submenu_course6:
				drawCourseFromDB(6);
				drawStampPointFromDB(6);
				return true;
			case R.id.submenu_course7:
				drawCourseFromDB(7);
				drawStampPointFromDB(7);
				return true;
			case R.id.submenu_course8:
				drawCourseFromDB(8);
				drawStampPointFromDB(8);
				return true;
			case R.id.submenu_course9:
				drawCourseFromDB(9);
				drawStampPointFromDB(9);
				return true;
			case R.id.submenu_course10:
				drawCourseFromDB(10);
				drawStampPointFromDB(10);
				return true;
			case R.id.submenu_course11:
				drawCourseFromDB(11);
				drawStampPointFromDB(11);
				return true;
			case R.id.submenu_course12:
				drawCourseFromDB(12);
				drawStampPointFromDB(12);
				return true;
			case R.id.submenu_course13:
				drawCourseFromDB(13);
				drawStampPointFromDB(13);
				return true;
			case R.id.submenu_course14:
				drawCourseFromDB(14);
				drawStampPointFromDB(14);
				return true;
			case R.id.submenu_course15:
				drawCourseFromDB(15);
				drawStampPointFromDB(15);
				return true;
			case R.id.submenu_course16:
				drawCourseFromDB(16);
				drawStampPointFromDB(16);
				return true;
			case R.id.submenu_course17:
				drawCourseFromDB(17);
				drawStampPointFromDB(17);
				return true;
			case R.id.menu_db: 
				Intent i = new Intent(this, TabManageActivity.class);
				startActivityForResult(i, ACTIVITY_DB_LIST);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch(requestCode) {
		case ACTIVITY_BOOT:
			if(resultCode == RESULT_OK) {
				Toast.makeText(this, "Database insert success!", 0).show();
			}
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void createBootActivity() {
		Intent i = new Intent(this, BootActivity.class);
		startActivityForResult(i, ACTIVITY_BOOT);
	}
	
}
