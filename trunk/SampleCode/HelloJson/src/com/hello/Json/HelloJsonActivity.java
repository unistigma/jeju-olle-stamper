package com.hello.Json;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HelloJsonActivity extends Activity {
	
	private static final String TAG = HelloJsonActivity.class.toString();
	private static final int DIALOG_INTERNET_ERROR = 100;
	private static final int DIALOG_NODATA = 200;
	
	private LocationManager locationManager;
	private String provider;
	private GpsLocationListener listener = null;
	
	public static List<PointOfInterest> items = new ArrayList<PointOfInterest>();
	public static Location currentLocation;
	
	boolean isActivated = false;
	
	private RelativeLayout progressLayout;
	private QueryAsync queryAsync;
	private ListView resultList;
	private ResultAdapter adapter;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case QueryAsync.POI_DATA:
				Log.w(TAG , "handleMessage POI_DATA start");
				if (!isActivated && items.size() > 1) {
					showResultList();
					Log.w(TAG , "handleMessage POI_DATA mid");
				}
				PointOfInterest item = (PointOfInterest) (msg.obj);
				items.add(item);
				adapter.notifyDataSetChanged();
				Log.w(TAG , "handleMessage POI_DATA end");
				break;
			case QueryAsync.QUERYASYNC_ERROR:
				showDialog(DIALOG_INTERNET_ERROR);
				Exception e = (Exception) msg.obj;
				Log.w(TAG, e);
				break;
			case QueryAsync.QUERYASYNC_NODATA:
				showDialog(DIALOG_NODATA);
				Exception e1 = (Exception) msg.obj;
				Log.w(TAG, e1);
			}
		}
	};
	
	class ResultAdapter extends BaseAdapter {

		Context context;

		public ResultAdapter(Context context) {
			super();
			this.context = context;
		}

		public int getCount() {
			// 리스트의 갯수
			return items.size();
		}

		public PointOfInterest getItem(int position) {
			// 해당 위치의 리스트 아이템
			return items.get(position);
		}

		public long getItemId(int position) {
			// 해당 위치의 리스트 아이템의 id
			return position;
		}

		public View getView(int position, View v, ViewGroup parent) {
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.ret_info, null);
			}
			RoundRectImageView res_image = (RoundRectImageView) v.findViewById(R.id.ImageView01);
			res_image.setRoundThickness(6);
			res_image.setHeight(80);
			res_image.setWidth(80);
			TextView name = (TextView) v.findViewById(R.id.TextView01);
			TextView vicinity = (TextView) v.findViewById(R.id.TextView_Vicinity);
			TextView phone = (TextView) v.findViewById(R.id.TextView_Phone);
			TextView url = (TextView) v.findViewById(R.id.TextView_URL);
			
			PointOfInterest poi = items.get(position);
			name.setText(poi.getName().trim());
			phone.setText(poi.getPhone().trim().split(",", 1)[0]);
			vicinity.setText(poi.getVicinity().trim());
			res_image.setBackgroundDrawable(poi.getDrawable());
			
//			url.setText(poi.getUrl().toString());
			url.setText(Html.fromHtml("<a href = \"" + poi.getUrl().toString() +"\">구글검색"));
			url.setMovementMethod(LinkMovementMethod.getInstance());

			return v;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        loadGps();
        currentLocation = getLocation();
        adapter = new ResultAdapter(this);
        
        progressLayout = (RelativeLayout) findViewById(R.id.rlayout_progress);
        progressLayout.setVisibility(View.GONE);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();		
		inflater.inflate(R.menu.json_menu, menu);
		
		MenuItem menuItem = menu.getItem(0);
		menuItem.setIcon(android.R.drawable.ic_menu_search);
		 
		return super.onCreateOptionsMenu(menu);
	}
	
	public void loadGps() {
    	Log.w(TAG , "loadGps" );
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);		// 정확도
    	criteria.setPowerRequirement(Criteria.POWER_LOW);	// 전원 소비량
    	criteria.setAltitudeRequired(false);				// 고도, 높이 값을 얻어 올지를 결정
    	criteria.setBearingRequired(false);
    	criteria.setSpeedRequired(false);					//속도
    	criteria.setCostAllowed(true);						//위치 정보를 얻어 오는데 들어가는 금전적 비용
    	provider = locationManager.getBestProvider(criteria, true);
    	listener = new GpsLocationListener();
    	locationManager.requestLocationUpdates(provider, 1000, 5, listener);
    }
	
	private class GpsLocationListener implements LocationListener {

    	public void onLocationChanged(Location location) {
    		Log.w(TAG , "onLocationChanged" );
		}

		public void onProviderDisabled(String provider) {
			Log.w(TAG , "onProviderDisabled" );
		}

		public void onProviderEnabled(String provider) {
			Log.w(TAG , "onProviderEnabled" );
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.w(TAG , "onStatusChanged" );
		}    	
    }
	
	private Location getLocation() {
		
		Location location = locationManager.getLastKnownLocation( provider );
		if ( location == null ) {
			location = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
		}
		return location;
	}
	
	private void showResultList() {
		progressLayout.setVisibility(View.GONE);
		resultList.setVisibility(View.VISIBLE);

		isActivated = true;
	}
	
	private void startSeach(String keyword) {
		queryAsync = new QueryAsync(getBaseContext(), currentLocation, keyword, handler);
		new Thread(queryAsync).start();

		//progressLayout = (RelativeLayout) findViewById(R.id.rlayout_progress);
		progressLayout.setVisibility(View.VISIBLE);
		resultList = (ListView) findViewById(R.id.result_list);
		resultList.setVisibility(View.GONE);
		resultList.setAdapter(adapter);
		resultList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListItemClick((ListView) parent, view, position, id);
			}
		});
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.submenu_land:
//			createCustomAlertDialog(0);
			startSeach("명소");
			Toast.makeText(this, "관광명소를 선택하였습니다", 0).show();
			return true;
		case R.id.submenu_food:
			Toast.makeText(this, "음식점을 선택하였습니다", 0).show();
			return true;
		case R.id.submenu_inn:
			Toast.makeText(this, "숙소를 선택하였습니다", 0).show();
			return true;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void createCustomAlertDialog(int selectedIndex) {
		final LinearLayout linear = (LinearLayout)
				View.inflate(this, R.layout.custom, null);
		
			new AlertDialog.Builder(this)
			.setTitle("섭지코지")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setView(linear)
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			})
			.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					
				}
			})
			.show();
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		PointOfInterest currentItem = items.get(position);

		String phoneNum = currentItem.getPhone();
		String addr = currentItem.getVicinity();
		String name = currentItem.getName();
		
		StringBuffer detail = new StringBuffer();
		if (addr != null && !addr.equals("")) {
			detail.append(addr);
			Toast.makeText(this.getBaseContext(), detail.toString(), Toast.LENGTH_SHORT).show();
		}

		StringBuffer itemDetail = new StringBuffer();
		itemDetail.append(name + " " + phoneNum + " " + addr);

		Log.i(TAG, itemDetail.toString() + " Item Detail");
		Log.i(TAG, String.valueOf(currentLocation.distanceTo(items.get(position).getLocation())) + " Between current and selecting item");
	}
}