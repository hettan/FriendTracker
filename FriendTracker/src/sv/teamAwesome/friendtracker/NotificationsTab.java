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
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class NotificationsTab extends TabActivity{
	private static final String TAG = "NOT";
	private static final String LIST1_TAB_TAG = "All";
	private static final String LIST2_TAB_TAG = "FriendRequests";
	private static final String LIST3_TAB_TAG = "GroupRequests";
	private static final String LIST4_TAB_TAG = "Buzz";
	
	TabHost tabHost;
	private ListView tabAll;
	private ListView tabFriendReq;
	private ListView tabGroupReq;
	private ListView tabBuzz;
	
	/*
	 * ID
	 * 1	FriendRequest
	 * 2	GroupRequest
	 * 3	Buzz
	 * noteManager.cancel(id)
	 *
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final NotificationManager noteManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Log.v(TAG, "Starting NotiTab");
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

		Bundle extras = getIntent().getExtras();
		Log.v(TAG, "extras:" + extras.getString("type"));
        tabHost.setCurrentTabByTag(extras.getString("type"));
        
        /*
		tabAll.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
			}
		});
		tabFriendReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
			}
		});
		
		tabGroupReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
			}
		});
		tabBuzz.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
			}
		});
		*/
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
			int i = getTabHost().getCurrentTab();
			    if (i == 0) {
			    	noteManager.cancelAll();
			    }
			    else if (i ==1) {
					Log.v(TAG, "tab1");
					noteManager.cancel(1);
			    }
			    else if (i ==2) {
					Log.v(TAG, "tab2");
					noteManager.cancel(2);
			    }
			    else if (i ==3) {
					Log.v(TAG, "tab3");
					noteManager.cancel(3);
			    }
			  }
			});
	}
		/*JSONObject toServer = new JSONObject();
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
				
		 	ConnectionData connData = new ConnectionData(NotificationsTab.class.getMethod("Callback", params), me, toSend);

			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
			}
			catch(Exception e) {
				Log.v(TAG, "Error: " + e.toString());
			}
		}*/
		
		public void Callback(String res, Boolean error) {
			Log.v(TAG, "Callback: " + res);
			if(!error) {
				try {
					String[] derp = new String[1];
					derp[0] = "asdad";
					tabAll.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,derp));
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
