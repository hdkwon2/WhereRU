package edu.illinois.whereru;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DBConnector extends AsyncTask<String, String, JSONObject>{
	public static final String ADD_USER = "add_user";
	public static final String UPDATE_USER_INFO = "update_user_info";
	public static final String GET_USER_INFO = "get_user_info";
	private static final String DEBUG_TAG = "[DBConnector]";
	private static final String ADD_USER_URL = "http://192.168.2.3/db_add_user.php";
	private static final String GET_USER_INFO_URL = "http://192.168.2.3/db_get_user_info.php";
	private static final String UPDATE_USER_INFO_URL = "http://192.168.2.3/db_update_user_info.php";

	private final List<NameValuePair> params = new ArrayList<NameValuePair>();
	
	
	@Override
	protected void onPreExecute(){
	}
	
	// args should contain
	// 0 = ADD_USER, 1 = user_id, 2 = device_id 
	//or 0 = UPDATE_USER_INFO, 1 = user_id, 2= latitude, 3 = longitude, 4= time stamp
	//or 0 = GET_UESR_INFO, 1=user_id
	// TODO 
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
	
	// Called at creation, restart of GoogleMapActivity, or explicitely called by refresh
	private JSONObject loadUserInfo(String userID){
		Log.d(DEBUG_TAG, "Loading user info");
		
		params.add(new BasicNameValuePair("user_id",userID));
		return makeHttpRequest(GET_USER_INFO_URL, "GET", params);
	}
	
	// Called when a new best location is found, update the new location to DB
	private JSONObject updateUserInfo(String userId, String latitude, String longitude, String timeStamp){
		Log.d(DEBUG_TAG, "Updating location");
		
		params.add(new BasicNameValuePair("user_id", userId));
		params.add(new BasicNameValuePair("latitude", latitude));
		params.add(new BasicNameValuePair("longitude", longitude));
		params.add(new BasicNameValuePair("time_stamp", timeStamp));
		
		return makeHttpRequest(UPDATE_USER_INFO_URL, "POST", params);
	}
	
	// This function should only be called once per client
	private JSONObject addUser(String userName, String deviceId){
		Log.d(DEBUG_TAG, "Adding user");
		
		params.add(new BasicNameValuePair("name", userName));
		params.add(new BasicNameValuePair("device_id", deviceId));
		
		// send request and retrieve parsed json from data
		return makeHttpRequest(ADD_USER_URL, "POST", params);
	}
	
	// Parsing JSON object from contents returned from DB
	private JSONObject makeHttpRequest(String urlString, String method,
			List<NameValuePair> params) {

		JSONObject jsonObject = null;
		
		try {
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;

			if (method.equals("POST")) {
				// init post
				HttpPost httpPost = new HttpPost(urlString);
				// set the resource to send
				httpPost.setEntity(new UrlEncodedFormEntity(params));	
				// send the request, retrieve response
				httpResponse = httpClient.execute(httpPost);

			} else {
				// GET method
				// formulate url
				String paramString = URLEncodedUtils.format(params, "utf-8");
				urlString += "?" + paramString;
				// init GET
				HttpGet httpGet = new HttpGet(urlString);
				// send the request, retrieve response
				httpResponse = httpClient.execute(httpGet);

			}
		
			// retrieve content from the response
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
			
			is.close();
			jsonObject =  new JSONObject(sb.toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(DEBUG_TAG, "Error  " + e.toString());
		} catch (IllegalStateException e) {
			Log.e(DEBUG_TAG, "Error IllegalState " + e.toString());
		} catch (IOException e) {
			Log.e(DEBUG_TAG, "Error IO " + e.toString());
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, "Error parsing data " + e.toString());
		}
		return jsonObject;
	}
	
}
