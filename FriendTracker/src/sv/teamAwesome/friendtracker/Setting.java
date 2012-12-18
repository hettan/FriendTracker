package sv.teamAwesome.friendtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "SET";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.xml.preferences);        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "Reset Settings To Default");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                finish();
                startActivity(new Intent(this, ResetSettings.class));
                return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    // Register and Unregister to prevent the listener from being garbage-collected
    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes. 
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	//Update All settings to the Config Class
    	
    	//Visibility
    	
    	
    	//Interval
    	String updateInterval = prefs.getString("updates_interval", "-1");
    	int temp = Integer.parseInt(updateInterval);
    	Config.USER_POSITION_UPDATE_INTERVAL = temp;
    	
    	//Status
    	Config.STATUS = prefs.getString("status_msg", "Using this awesome new App, its great!");
    	
    	Log.v(TAG,"Changed " + findPreference(key));
    }


}
