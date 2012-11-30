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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Activity for editing a friend's nickname.
 * Returns a new nickname user sets to the calling activity/fragment.
 * 
 * @author don
 *
 */
public class EditFriendNameActivity extends Activity{

	private EditText editText;
	private int position;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_edit_friend_name);
		// Extract friend's nickname and position of friend object
		Bundle extra = getIntent().getExtras();
		String originalNickname = extra.getString(FriendListFragment.EXTRA_NICKNAME);
		position = extra.getInt(FriendListFragment.EXTRA_POSITION);
		
		// Set the edit text to show the original nickname
		editText = (EditText) findViewById(R.id.edit_friend_name_edittext);
		editText.setText(originalNickname);
	}
	
	/**
	 * Edit friend OK button onClick handler.
	 * Retrieves the new nickname and send it back to the calling fragment. 
	 * 
	 * @param v View of the clicked item
	 */
	public void setNickname(View v){
		// Retrieve new nickname and create a result
		String newNickname = editText.getText().toString();
		Intent intent = new Intent(this, MainTabFragmentActivity.class);
		intent.putExtra(FriendListFragment.EXTRA_NICKNAME, newNickname);
		intent.putExtra(FriendListFragment.EXTRA_POSITION, position);

		// Set the result to be returned
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
}
