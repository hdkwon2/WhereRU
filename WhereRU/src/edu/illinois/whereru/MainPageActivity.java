package edu.illinois.whereru;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
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

public class MainPageActivity extends Activity{

	public static final String PREFERENCE_NAME = "edu.illinois.user_info_pref";
	public static final String PREF_USER_NAME = "user_name";
	public static final String PREF_DEVICE_ID = "device_id";
	public static final String PREF_APPLICATION_STATE = "application_state";
	public static final String PREF_USER_ID = "user_id";
	private static final String DEBUG_TAG = "[MainPageActivity]";
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_INSERT_ID = "insert_id";
	public static final String TAG_FRIENDS_INFO = "friends_info";
	public static final String TAG_FRIEND_ID = "friend_id";
	public static final String TAG_LOCATION = "location";
	public static final String TAG_TIME_STAMP = "time_stamp";
	
	private EditText editText;
	private SharedPreferences userInfo;
	private Editor preferenceEditor;
	private AlertDialog dialog;
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_page);
		
		intent = new Intent(this,GoogleMapActivity.class);
		userInfo = getSharedPreferences(PREFERENCE_NAME, 0);	
		boolean firstStart = userInfo.getBoolean(PREF_APPLICATION_STATE, true);
		
		// If it is the first time starting this application, load up registration dialog
		if(!firstStart)	showRegistrationDialog();			
		
		else{
			// Load friends information from DB
			getUserInfoFromDB();	
//			intent.putExtra()
			// Give sometime to brag about the main page.
			Timer timer= new Timer();
			timer.schedule(new TimerTask(){

				@Override
				public void run() {
					// start GoogleMapActivity
					startActivity(intent);
					// terminate the main page
					// without this back button would start this activity again.
					finish();
				}
				
			}, 2000);
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
				
				if (result != null) {
					try {
						successful = result.getInt(TAG_SUCCESS) == 1;
						userId = result.getInt(TAG_INSERT_ID) + "";
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				// Store user info locally
				if(successful){
					// user id returned from DB
					preferenceEditor.putString(PREF_USER_ID, userId);
					preferenceEditor.putString(PREF_USER_NAME, userName);
					preferenceEditor.putString(PREF_DEVICE_ID, deviceID);
					preferenceEditor.putBoolean(PREF_APPLICATION_STATE, false);
					preferenceEditor.commit();
					// start googlemapactivity
					startActivity(intent); 
					finish();
				} else{
					// notify user of failure, let users press cancle or back button to exit.
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
	
	/*
	 * Connec to DB and get user info
	 * parse the json file here, pass the locations and usernames to the map activity
	 * Gets the data before starting map activity
	 */
	private void getUserInfoFromDB(){
		DBConnector dBConnector =  new DBConnector();
		dBConnector.execute(DBConnector.GET_USER_INFO, userInfo.getString(PREF_USER_ID, "-1"));
		
		JSONObject result = null;
		
		try {
			result = dBConnector.get();
		} catch (InterruptedException ignored) {
			//Ignored if interrupted
			Log.e(DEBUG_TAG, "Interrupted");
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		boolean successful = false;
		try {
			if (result != null) {

				successful = result.getInt(TAG_SUCCESS) == 1;
				if (successful) {
//					result.getJSONArray(TAG_FRIENDS_INFO).g
				} else {
//					finish();
				}
			}
		} catch (JSONException e) {

		}
	}
}
