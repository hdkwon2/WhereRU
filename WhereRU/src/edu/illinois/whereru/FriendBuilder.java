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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.google.android.maps.OverlayItem;

/**
 * Globally shared class to build and keep friends objects. 
 * 
 * TODO: figure out if this is the best way to keep friends objects.
 * @author don
 *
 */
public class FriendBuilder {

	/* Globally shared list of friends */
	private static HashMap<String,Friend> friends  =  new HashMap<String,Friend>();
	
	/**
	 * Build friends list from DB data.
	 * 
	 * @param context application context
	 */
	public static void build(Context context){
		
		try {
			
			JSONObject json = getFriendInfoFromDB(context); // get data from DB

			List<Profile> profiles = JSONObjectParser // extract profiles
					.parseDistinctProfile(json);
			
			List<OverlayItem> locations = JSONObjectParser // extract overlays
					.parseOverlayItems(json);
			
			for (Profile profile : profiles) {
			
				if (friends.containsKey(profile.getId())) { //If this friend already exists
					// update the profile of with possibly a new one
					Friend friend = friends.get(profile.getId());
					friend.setProfile(profile);
				} else {
					// Create a new friend for with this profile
					friends.put(profile.getId(), new Friend(context, profile));
				}
			}
			
			Friend friend = null;
			String prevId = "";
			
			/* Update locations to corresponding friend object.
			 * Assumes the data returned from DB is ordered by friend id.
			 */
			for(OverlayItem location : locations){
				String id = ((MarkerOverlayItem)location).getId();
				
				if(!prevId.equals(id)){
					friend = friends.get(id);
				}
				
				friend.getOverlay().addOverlay(location);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Couldn't load friend information...",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Returns profiles of all friends.
	 * @return list of profiles
	 */
	public static List<Profile> getProfiles(){
		
		List<Profile> profiles = new ArrayList<Profile>();
		
		for(Object friend : ((HashMap<String, Friend>)friends).values().toArray())
			profiles.add(((Friend)friend).getProfile());
		
		return profiles;
	}
	
	/**
	 * Returns overlays of all friends.
	 * @return list of overlays
	 */
	public static List<FriendOverlay> getOverlays(){
		
		List<FriendOverlay> overlays = new ArrayList<FriendOverlay>();
		
		for(Object friend : ((HashMap<String, Friend>)friends).values().toArray())
			overlays.add(((Friend) friend).getOverlay());
		
		return overlays;
	}
	
	/**
	 * Returns a friend with a specific id
	 * @param id friend's id
	 * @return friend with the id
	 */
	public static Friend getFriend(String id){
		return friends.get(id);
	}
	
	/* Retrieves data from DB */
	private static JSONObject getFriendInfoFromDB(Context context) throws InterruptedException, ExecutionException{
		DBConnector connector = new DBConnector();
		String userId = context.getSharedPreferences(MainPageActivity.PREFERENCE_NAME, 0)
				.getString(MainPageActivity.PREF_USER_ID, "-1");
		connector.execute(DBConnector.GET_USER_INFO, userId);
		
		return connector.get();
	}
}
