

package com.hello.Json;

import java.util.List;

import android.content.Context;
import android.location.Location;

/**
 * @author KyungSeop Kim
 *
 */
public class QueryHandler {

	private Location location;
	private Context context;
	private String keyword;
	/**
	 * 
	 */

	public QueryHandler(Context context, Location loc, String keyword) {
		location = loc;
		this.context = context;
		this.keyword = keyword;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
