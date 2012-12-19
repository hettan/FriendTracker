package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

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
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
    	//Update All settings to the Config Class
    	
    	//Visibility
    	if("set_visibility".equals(key)) {
    		Log.v(TAG,key);
    		Boolean bool = sharedPreferences.getBoolean("set_visibility", true);
    		JSONObject toServer = new JSONObject();
			JSONObject data = new JSONObject();
			try {
				data.put("username", Config.USERNAME);
				data.put("visible",bool);
				toServer.put("type", "setVisible");
				toServer.put("data", data);
				Log.v(TAG,"qewqeweqeqweqweqew");
			} catch (Exception e) {
				
			}
			String toSend = toServer.toString();
			try {
		        @SuppressWarnings("rawtypes")
				Class[] params = {String.class, Boolean.class};
		        Log.v(TAG,"asdsadsadasdsadsad");
				ConnectionData connData = new ConnectionData(Setting.class.getMethod("Callback", params), this, toSend);
				new ConnectionHandler().execute(connData);
			}
			catch(Exception e) {
				Log.v(TAG, "Error: " + e.toString());
			}
    	}
    	else if("updates_interval".equals(key)) {
    		Log.v(TAG,key);
    		int tempi = 6666;
    		String temps = sharedPreferences.getString("updates_interval", "1000");
    		try {
    		    tempi = Integer.parseInt(temps);
    		} catch(NumberFormatException nfe) {
    			Log.v(TAG,"fucked int parse");
    		}
    		Config.USER_POSITION_UPDATE_INTERVAL = tempi;
    	}
    	else if("status_msg".equals(key)) {
    		Log.v(TAG,key);
    	}
    	
    	Log.v(TAG,"Changed " + findPreference(key));
    }
    public void Callback(String res, Boolean error) {
    	Log.v(TAG, "Callback");
    	try {
    	}
    	catch (Exception e) {
    			
    	}
    }
    public void init() {
    	Log.v(TAG,"intit launch");
    	//Initiate the local App settings
    	//Updateinterval
    	SharedPreferences appPref = PreferenceManager.getDefaultSharedPreferences(this);
    	int tempi = 6666;
		String temps = appPref.getString("updates_interval", "1000");
		try {
		    tempi = Integer.parseInt(temps);
		} catch(NumberFormatException nfe) {
			Log.v(TAG,"fucked int parse");
		}
		Config.USER_POSITION_UPDATE_INTERVAL = tempi;
		
		//Visibility
		Boolean bool = appPref.getBoolean("set_visibility", true);
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			data.put("visible",bool);
			toServer.put("type", "setVisible");
			toServer.put("data", data);
			Log.v(TAG,"qewqeweqeqweqweqew");
		} catch (Exception e) {
			
		}
		String toSend = toServer.toString();
		try {
	        @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
	        Log.v(TAG,"asdsadsadasdsadsad");
			ConnectionData connData = new ConnectionData(Setting.class.getMethod("Callback", params), this, toSend);
			new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
    }
}
