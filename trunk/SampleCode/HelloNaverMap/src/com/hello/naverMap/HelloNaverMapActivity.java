package com.hello.naverMap;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hello.naverMap.GeoPointLoader.PointData;
import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.maps.overlay.NMapPathData;
import com.nhn.android.maps.overlay.NMapPathLineStyle;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay;

public class HelloNaverMapActivity extends NMapActivity {
	
	public static final String NAVER_MAP_APIKEY = "1709d3750aaee96e6342e5330bd10598";
	private static final String LOG_TAG = "HelloNaverMapActivity";
	private static final boolean DEBUG = false;
	
	private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_HYBRID;
	private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
	private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

	private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
	private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
	private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
	private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
	private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
	private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";
	
	NMapView mMapView;
	NMapController mMapController;
	
	private MapContainerView mMapContainerView;
	private SharedPreferences mPreferences;
	private NMapViewerResourceProvider mMapViewerResourceProvider;
	private NMapOverlayManager mOverlayManager;
	
	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mMapView = new NMapView(this);
        mMapView.setApiKey(NAVER_MAP_APIKEY);
        
        ////////////////////////////
        // create parent view to rotate map view
        mMapContainerView = new MapContainerView(this);
        mMapContainerView.addView(mMapView);

        // set the activity content to the map view
        setContentView(mMapContainerView);
        ////////////////////////////
        
//        setContentView(mMapView);
        mMapView.setClickable(true);
        
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
     	mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mMapView.getMapController();
        mMapView.setBuiltInZoomControls(true, null);
        
        //1. 지도위에 오버레이 아이템 표시
//        showOverlayItem();
        
        //2. 지도 위에 경로 그리기
//        showPathDataPoints();
//        showPathDataPointsWithLoader();
        
        //3. 현재 위치 표시 및 나침반에 의한 지도 회전
        showLocationNCompass();
    }
    
    @Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		stopMyLocation();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// save map view state such as map center position and zoom level.
		saveInstanceState();
		super.onDestroy();
	}
    
    /**
     *  지도위에 오버레이 아이템 표시
     */
    public void showOverlayItem() {
    	int markerId = NMapPOIflagType.PIN;

    	// set POI data
    	NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
    	poiData.beginPOIdata(2);
    	poiData.addPOIitem(126.819316, 33.314939, "집", markerId, 0);
    	poiData.addPOIitem(126.831557, 33.326463, "표선면사무소", markerId, 0);
    	poiData.endPOIdata();

    	// create POI data overlay
    	NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

    	// show all POI data
    	poiDataOverlay.showAllPOIdata(0);
    	
    	// set event listener to the overlay
    	poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);
    }
    
    /**
     * 지도 위에 경로 그리기
     */
    public void showPathDataPoints() {
    	// set path data points
    	NMapPathData pathData = new NMapPathData(9);

    	pathData.initPathData();
    	pathData.addPathPoint(127.108099, 37.366034, NMapPathLineStyle.TYPE_SOLID);
    	pathData.addPathPoint(127.108088, 37.366043, 0);
    	pathData.addPathPoint(127.108079, 37.365619, 0);
    	pathData.addPathPoint(127.107458, 37.365608, 0);
    	pathData.addPathPoint(127.107232, 37.365608, 0);
    	pathData.addPathPoint(127.106904, 37.365624, 0);
    	pathData.addPathPoint(127.105933, 37.365621, NMapPathLineStyle.TYPE_DASH);
    	pathData.addPathPoint(127.105929, 37.366378, 0);
    	pathData.addPathPoint(127.106279, 37.366380, 0);
    	pathData.endPathData();

    	NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);
    	
    	// show all path data
    	pathDataOverlay.showAllPathData(0);
    }
    
    /**
     * xml에서 좌표값을 읽어들여 지도 위에 경로 그리기
     */
    public void showPathDataPointsWithLoader() {
    	GeoPointLoader gpl = new GeoPointLoader(this);
    	List<PointData> tempPd = gpl.getCourseGeopoints(1);
    	int pointCount = tempPd.size();
    	
    	// set path data points
    	NMapPathData pathData = new NMapPathData(pointCount);

    	pathData.initPathData();
    	for(int i = 0; i < pointCount; i++) {
    		pathData.addPathPoint(tempPd.get(i).lng, tempPd.get(i).lat, 0);
    	}
    	pathData.endPathData();

    	NMapPathDataOverlay pathDataOverlay = mOverlayManager.createPathDataOverlay(pathData);
    	
    	// show all path data
    	pathDataOverlay.showAllPathData(0);
    }
    
    /**
     * 현재 위치 표시 및 나침반에 의한 지도 회전
     */
    public void showLocationNCompass() {
        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        // compass manager
        mMapCompassManager = new NMapCompassManager(this);
        
        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);
        
        if (mMyLocationOverlay != null) {
			if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
				mOverlayManager.addOverlay(mMyLocationOverlay);
			}
			
			mMapLocationManager.enableMyLocation(false);

			if (mMapLocationManager.isMyLocationEnabled()) {

				if (!mMapView.isAutoRotateEnabled()) {
					mMyLocationOverlay.setCompassHeadingVisible(true);

					mMapCompassManager.enableCompass();

					mMapView.setAutoRotateEnabled(true, false);

					mMapContainerView.requestLayout();
				} else {
					stopMyLocation();
				}

				mMapView.postInvalidate();
			} else {
				boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(false);
				if (!isMyLocationEnabled) {
					Toast.makeText(HelloNaverMapActivity.this, "Please enable a My Location source in system settings",
						Toast.LENGTH_LONG).show();

					Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(goToSettings);

					return;
				}
			}
		}
    }
    
    /* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

//		@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom level.
				restoreInstanceState();
			} else { // fail
				Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());
				Toast.makeText(HelloNaverMapActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
			}
		}

//		@Override
		public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
			}
		}

//		@Override
		public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
			}
		}

//		@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
			}
		}

//		@Override
		public void onMapCenterChangeFine(NMapView mapView) {

		}
	};
	
	private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

//		@Override
		public void onLongPress(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub

		}

//		@Override
		public void onLongPressCanceled(NMapView mapView) {
			// TODO Auto-generated method stub

		}

//		@Override
		public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
			// TODO Auto-generated method stub

		}

//		@Override
		public void onTouchDown(NMapView mapView, MotionEvent ev) {

		}

//		@Override
		public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
		}

	};
	
	/* POI data State Change Listener*/
	private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

//		@Override
		public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
			}

			// [[TEMP]] handle a click event of the callout
			Toast.makeText(HelloNaverMapActivity.this, "onCalloutClick: " + item.getTitle(), Toast.LENGTH_SHORT).show();
		}

//		@Override
		public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
			if (DEBUG) {
				if (item != null) {
					Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
				} else {
					Log.i(LOG_TAG, "onFocusChanged: ");
				}
			}
		}
	};
	
	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

//		@Override
		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

			if (mMapController != null) {
				mMapController.animateTo(myLocation);
			}

			return true;
		}

//		@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

			// stop location updating
			//			Runnable runnable = new Runnable() {
			//				public void run() {										
			//					stopMyLocation();
			//				}
			//			};
			//			runnable.run();	

			Toast.makeText(HelloNaverMapActivity.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

//		@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(HelloNaverMapActivity.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

			stopMyLocation();
		}

	};
    
    private void restoreInstanceState() {
		mPreferences = getPreferences(MODE_PRIVATE);

		int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
		boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
		boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

		mMapController.setMapViewMode(viewMode);
		mMapController.setMapViewTrafficMode(trafficMode);
		mMapController.setMapViewBicycleMode(bicycleMode);
//		mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);
	}

	private void saveInstanceState() {
		if (mPreferences == null) {
			return;
		}

		NGeoPoint center = mMapController.getMapCenter();
		int level = mMapController.getZoomLevel();
		int viewMode = mMapController.getMapViewMode();
		boolean trafficMode = mMapController.getMapViewTrafficMode();
		boolean bicycleMode = mMapController.getMapViewBicycleMode();

		SharedPreferences.Editor edit = mPreferences.edit();

		edit.putInt(KEY_CENTER_LONGITUDE, center.getLongitudeE6());
		edit.putInt(KEY_CENTER_LATITUDE, center.getLatitudeE6());
		edit.putInt(KEY_ZOOM_LEVEL, level);
		edit.putInt(KEY_VIEW_MODE, viewMode);
		edit.putBoolean(KEY_TRAFFIC_MODE, trafficMode);
		edit.putBoolean(KEY_BICYCLE_MODE, bicycleMode);

		edit.commit();

	}
    
    private void stopMyLocation() {
    	if (mMyLocationOverlay != null) {
    		mMapLocationManager.disableMyLocation();

    		if (mMapView.isAutoRotateEnabled()) {
    			mMyLocationOverlay.setCompassHeadingVisible(false);

    			mMapCompassManager.disableCompass();

    			mMapView.setAutoRotateEnabled(false, false);

    			mMapContainerView.requestLayout();
    		}
    	}
    }
    
    /** 
     * Container view class to rotate map view.
     */
    private class MapContainerView extends ViewGroup {

    	public MapContainerView(Context context) {
    		super(context);
    	}

    	@Override
    	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    		final int width = getWidth();
    		final int height = getHeight();
    		final int count = getChildCount();
    		for (int i = 0; i < count; i++) {
    			final View view = getChildAt(i);
    			final int childWidth = view.getMeasuredWidth();
    			final int childHeight = view.getMeasuredHeight();
    			final int childLeft = (width - childWidth) / 2;
    			final int childTop = (height - childHeight) / 2;
    			view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    		}

    		if (changed) {
    			mOverlayManager.onSizeChanged(width, height);
    		}
    	}

    	@Override
    	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    		int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    		int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    		int sizeSpecWidth = widthMeasureSpec;
    		int sizeSpecHeight = heightMeasureSpec;

    		final int count = getChildCount();
    		for (int i = 0; i < count; i++) {
    			final View view = getChildAt(i);

    			if (view instanceof NMapView) {
    				if (mMapView.isAutoRotateEnabled()) {
    					int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
    					sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
    					sizeSpecHeight = sizeSpecWidth;
    				}
    			}

    			view.measure(sizeSpecWidth, sizeSpecHeight);
    		}
    		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	}
    }

}