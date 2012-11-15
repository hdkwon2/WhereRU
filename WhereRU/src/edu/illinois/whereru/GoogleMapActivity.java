package edu.illinois.whereru;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.illinois.whereru.LocationManagerService.LocationManagerBinder;

//This class only does basic functions related to map
//Take locations, and plot overlay items according to the coordinate
//Store different overlay for each user
public class GoogleMapActivity extends MapActivity {
	
	public static final String MY_KEY = "mykey"; // Obviously change to something unique
	private static final int MAX_NUM_LOCATION = 3;
	private static final String DEBUG_TAG = "[GoogleMapActivity]";
	
	private List<Overlay> mapOverlays;
	private LocationManagerService mService;
	private boolean mBound = false;
	private final HashMap<String, Overlay> overlays = new HashMap<String, Overlay>();
	private MapView mapView;
	private Location debugLocation;
	private SharedPreferences userInfo;
	private Editor preferenceEditor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(DEBUG_TAG, "Created");
		
		setContentView(R.layout.activity_google_map);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();

		Intent intent = new Intent(this, LocationManagerService.class);
		startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_default_user_marker);
		overlays.put(MY_KEY, new GoogleMapItemizedOverlay(drawable,this,1));
		mapOverlays.add(overlays.get(MY_KEY));
		// NETWORK_PROVIDER= cell tower or wifi
		// GPS_PROVIDER = Satellite
		// Get cached location first for faster user experience.
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		updateMap(MY_KEY, locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

	}
	
	// Get my location when we start the activity
	@Override
	public void onStart(){
		super.onStart();
		Log.d(DEBUG_TAG, "Started");
		
		// if for some reason the service is not bound, bind again
		if(!mBound){
			Intent intent = new Intent(this, LocationManagerService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
		// takes a while after binding, cannot immediately use mService
		// that's why this is else instead of another if or without flow control
		else{
			if(mService.isLocationUpdated()){
				updateMap(MY_KEY, mService.getCurrentLocation());
				mService.markLocationOutdated();
			}
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			LocationManagerBinder binder = (LocationManagerBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
		}
	};
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.d(DEBUG_TAG, "Destroyed");
		
		if(mBound){
			mService.stopSelf(); // Remove it later
			unbindService(mConnection);
			mBound=false;
		}
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
				if(mBound){
					if(mService.isLocationUpdated()){
						updateMap(MY_KEY, mService.getCurrentLocation());
						mService.markLocationOutdated();
					}
					Toast.makeText(this, "Refreshed!", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(this, "Can't refresh, try later!", Toast.LENGTH_SHORT).show();
				}
				return true;
				
			case R.id.menu_add_friend_debug:
				if(debugLocation == null){
					if(mBound){
						debugLocation = mService.getCurrentLocation();
					}
					else{
						Toast.makeText(this, "Try later", Toast.LENGTH_SHORT).show();
					}
				}
				debugLocation.setLatitude(debugLocation.getLatitude()+2);
				updateMap("debug", debugLocation);
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/*
	 * Searches overlay from hash matching the key
	 * and update add the location
	 * TODO find an appropriate key representing each friend/user
	 */
	public void updateMap(String key, Location location) {
		
		GoogleMapItemizedOverlay overlay;
		
		if(!overlays.containsKey(key)){
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.ic_default_friend_marker);
			overlay = new GoogleMapItemizedOverlay(drawable, this, MAX_NUM_LOCATION);
			overlays.put(key,overlay);
			mapOverlays.add(overlay);
		}else{
			overlay = (GoogleMapItemizedOverlay) overlays.get(key);
		}
	
		int latitude = (int) (location.getLatitude() * 1E6);
		int longitude = (int) (location.getLongitude() * 1E6);
		GeoPoint geoPoint = new GeoPoint(latitude, longitude);
		OverlayItem overlayItem = new OverlayItem(geoPoint, "User ID",
				new SimpleDateFormat("HH:mm:ss").format(new Date(location.getTime())));
		overlay.addOverlay(overlayItem);
		if(key.equals(MY_KEY)) mapView.getController().animateTo(geoPoint);
	}

}
