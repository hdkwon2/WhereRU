/*
* 
* Copyright (C) 2012 Hyuk Don Kwon
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package edu.illinois.whereru;

import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MarkerOverlayItem extends OverlayItem {

	
	private MarkerOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	/*
	 * Factory method, only called when making a marker for user location
	 */
	public static MarkerOverlayItem newOverlayItem(Location location) {
		
		return new MarkerOverlayItem(makeGeoPoint(location.getLatitude(),
				location.getLongitude()), "User", location.getTime() + "");
	}
	
	/*
	 * Factory method, called when making a marker for friends locations
	 */
	public static MarkerOverlayItem newOverlayItem(double latitude,
			double longitude, String id, String timeStamp) {
		
		return new MarkerOverlayItem(makeGeoPoint(latitude, longitude), id, timeStamp);
	}
	
	private static GeoPoint makeGeoPoint(double latitude, double longitude){
		int lat = (int) (latitude * 1E6);
		int lon = (int) (longitude * 1E6);
		return new GeoPoint(lat,lon);
	}
	
	public String getId(){
		return this.getTitle();
	}
	
	public String getTimeStamp(){
		return this.getSnippet();
	}
}
