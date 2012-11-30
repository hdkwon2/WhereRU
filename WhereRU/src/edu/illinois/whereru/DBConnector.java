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


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Class responsible for handling connection with DB.
 * Makes query in the background asynchronously.
 * TODO: For some calls that the main thread waits on, implement progress dialog.
 * @author don
 *
 */
public class DBConnector extends AsyncTask<String, String, JSONObject>{
	public static final String ADD_USER = "add_user";
	public static final String UPDATE_USER_INFO = "update_user_info";
	public static final String GET_USER_INFO = "get_user_info";
	private static final String DEBUG_TAG = "[DBConnector]";
	private static final String ADD_USER_URL = "http://10.0.246.128/db_add_user.php";
	private static final String GET_USER_INFO_URL = "http://10.0.246.128/db_get_user_info.php";
	private static final String UPDATE_USER_INFO_URL = "http://10.0.246.128/db_update_user_info.php";

	private final List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	
	@Override
	protected void onPreExecute(){
	}
	
	/**
	 * 0 = ADD_USER, 1 = user_id, 2 = device_id
	 * or 0 = UPDATE_USER_INFO, 1 = user_id, 2= latitude, 3 = longitude, 4= time stamp
	 * or 0 = GET_UESR_INFO, 1=user_id
	 */
	@Override
	protected JSONObject doInBackground(String... args) {
		Log.d(DEBUG_TAG, "Starting connection");
		
		JSONObject result = null;
		
		if(args[0].equals(ADD_USER)) result = addUser(args[1], args[2]);
		else if(args[0].equals(UPDATE_USER_INFO)) result = updateUserInfo(args[1], args[2], args[3], args[4]);
		else if(args[0].equals(GET_USER_INFO)) result = loadUserInfo(args[1]);
		
		return result;
	}
	
	@Override
	protected void onPostExecute(JSONObject result){
		
	}
	
	/* Called at creation, restart of GoogleMapActivity, or explicitly called by refresh*/
	private JSONObject loadUserInfo(String userID){
		Log.d(DEBUG_TAG, "Loading user info");
		
		params.add(new BasicNameValuePair("user_id",userID));
		return HTTPRequest.makeHttpRequest(GET_USER_INFO_URL, "GET", params);
	}
	
	/* Called from LocationManagerService when a new location is found, update the new location to DB*/
	private JSONObject updateUserInfo(String userId, String latitude, String longitude, String timeStamp){
		Log.d(DEBUG_TAG, "Updating location");
		
		params.add(new BasicNameValuePair("user_id", userId));
		params.add(new BasicNameValuePair("latitude", latitude));
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("time_stamp", timeStamp));
		
		return HTTPRequest.makeHttpRequest(UPDATE_USER_INFO_URL, "POST", params);
	}
	
	/* This function should only be called once per client */
	private JSONObject addUser(String userName, String deviceId){
		Log.d(DEBUG_TAG, "Adding user");
		
		params.add(new BasicNameValuePair("name", userName));
		params.add(new BasicNameValuePair("device_id", deviceId));
		
		// send request and retrieve parsed json from data
		return HTTPRequest.makeHttpRequest(ADD_USER_URL, "POST", params);
	}
	
	
}
