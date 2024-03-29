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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/* LocationManagerService
 * Acquires the current location and updates it back to the map activity and the server
 * when a new best location is acquired.
 */
public class LocationManagerService extends Service implements LocationListener{
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final String DEBUG_TAG = "[LocationManagerService]";
	
	private LocationManager locationManager; 
	private final IBinder mBinder = new LocationManagerBinder();
	private Location currentBestLocation;
	private boolean updated = false;
	
	public class LocationManagerBinder extends Binder{
		LocationManagerService getService(){
			return LocationManagerService.this;
		}
	}
	
	// Once this method is called, the service can run in the background indefinitely
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.d(DEBUG_TAG, "Service Started");
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 0, this);
		// If gets killed, restart
		return START_STICKY;
	}
	
	// Called when bindService() is called. If startService() is not called,
	// then the service terminates when there are no bound components
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(DEBUG_TAG, "Service Bound");
		
		// Return binder so that the activity can directly access the public methods
		return mBinder;
	}
	
	@Override
	public void onDestroy(){
		Log.d(DEBUG_TAG, "Service Destroyed");
		
		locationManager.removeUpdates(this);
	}

	/**
	 * Send location to DB.
	 * Performs connection asynchronously. 
	 * 
	 */
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location, currentBestLocation)){
			Log.d(DEBUG_TAG, location.toString());
			
			currentBestLocation = location;
			updated = true;
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			long time = location.getTime();
			String userId = getSharedPreferences(MainPageActivity.PREFERENCE_NAME,
					0).getString(MainPageActivity.PREF_USER_ID, "");
			
			DBConnector dbConnector = new DBConnector();
			dbConnector.execute(DBConnector.UPDATE_USER_INFO, userId, latitude+"", longitude+"", time+"");
			
		}
	}

	public void onProviderDisabled(String provider) {
		
	}

	public void onProviderEnabled(String provider) {
		
	}

	public void onStatusChanged(String provider, int status, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	// Returns my current location
	public Location getCurrentLocation(){
		return currentBestLocation;
	}
	
	public boolean isLocationUpdated(){
		return updated;
	}
	
	public void markLocationOutdated(){
		updated = false;
	}

	/** Below code is straight from the android API
	  * Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/* Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
