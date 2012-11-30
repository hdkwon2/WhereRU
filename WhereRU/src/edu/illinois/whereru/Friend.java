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

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Abstraction of a friend.
 * Contains profile, location, and options of a friend.
 * 
 * 
 * @author don
 *
 */
public class Friend {
	
	/* Marker type constants */
	public static final int RED = 0;
	public static final int PURPLE = 1;
	public static final int WHITE = 2;
	public static final int GREY = 3;
	
	/* Default option values */
	public static final int DEFAULT_MARKER_NUM = 3;
	public static final int DEFAULT_MARKER_TYPE = RED;
	
	private final Profile profile;
	private final SavedOption option;
	private FriendOverlay overlay; // locations, for now non final to make implementation easy. 
	private Context context;
	
	
	public Friend(Context context, Profile profile){
		this.profile = profile;
		this.overlay = new FriendOverlay(context, DEFAULT_MARKER_NUM, profile.getId());
		this.option = new SavedOption();
		option.optionMarkerType = DEFAULT_MARKER_TYPE;
		option.optionNumMarkers = DEFAULT_MARKER_NUM;
		this.context = context;
	}
	
	/**
	 * Returns the unique id representing the friend
	 * @return the id of the friend
	 */
	public String getUnqieueId(){
		return profile.getId();
	}
	
	/**
	 * Returns the current nickname of the friend
	 * @return the nickname of the friend
	 */
	public String getNickName(){
		return profile.getNickname();
	}
	
	/**
	 * Returns overlay containing locations of the friend
	 * @return an overlay of friend locations
	 */
	public FriendOverlay getOverlay(){
		return overlay;
	}
	
	/**
	 * Returns profile of the friend.
	 * @return profile of the friend
	 */
	public Profile getProfile(){
		return profile;
	}
	
	/**
	 * Returns the number of markers shown for this friend.
	 * @return number of markers shown
	 */
	public int getOptionNumMarkers(){
		return option.optionNumMarkers;
	}
	
	/**
	 * Returns the type of marker representing this friend's location.
	 * @return type of marker in integer representation
	 */
	public int getOptionMarkerType(){
		return option.optionMarkerType;
	}
	/**
	 * Sets the nickname of the friend
	 * @param newNickname a new nickname for the friend
	 */
	public void setNickName(String newNickname){
		profile.setNickName(newNickname);
	}
	
	/**
	 * Sets the profile of the friend with a new profile.
	 * @param prof new profile of the friend
	 */
	public void setProfile(Profile prof){
		profile.setNickName(prof.getNickname());
	}
	
	/**
	 * Sets the overlay (locations) with a new overlay
	 * @param overlay new overlay of the friend
	 */
	public void setOverlay(FriendOverlay overlay){
		this.overlay = overlay;
	}
	
	/**
	 * Sets the number of markers to be shown for this friend.
	 * @param n
	 */
	public void setOptionNumMarker(int n){
		option.optionNumMarkers = n;
		overlay.resize(n);
	}
	
	/**
	 * Sets the type of marker to represent the location of this friend.
	 * @param t
	 */
	public void setOptionMarkerType(int t){
		option.optionMarkerType = t;
		Drawable drawable;
	
		switch (t) {
		
		case PURPLE:
			drawable = context.getResources().getDrawable(
					R.drawable.ic_friend_marker_purple);
			break;
		case WHITE:
			drawable = context.getResources().getDrawable(
					R.drawable.ic_friend_marker_white);
			break;
		case GREY:
			drawable = context.getResources().getDrawable(
					R.drawable.ic_friend_marker_grey);
			break;
			
		default:
				drawable = context.getResources().getDrawable(
						R.drawable.ic_friend_marker_red);
		}
		
		overlay.setMarker(drawable);
	}
	
	@Override
	public boolean equals(Object friend){
		return profile.equals(((Friend)friend).getProfile());
	}
	
	/*
	 * Class to represent settings for each friend
	 */
	static class SavedOption{
		int optionNumMarkers;
		int optionMarkerType;
	}
}
