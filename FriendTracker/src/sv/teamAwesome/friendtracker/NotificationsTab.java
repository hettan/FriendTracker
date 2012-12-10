package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabContentFactory;

public class NotificationsTab extends TabActivity{
	private static final String TAG = "NOT";
	private static final String LIST1_TAB_TAG = "All";
	private static final String LIST2_TAB_TAG = "FriendRequests";
	private static final String LIST3_TAB_TAG = "GroupRequests";
	private static final String LIST4_TAB_TAG = "Buzz";
	
	TabHost tabHost;
	ListView tabAll;
	ListView tabFriendReq;
	ListView tabGroupReq;
	ListView tabBuzz;
	
	NotificationManager noteManager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
		
	/*
	 * ID
	 * 1	FriendRequest
	 * 2	GroupRequest
	 * 3	Buzz
	 * noteManager.cancel(id)
	 *
	 */
	
		// TODO Auto-generated method stub
		protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
		
	    final Object me = this;
	    tabHost = (TabHost)findViewById(android.R.id.tabhost);
	    tabAll = (ListView) findViewById(R.id.tabAll);
	    tabFriendReq = (ListView) findViewById(R.id.tabFriendReq);
	    tabGroupReq = (ListView) findViewById(R.id.tabGroupReq);
	    tabBuzz = (ListView) findViewById(R.id.tabBuzz);
	       
		tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return tabAll;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG).setIndicator(LIST2_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return tabFriendReq;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(LIST3_TAB_TAG).setIndicator(LIST3_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return tabGroupReq;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(LIST4_TAB_TAG).setIndicator(LIST4_TAB_TAG).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return tabBuzz;
			}
		}));
		
		tabAll.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				noteManager.cancel(1);
			}
		});
		tabFriendReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				noteManager.cancel(1);
			}
		});
		tabGroupReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				noteManager.cancel(2);
			}
		});
		tabBuzz.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				noteManager.cancel(3);
			}
		});
		
		tabHost.setCurrentTabByTag("All");
     	JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			toServer.put("type", "getFriendReq");
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
					//listView1.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listActive));
				} catch(Exception e){
					Log.v(TAG, "Error");
				}
			} else {
				Log.v(TAG, "Error");
			}
		}
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, 0, 1, "Clear All Notifications");
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (item.getItemId() == 0) {
				NotificationManager nm = (NotificationManager)
			            getSystemService(NOTIFICATION_SERVICE);
				nm.cancelAll();
			}
			return true;
		}
}
