package sv.teamAwesome.friendtracker;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.ArrayAdapter;

public class FriendsTab extends TabActivity {
	private static final String TAG = "FRIENDS";
    TabHost tabHost;
	final Object me = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

 
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        FriendsAppTabs.setMyTabs(tabHost, this);
        
     	JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			toServer.put("type", "getFriends");
			toServer.put("data", data);
		} catch (Exception e) {
				
		}
		String toSend = toServer.toString();
		try {
	        Class[] params = {String.class, Boolean.class};
				
		 	ConnectionData connData = new ConnectionData(FriendsTab.class.getMethod("Callback", params), me, toSend);

			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
			}
			catch(Exception e) {
				Log.v(TAG, "Error: " + e.toString());
		}
    }
    
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			try {
				Log.v(TAG, "10");
				//JSONArray data = new JSONArray(res);
				JSONObject data = new JSONObject(res);
				JSONArray friends = data.getJSONArray("friends");
				JSONArray requests = data.getJSONArray("requests");
				String[] listAll = new String[friends.length()];
				//String[] listActive = new String[friends.length()];
				for(int i=0; i < friends.length();i++){
					listAll[i] = friends.getJSONObject(i).getString("name");
					//if (friends.getJSONObject(i).getBoolean("active"))
				}
			   ((ListActivity) Config.andersMamma).setListAdapter(new ArrayAdapter<String>(this, R.layout.friends, R.id.label2, listAll));
				
			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}

		} else {
			Log.v(TAG, "Error");
		}
	}
	
}
