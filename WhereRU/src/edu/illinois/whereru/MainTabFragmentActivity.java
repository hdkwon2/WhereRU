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

import edu.illinois.whereru.LocationManagerService.LocationManagerBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;


public class MainTabFragmentActivity extends FragmentActivity {
	private static final String DEBUG_TAG = "[MainTabFragmentActivity]";
    private FragmentTabHost mTabHost;

    private LocationManagerService mService;
	private boolean mBound = false;
	
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "onCreate");
        
        setContentView(R.layout.activity_main_tab);
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("map").setIndicator("Map"),
                GoogleMapFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("friendList").setIndicator("Friends"),
        		FriendListFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("friendRequest").setIndicator("Friend Requests"),
        		FriendRequestListFragment.class, null);

        
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        
		// Starting location manager service
		Intent intent = new Intent(this, LocationManagerService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		startService(intent);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(DEBUG_TAG, "Saving instance state");
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    
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
}