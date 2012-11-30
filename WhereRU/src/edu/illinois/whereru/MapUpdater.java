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

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapUpdater {
	public static final String MY_KEY = "mykey"; // Obviously change to something unique
	
	private static final String DEBUG_TAG = "[MapUpadater]";
	
	private final Context mContext;
	private final List<Overlay> mapOverlays;
	private final HashMap<String, Overlay> overlays = new HashMap<String, Overlay>(); //user and friends overlays
	private final MapView mapView;
	
	public MapUpdater(Context context, List<Overlay> mapOverlays, MapView mapview){
		this.mContext = context;
		this.mapOverlays = mapOverlays;
		this.mapView = mapview;
		// add user overlay by default
		UserOverlay overlay = new UserOverlay(context);
		overlays.put(MY_KEY, overlay);
		mapOverlays.add(overlay);
	}
	
	
	public void initFriendsLocations(){
		for(FriendOverlay overlay : FriendBuilder.getOverlays()){
			overlay.setContext(mContext);
			String id = overlay.getId();
			
			overlays.put(id, overlay);
			mapOverlays.add(overlay);
		}
	}
	
	/**
	 * Updates location of the user.
	 * Called when activity is started, or refreshed.
	 * TODO: avoid overflowing the server by multiple requests.
	 * Either use broadcast from the service, or find another way. 
	 * 
	 * @param location a new location of the user
	 * @param mapView  google MapView, needed for shifting focus
	 */
	public void updateUserLocation(Location location) {
		
		UserOverlay overlay = (UserOverlay) overlays.get(MY_KEY);
		
		MarkerOverlayItem marker = MarkerOverlayItem.newOverlayItem(location);
		// add to map
		overlay.setOverlay(marker);
		// shift the focus
		mapView.getController().animateTo(marker.getPoint());
	}
}
