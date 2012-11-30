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
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.OverlayItem;

/*
 * TODO: find why location stored in DB is null
 * do buildFriendOverlays, 
 * for each different friend, add a dummy node containing user id and name.
 */
public class JSONObjectParser {

	/* JSON object tags */
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_INSERT_ID = "insert_id";
	public static final String TAG_FRIENDS_INFO = "friends_info";
	public static final String TAG_FRIEND_NAME = "nickname";
	public static final String TAG_FRIEND_ID = "id";
	public static final String TAG_LATITUDE = "latitude";
	public static final String TAG_LONGITUDE = "longitude";
	public static final String TAG_TIME_STAMP = "time_stamp";
	
	/**
	 * Returns the list of marker overlay items representing locations of frends.
	 * The json object must contain friend informations, such as id, location, and time stamp.
	 * 
	 * Exits when json is not in the expected format. 
	 * 
	 * @param json a result returned from DB server 
	 * @return     the list of marker overlay items parsed from json 
	 * @throws JSONException 
	 */
	public static List<OverlayItem> parseOverlayItems(JSONObject json) throws JSONException{
		
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

		JSONArray friendsInfo = json.getJSONArray(TAG_FRIENDS_INFO);

		for (int i = 0; i < friendsInfo.length(); i++) {
			if (!friendsInfo.isNull(i)) {
				JSONObject obj = friendsInfo.getJSONObject(i);
				String userId = obj.getString(TAG_FRIEND_ID);
				double latitude = obj.getDouble(TAG_LATITUDE);
				double longitude = obj.getDouble(TAG_LONGITUDE);
				String timeStamp = obj.getString(TAG_TIME_STAMP);
				OverlayItem item = MarkerOverlayItem.newOverlayItem(latitude,
						longitude, userId, timeStamp);
				items.add(item);
			}
		}
		
		return items;
	}
	
	/**
	 * Extracts profile from DB's response.
	 * One profile per friend.
	 * 
	 * @param json a result returned from DB
	 * @return list of profiles
	 * @throws JSONException 
	 */
	public static ArrayList<Profile> parseDistinctProfile(JSONObject json) throws JSONException{
		
		HashSet<Profile> set = new HashSet<Profile>();
		
		JSONArray friendsInfo = json.getJSONArray(TAG_FRIENDS_INFO);

		for (int i = 0; i < friendsInfo.length(); i++) {
			if (!friendsInfo.isNull(i)) {
				JSONObject obj = friendsInfo.getJSONObject(i);
				String id = obj.getString(TAG_FRIEND_ID);
				String nickname = obj.getString(TAG_FRIEND_NAME);
				set.add(new Profile(id, nickname));
			}
		}
		
		return new ArrayList<Profile>(set);
	}
	
	/**
	 * Returns a boolean indicating a successful DB queries set by DB server.
	 * True if DB query was successful, false otherwise.
	 * 
	 * Exits if json is not the expected format.
	 * 
	 * @param json a result returned from DB server
	 * @return     boolean indicating successful DB queries
	 */
	public static boolean getSuccess(JSONObject json){
		boolean success = false;

		try {
			success = json.getInt(TAG_SUCCESS) == 1;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return success;
	}
	
	/**
	 * Returns unique user id set by DB server.
	 * It is the insert id to global user info table in DB
	 * 
	 * Exits exception if
	 *  
	 * @param json a result returned from DB
	 * @return	   unique user id
	 */
	public static int getUserId(JSONObject json){
		int userId = -1;
		
		try{
			userId = json.getInt(TAG_INSERT_ID);
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return userId;
	}
}
