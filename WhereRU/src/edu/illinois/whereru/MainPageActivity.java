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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Start up activity.
 * Pops up a registration dialog when the application starts up for the first time.
 * Otherwise do some background DB connections before loading the main tab activity.
 * 
 * @author don
 *
 */
public class MainPageActivity extends Activity{

	public static final String PREFERENCE_NAME = "edu.illinois.user_info_pref";
	public static final String PREF_USER_NAME = "user_name";
	public static final String PREF_DEVICE_ID = "device_id";
	public static final String PREF_APPLICATION_STATE = "application_state";
	public static final String PREF_USER_ID = "user_id";
	private static final String DEBUG_TAG = "[MainPageActivity]";
	
	private EditText editText;
	private SharedPreferences userInfo;
	private Editor preferenceEditor;
	private AlertDialog dialog;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		
		intent = new Intent(this,MainTabFragmentActivity.class);
		userInfo = getSharedPreferences(PREFERENCE_NAME, 0);	
		boolean firstStart = userInfo.getBoolean(PREF_APPLICATION_STATE, true);
		
		// If it is the first time starting this application, load up registration dialog
		if(firstStart)	showRegistrationDialog();			
		
		else{ 

			// Give sometime to brag about the main page.
			Timer timer= new Timer();
			timer.schedule(new TimerTask(){

				@Override
				public void run() {
					// start main tab fragment activity
					startActivity(intent);
					// terminate the main page
					// without this, back button would start this activity again.
					finish();
				}
				
			}, 2000);
			
			// build friend info from DB
			FriendBuilder.build(getApplicationContext());
		}
		
	}
	
	private void showRegistrationDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater factory = LayoutInflater.from(this);
		// inflate dialog view
		View view = factory.inflate(R.layout.dialog_user_registration, null);
		// Setting dialog message and title
		builder.setMessage(R.string.register_dialog_message).setTitle(R.string.register_dialog_title);
		// set custom view
		builder.setView(view);
		
		// need to get the edittext from dialog layout
		// calling findViewById automatically searches the layout attached to this activity
		editText = (EditText) view.findViewById(R.id.dialog_username_text);
		preferenceEditor = userInfo.edit();
		
		builder.setPositiveButton(R.string.register_button_text, new OnClickListener(){

			public void onClick(DialogInterface dialogs, int id) {
				String userName = editText.getText().toString();
				// TODO need to figure out a way to define a user with unique id
				String deviceID = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
				
				// Connect to DB and store a new user
				DBConnector dbConnector = new DBConnector();
				dbConnector.execute(DBConnector.ADD_USER, userName, deviceID);
				JSONObject result = null;
				
				// Wait for the feedback from the DB
				try {
					result = dbConnector.get();
				} catch (InterruptedException e) {
					// shouldn't be interrupted
					Log.e(DEBUG_TAG, "Interrupted, restarting");		
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				
				boolean successful = false;
				String userId = null;
				
				// Parse the response returned from DB
				if (result != null) {
					successful = JSONObjectParser.getSuccess(result);
					userId = JSONObjectParser.getUserId(result)+"";
				}
				
				
				if(successful){ // Store user info locally
					// user id returned from DB
					preferenceEditor.putString(PREF_USER_ID, userId);
					preferenceEditor.putString(PREF_USER_NAME, userName);
					preferenceEditor.putString(PREF_DEVICE_ID, deviceID);
					preferenceEditor.putBoolean(PREF_APPLICATION_STATE, false);
					preferenceEditor.commit();
					// start main tab activity
					startActivity(intent); 
					finish();
				} else{
					// notify users of failure, let users press back button to exit.
							Toast.makeText(
									getApplicationContext(),
									"Failed to register, please try again later",
									Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		builder.setNegativeButton(R.string.register_dialog_cancel, new OnClickListener(){
			
			public void onClick(DialogInterface dialog, int which) {
				// exit out of this activity
				finish();
			}
			
		});
		
		dialog = builder.create();
		dialog.show();
	}


}
