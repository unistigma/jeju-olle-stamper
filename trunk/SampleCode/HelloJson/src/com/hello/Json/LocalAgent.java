package com.hello.Json;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;



public class LocalAgent {

	private static final String TAG = LocalAgent.class.toString();
	private static final String PROTO = "https";
	private static final String HOST = "maps.googleapis.com";
	public static final int POI_LIMIT = 20;
	
	private static final String GOOGLE_PLACES_API_KEY = "AIzaSyD3NFSJbOhjUGJlW1cQdCopPqcOkSnUNWA";
	public static final String PLACE_SEARCH_RADIUS = "5000";
	
	private DefaultHttpClient client;
	private HttpGet _request;
	
	private Context context;
	private Location location;
	private String mKeyword;
	
	private List<String> mReferences;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public LocalAgent() {
		super();
		// TODO Auto-generated constructor stub
		client = new DefaultHttpClient();

		_request = new HttpGet();
		_request
				.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		_request.setHeader("Accept-Language", "en-us,en;q=0.5");
		_request.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
	}

	public LocalAgent(Context context, Location loc, String keyword) throws Exception {
		this();
		this.location = loc;
		this.context = context;
		this.mKeyword = keyword;
	}
	
//	https://maps.googleapis.com/maps/api/place/search/json?
//	location=33.314939,126.819316
//	&radius=5000
//	&keyword=%EB%AA%85%EC%86%8C
//	&sensor=true
//	&key=AIzaSyD3NFSJbOhjUGJlW1cQdCopPqcOkSnUNWA

	protected List<NameValuePair> getParamsForStep1(Location location) throws Exception {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		StringBuffer current = new StringBuffer();
		current.append(location.getLatitude());
		current.append(",");
		current.append(location.getLongitude());

		qparams.add(new BasicNameValuePair("location", current.toString()));
		qparams.add(new BasicNameValuePair("radius", PLACE_SEARCH_RADIUS));
		qparams.add(new BasicNameValuePair("keyword", mKeyword));
		qparams.add(new BasicNameValuePair("sensor", "true"));
		qparams.add(new BasicNameValuePair("key", GOOGLE_PLACES_API_KEY));

		return qparams;
	}
	
// https://maps.googleapis.com/maps/api/place/details/json?
// reference=CoQBdQAAAPP_ghxCAScUp1E8I-OiElCVkEuIMgH_BbaObga8gt0i8_0vWibnHaswrE1rNwQwodgnw2RdeSvrvCFVlDZi-noITWN-I5IFTuayXfwUNMguwqvimlnEMc1V1p2T1qvRWmRIu91hYePBgmWf0aSxx8-tt3t5rcc1cLfDw9WhoLedEhB3-gaqpPlBr-DZddbI3vPNGhSKsqT52Z5M7RVrq6yi9_T73EDxKw
// &sensor=true
// &key=AIzaSyD3NFSJbOhjUGJlW1cQdCopPqcOkSnUNWA
	
	protected List<NameValuePair> getParamsForStep2(String reference) throws Exception {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		qparams.add(new BasicNameValuePair("reference", reference));
		qparams.add(new BasicNameValuePair("sensor", "true"));
		qparams.add(new BasicNameValuePair("key", GOOGLE_PLACES_API_KEY));

		return qparams;
	}

	public URI takeUriForStep1(Location loc) throws Exception {
		return takeUriForStep1(getParamsForStep1(loc));
	}
	
	public URI takeUriForStep2(String ref) throws Exception {
		return takeUriForStep2(getParamsForStep2(ref));
	}

	public URI takeUriForStep1(List<NameValuePair> params) throws Exception {

		URI uri = URIUtils.createURI(PROTO, HOST, -1, "/maps/api/place/search/json", URLEncodedUtils
				.format(params, "UTF-8"), null);

		return uri;
	}
	
	public URI takeUriForStep2(List<NameValuePair> params) throws Exception {

		URI uri = URIUtils.createURI(PROTO, HOST, -1, "/maps/api/place/details/json", URLEncodedUtils
				.format(params, "UTF-8"), null);

		return uri;
	}

	public String request_STEP1() throws Exception {
		ResponseHandler<String> h = new BasicResponseHandler();
		_request.setURI(takeUriForStep1(location));

//		Log.i("REQUEST_STEP1", _request.getURI().toString());
		return client.execute(_request, h);
	}
	
	public String request_STEP2(String referenceKey) throws Exception {
		ResponseHandler<String> h = new BasicResponseHandler();
		_request.setURI(takeUriForStep2(referenceKey));

//		Log.i("REQUEST_STEP2", _request.getURI().toString());
		return client.execute(_request, h);
	}
	
	public boolean takeReferenceKey(String json) throws Exception {
		JSONObject jObj = new JSONObject(json);
		mReferences = new ArrayList<String>();
		
		if(jObj.has("results")) {
			JSONArray jaResults = jObj.getJSONArray("results");
			for(int i = 0 ; i < jaResults.length(); i++ ){
				mReferences.add(jaResults.getJSONObject(i).getString("reference"));
			}
		}
		
		return true;
	}

	public PointOfInterest takePOIs(String json) throws Exception {
//		Log.i("GET JS", json);

		JSONObject jObj = new JSONObject(json);

		if(jObj.has("result")) {
			return new PointOfInterest(jObj.getJSONObject("result"));
		}
				
		return null;
	}
	
	/**
	 * Do "request_STEP2" for detail result with referenceKey which is gotten from "request_STEP1".
	 * POI_LIMIT execute.
	 * @return
	 * @throws Exception
	 */
	public List<PointOfInterest> getSearchResults() throws Exception {
		
		if(getReady()) {
			List<PointOfInterest> ret = new ArrayList<PointOfInterest>();
			String response = "";

			for(String ref : mReferences) {
				if ((response = request_STEP2(ref)) == null)
					return null;
				
				ret.add(takePOIs(response));
			}

			return ret;
		}
		return null;
	}
	
	/**
	 * get reference value with keyword, location, radius...etc for detail search.
	 * just one execute.
	 * @return
	 * @throws Exception
	 */
	private boolean getReady() throws Exception {
		String response;
		
		if ((response = request_STEP1()) == null)
			return false;
		
		takeReferenceKey(response);
		
		return true;
	}
	
}
