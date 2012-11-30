package edu.illinois.whereru;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Friend settings activity.
 * One preference is shared by all the friend objects.
 * TODO:1. Make marker type preference a list of drawables instead of strings
 * 
 * @author don
 *
 */
public class FriendSettingsActivity extends PreferenceActivity{

	Intent result;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);

		result = getIntent();
		resetSettings();
	}


	@Override
	public void onBackPressed() {
		/* Return the result before exiting out of the setting */
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		int newNum = Integer.parseInt(pref
				.getString(
						getResources().getString(
								R.string.pref_marker_number_pref), "3"));
		int newType = Integer.parseInt(pref.getString(
				getResources().getString(R.string.pref_marker_type_pref), "0"));

		result.putExtra(FriendListFragment.EXTRA_MARKER_NUM, newNum);
		result.putExtra(FriendListFragment.EXTRA_MARKER_TYPE, newType);
		setResult(Activity.RESULT_OK, result);

		super.onBackPressed();
	}
	
	/* Sets the state of settings to current friend's setting */
	private void resetSettings(){
		Bundle bundle = result.getExtras();
		int markerNum = bundle.getInt(FriendListFragment.EXTRA_MARKER_NUM);
		int markerType = bundle.getInt(FriendListFragment.EXTRA_MARKER_TYPE);
		
		ListPreference numPref = (ListPreference) findPreference(getResources().getString(R.string.pref_marker_number_pref));
		ListPreference typePref = (ListPreference) findPreference(getResources().getString(R.string.pref_marker_type_pref));
		
		numPref.setValue(markerNum+"");
		typePref.setValue(markerType+"");
	}
}
