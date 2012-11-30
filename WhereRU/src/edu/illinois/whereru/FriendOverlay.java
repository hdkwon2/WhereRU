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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class FriendOverlay extends GoogleMapItemizedOverlay{
	public static final int MAX_MARKER_NUM = 10;

	
	private int capacity; // number of visible markers, max is 10
	private final String friendId;
	private Context context;
	

	
	public FriendOverlay(Context context, int capacity, String friendId) {
		super(context.getResources().getDrawable(
				R.drawable.ic_friend_marker_red), MAX_MARKER_NUM);
	
		this.context = context;
		this.capacity = capacity;
		this.friendId = friendId;
	}
	
	public FriendOverlay(Context context, Drawable drawable, int capacity, String friendId) {
		super(drawable, MAX_MARKER_NUM);
	
		this.context = context;
		this.capacity = capacity;
		this.friendId = friendId;
	}
	
	@Override
	public int size(){
		if(capacity < super.size())
			return capacity;
		else return super.size();
	}
	
	
	/**
	 * Resizes the number of markers shown on the overlay
	 * @param capacity number of markers to be shown
	 */
	public void resize(int capacity){
		this.capacity = capacity;
		populate();
	}

	
	/**
	 * Returns unique ID of friend this overlay belongs to.
	 * @return id of the friend
	 */
	public String getId(){
		return friendId;
	}
	
	public void setContext(Context context){
		this.context = context;
	}
	@Override
	protected boolean onTap(int index){
		MarkerOverlayItem item = (MarkerOverlayItem) super.getItem(index);
		String nickname = FriendBuilder.getFriend(item.getId()).getNickName();
		long timeStamp = Long.parseLong(item.getTimeStamp());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH:mm", Locale.US);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String time = simpleDateFormat.format(new Date(timeStamp));
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(nickname);
		dialog.setMessage(time);
		dialog.show();
		return true;
	}
}
