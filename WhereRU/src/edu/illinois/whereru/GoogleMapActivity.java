package edu.illinois.whereru;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
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

public class GoogleMapActivity extends MapActivity {
	private static final String DEBUG_TAG = "[GoogleMapActivity]";
	private List<Overlay> mapOverlays;
	private GoogleMapItemizedOverlay myItemizedOverlay;
	private LocationManagerService mService;
	private boolean mBound = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_google_map);
		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapOverlays = mapView.getOverlays();

		Intent intent = new Intent(this, LocationManagerService.class);
		startService(intent);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
		// NETWORK_PROVIDER= cell tower or wifi
		// GPS_PROVIDER = Satellite
		// Get cached location first for faster user experience.
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		updateMyLocation(locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

	}
	
	// Get my location when we start the activity
	@Override
	public void onStart(){
		super.onStart();
		// if for some reason the service is not bound, bind again
		if(!mBound){
			Intent intent = new Intent(this, LocationManagerService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
		// takes a while after binding, cannot immediately use mService
		// that's why this is else instead of another if or without flow control
		else{
			if(mService.isLocationUpdated()){
				updateMyLocation(mService.getCurrentLocation());
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
						updateMyLocation(mService.getCurrentLocation());
						mService.markLocationOutdated();
					}
					Toast.makeText(this, "Refreshed!", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(this, "Can't refresh, try later!", Toast.LENGTH_SHORT).show();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	// TODO create hashmap or list of itemizedOverlay for each user/friend
	// Adding a new location is simply adding to the corresponding
	// itemizedOverlay
	public void updateMyLocation(Location location) {
		if (myItemizedOverlay == null) {
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.androidmarker);
			myItemizedOverlay = new GoogleMapItemizedOverlay(drawable, this, 1);
		}

		int latitude = (int) (location.getLatitude() * 1E6);
		int longitude = (int) (location.getLongitude() * 1E6);
		GeoPoint geoPoint = new GeoPoint(latitude, longitude);
		OverlayItem overlayItem = new OverlayItem(geoPoint, "my current point",
				"I'm at home");

		myItemizedOverlay.addOverlay(overlayItem);
		mapOverlays.add(myItemizedOverlay);
	}

}
