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

import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

//This class only does basic functions related to map
//Take locations, and plot overlay items according to the coordinate
//Store different overlay for each user
public class GoogleMapActivity extends MapActivity {
	
	

	private static final String DEBUG_TAG = "[GoogleMapActivity]";
	
	private List<Overlay> mapOverlays; // Overlay applied to the map
	
	private MapUpdater updater;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(DEBUG_TAG, "Created");
		
		setContentView(R.layout.activity_google_map);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();		
		updater = new MapUpdater(this, mapOverlays, mapView);

		// NETWORK_PROVIDER= cell tower or wifi
		// GPS_PROVIDER = Satellite
		// Get cached location first for faster user experience.
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		updater.updateUserLocation(locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
		
		updater.initFriendsLocations();

	}
	
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(DEBUG_TAG, "Started");
	}


	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(DEBUG_TAG, "Destroyed");
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_google_map, menu);
		
		//return false menu is not to be shown
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		
			case R.id.menu_refresh:
				return true;
				
			case R.id.menu_add_friend_debug:
				
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
