package com.hello.Json;

import java.io.Serializable;
import java.net.URL;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;


public class PointOfInterest implements Serializable, Comparable<PointOfInterest> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8608367384977112177L;
	private static final String TAG = PointOfInterest.class.toString();
	
//////////////////////////////////////////////////////////////////////
//"result" : {
//"geometry" : {
//"location" : {
//"lat" : 33.3225110,
//"lng" : 126.8418680
//}
//},
//"icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/museum-71.png",
//"international_phone_number" : "+82 64-787-4501",
//"name" : "제주민속촌박물관",	      
//"url" : "http://maps.google.com/maps/place?cid=523936506654881901",
//"vicinity" : "서귀포시 표선면 표선리 40-1",
//},
//////////////////////////////////////////////////////////////////////
	
	private Location mLocation;
	private URL mIcon;
	private String mPhone;
	private String mName;
	private URL mUrl;
	private String mVicinity;
		
	private URL mPhotoUrl;
	private Drawable mDrawable;
	
	
	//add austin
	private int x;
	private int y;

	
	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drawable) {
		this.mDrawable = drawable;
	}

	public URL getPhotoUrl() {
		return mPhotoUrl;
	}

	public void setPhotoUrl(URL photoUrl) {
		this.mPhotoUrl = photoUrl;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public Location getLocation() {
		return mLocation;
	}

	public void setLocation(Location location) {
		this.mLocation = location;
	}

	public String getVicinity() {
		return mVicinity;
	}

	public void setVicinity(String vicinity) {
		this.mVicinity = vicinity;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		this.mPhone = phone;
	}
	
	public URL getIcon() {
		return mIcon;
	}

	public void setIcon(URL icon) {
		this.mIcon = icon;
	}
	
	public URL getUrl() {
		return mUrl;
	}

	public void setUrl(URL url) {
		this.mUrl = url;
	}
	
	public Bitmap getBitmap(){
		if( mDrawable == null )
			return null;
		return ((BitmapDrawable)mDrawable).getBitmap();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public PointOfInterest(String name, String vicinity, String phone, Location location) {
		super();
		this.mName = name;
		this.mLocation = location;
		this.mVicinity = vicinity;
		this.mPhone = phone;
	}
	
	
	public PointOfInterest(JSONObject object) throws Exception {
		JSONObject loc = object.getJSONObject("geometry").getJSONObject("location");
		this.setLocation(new Location("current point"));
		mLocation.setLatitude(Double.valueOf(loc.getString("lat")));
		mLocation.setLongitude(Double.valueOf(loc.getString("lng")));
		setIcon(new URL(object.getString("icon")));
		setPhone(object.getString("international_phone_number"));
		setName(object.getString("name"));
		setUrl(new URL(object.getString("url")));
		setVicinity(object.getString("vicinity"));
	}

//	@Override
	public int compareTo(PointOfInterest another) {
		//이름순 정렬
		return this.getName().compareTo(another.getName());
	}
}
