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

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * UI for friend request page.
 * Shows the list of friend requests and lets the user handle (accept or decline) them.
 * 
 * @author don
 *
 */
public class FriendRequestListFragment extends ListFragment{

	private final List<Profile> friendRequests = new ArrayList<Profile>();
	private RequestTabArrayAdapter adapter;
	
	// Define button behaviors
	/* Add as a friend, update DB, remove the request from table*/
	private OnClickListener acceptButtonClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			final int position = getListView().getPositionForView(v);
			if(position != ListView.INVALID_POSITION){
				friendRequests.remove(position);
				adapter.notifyDataSetChanged();
			}
		}
	};
	

	/* Simple remove the request from the DB table, may want to notify the user*/
	private OnClickListener declineButtonClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			final int position = getListView().getPositionForView(v);
			if(position != ListView.INVALID_POSITION){
				friendRequests.remove(position);
				adapter.notifyDataSetChanged();
			}
			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		loadRequests();
		// Populate list items
		adapter = new RequestTabArrayAdapter();
		setListAdapter(adapter);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){		
		return super.onCreateView(inflater, container, savedInstanceState);
		 
	}

	@Override
	public void onPause(){
		super.onPause();
	}
	
	static class RequestTabTag{
		Button accept;
		Button decline;
		TextView nickname;
	}
	
	/*
	 * ArrayAdapter for customized list item view
	 */
	private class RequestTabArrayAdapter extends ArrayAdapter<Profile>{

		
		public RequestTabArrayAdapter() {
			super(getActivity(), R.layout.friend_request_list_item, friendRequests);
		}

		/**
		 * Override to use customized layout for friend request list item.
		 */
		@Override
		public View getView(int pos, View convertView, ViewGroup parent){
			
			View row = convertView;
			RequestTabTag tag = null;
			
			if(row == null){ // new row	
				
				// inflate the customized view
				LayoutInflater inflater = getActivity().getLayoutInflater();
				// Don't attach to the resource passed, it's not a parent view.
				row = inflater.inflate(R.layout.friend_request_list_item, parent,false);
				
				// create a tag for the view
				tag = new RequestTabTag();	
				tag.accept = (Button) row.findViewById(R.id.friend_request_accept_button);
				tag.decline = (Button) row.findViewById(R.id.friend_request_decline_button);
				tag.nickname = (TextView) row.findViewById(R.id.friend_request_name_text);
				tag.accept.setOnClickListener(acceptButtonClickListener);
				tag.decline.setOnClickListener(declineButtonClickListener);
				row.setTag(tag);		
			}else{ // override existing row
				tag = (RequestTabTag) row.getTag();		
			}
			
			// set needed information 
			Profile profile = friendRequests.get(pos);
			tag.nickname.setText(profile.getNickname());
			
			return row;
		}

	}


	
	private void loadRequests(){
		for(int i =0; i < 10; i++)
			friendRequests.add(new Profile(i+"", "nickname"+i));
			
	}
}
