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

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Friends tab fragment.
 * Implements context menu, on long click on a item, option menu, and an edit button.
 * Uses a custom array adapter to view customized list item.
 * 
 * @author don
 *
 */
public class FriendListFragment extends ListFragment{

	/* Intent data key values */
	public static final String EXTRA_NICKNAME = "nickname";
	public static final String EXTRA_POSITION = "position";
	public static final String EXTRA_MARKER_NUM = "marker_num";
	public static final String EXTRA_MARKER_TYPE = "marker_type";

	/* Activity result request key values */
	public static final int EDIT_FRIEND_NICKNAME_REQUEST = 0;
	public static final int FRIEND_SETTING_REQUEST = 1;
	
	/* Dynamically added menu ids */
	private final static int ADD_MENU_ID = 0;
	private final static int FRIEND_SETTING_MENU_ID = 0;
	private final static int FRIEND_REMOVE_MENU_ID = 1;
	
	private List<Profile> friends;
	private FriendTabArrayAdapter adapter;
	
	/* Edit button onclick listener, starts edit activity for a result */
	OnClickListener editButtonListener = new OnClickListener(){

		public void onClick(View v) {
			int position = getListView().getPositionForView(v);
			String nickname = friends.get(position).getNickname();
			Intent intent = new Intent(getActivity(), EditFriendNameActivity.class);
			intent.putExtra(EXTRA_NICKNAME, nickname);
			intent.putExtra(EXTRA_POSITION, position);
			startActivityForResult(intent, EDIT_FRIEND_NICKNAME_REQUEST);
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		// Get friends profiles from the builder
		friends = FriendBuilder.getProfiles();
	
		// Fragment has menu items to contribute
		setHasOptionsMenu(true);
				
		// Populate list items
		adapter = new FriendTabArrayAdapter();
		setListAdapter(adapter);	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		
		switch (requestCode){
			/* Stores the new nickname */
			case EDIT_FRIEND_NICKNAME_REQUEST:
				if(resultCode == Activity.RESULT_OK){
					// Propagate the change
					Bundle extra = data.getExtras();
					String newNickname = extra.getString(EXTRA_NICKNAME);
					int pos = extra.getInt(EXTRA_POSITION);
					friends.get(pos).setNickName(newNickname);
				}
				break;
				
			/* Stores new setting and propagate the changes accordingly */
			case FRIEND_SETTING_REQUEST:
				if(resultCode == Activity.RESULT_OK){
					// Extracts needed data from intent
					Bundle extra = data.getExtras();
					int pos = extra.getInt(EXTRA_POSITION);
					int markerNum = extra.getInt(EXTRA_MARKER_NUM);
					int markerType = extra.getInt(EXTRA_MARKER_TYPE);
					
					// Modify corresponding fields of the friend
					String id = friends.get(pos).getId();
					Friend friend = FriendBuilder.getFriend(id);
					friend.setOptionNumMarker(markerNum);
					friend.setOptionMarkerType(markerType);
				}	
				break;
				
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){		
		return super.onCreateView(inflater, container, savedInstanceState);
		 
	}

	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		
		menu.add(Menu.NONE,ADD_MENU_ID , Menu.NONE, "ADD Friend");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		
			case ADD_MENU_ID:
				
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id){
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		registerForContextMenu(getListView());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		
		menu.add(Menu.NONE, FRIEND_SETTING_MENU_ID, Menu.NONE, "Setting");
		menu.add(Menu.NONE, FRIEND_REMOVE_MENU_ID, Menu.NONE, "Remove");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()){
			
			case FRIEND_SETTING_MENU_ID:
				loadSettings(info.position);
				return true;
				
			case FRIEND_REMOVE_MENU_ID:
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	/* Starts setting activity with friend's saved options marked */
	private void loadSettings(int position){
		String friendId = friends.get(position).getId();
		Friend friend = FriendBuilder.getFriend(friendId);
		
		if (friend != null) {
			Intent intent = new Intent(getActivity(),
					FriendSettingsActivity.class);
			intent.putExtra(EXTRA_POSITION, position);
			intent.putExtra(EXTRA_MARKER_NUM, friend.getOptionNumMarkers());
			intent.putExtra(EXTRA_MARKER_TYPE, friend.getOptionMarkerType());
			startActivityForResult(intent, FRIEND_SETTING_REQUEST);
		}
	}
	
	static class FriendTabTag{
		ImageView profilePic;
		TextView nickname;
		ImageView edit;
	}
	
	/*
	 * ArrayAdapter for customized list item view
	 */
	private class FriendTabArrayAdapter extends ArrayAdapter<Profile>{

		public FriendTabArrayAdapter() {
			super(getActivity(), R.layout.friend_list_item, friends);
			
		}
		
		@Override
		public View getView(int pos, View convertView, ViewGroup parent){
			
			View row = convertView;
			FriendTabTag tag = null;
			
			if(row == null){
				
				LayoutInflater inflater = getActivity().getLayoutInflater();
				row = inflater.inflate(R.layout.friend_list_item, parent, false);
				
				tag = new FriendTabTag();
				tag.profilePic = (ImageView) row.findViewById(R.id.friend_list_image);
				tag.nickname = (TextView) row.findViewById(R.id.friend_list_name_text);
				tag.edit = (ImageView) row.findViewById(R.id.friend_name_edit_button);
				tag.edit.setOnClickListener(editButtonListener);
				row.setTag(tag);
			}else{
				tag = (FriendTabTag) row.getTag();
			}
			
			// set needed information 
			Profile profile = friends.get(pos);
			tag.nickname.setText(profile.getNickname());
						
			return row;
			
		}
		
	}
}
