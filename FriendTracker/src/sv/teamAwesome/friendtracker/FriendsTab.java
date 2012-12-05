package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.ArrayAdapter;
import android.widget.TabHost.TabContentFactory;

public class FriendsTab extends TabActivity {
	private static final String TAG = "FRIENDS";
	private static final String LIST1_TAB_TAG = "Active";
	private static final String LIST2_TAB_TAG = "All";
	private static final String LIST3_TAB_TAG = "Requests";
    TabHost tabHost;
	final Object me = this;
	  private ListView listView1;
	  private ListView listView2;
	  private ListView listView3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

 
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        //FriendsAppTabs.setMyTabs(tabHost, this);
        listView1 = (ListView) findViewById(R.id.list1);
        listView2 = (ListView) findViewById(R.id.list2);
        listView3 = (ListView) findViewById(R.id.list3);
        
		tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView1;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG).setIndicator(LIST2_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView2;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(LIST3_TAB_TAG).setIndicator(LIST3_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView3;
			}
		}));
        
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
				String[] listAll = new String[friends.length()];
				List<String> listActive = new ArrayList<String>();
				String username = "";
				for(int i=0; i < friends.length();i++){
					username = friends.getJSONObject(i).getString("username");
					listAll[i] = username;
					if (friends.getJSONObject(i).getBoolean("active")) {
						listActive.add(username);
					}
				}
				listView1.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listActive));
				listView2.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listAll));

				JSONArray requests = data.getJSONArray("requests");
				List<String> listReq = new ArrayList<String>();
				for(int i=0; i < requests.length();i++){
					listReq.add(requests.getJSONObject(i).getString("requester"));
				}
				listView3.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listActive));
				
			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}

		} else {
			Log.v(TAG, "Error");
		}
	}
	
}
