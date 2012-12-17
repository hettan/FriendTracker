package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
	NotificationManager noteManager;
    final Object me = this;
	
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
		setContentView(R.layout.notifications);
		
		noteManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	
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

		tabAll.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		tabFriendReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogAct.class);
				
				String item = (String) tabFriendReq.getAdapter().getItem(position);
				goDia.putExtra("item", item);
				Config.temp = me;
				Config.itemPos = position;
				startActivity(goDia);
			}
		});
		
		tabGroupReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogAcceptGroup.class);
				
				String item = (String) tabGroupReq.getAdapter().getItem(position);
				goDia.putExtra("item", item);
				Config.temp = me;
				Config.itemPos = position;
				startActivity(goDia);
			}
		});
		tabBuzz.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
			int i = getTabHost().getCurrentTab();
			    if (i == 0) {
			    	Update();
			    	noteManager.cancelAll();
			    }
			    else if (i ==1) {
					Update();
					noteManager.cancel(null, 1);
			    }
			    else if (i ==2) {
					Update();
					noteManager.cancel(null, 2);
			    }
			    else if (i ==3) {
					Update();
					noteManager.cancel(null, 3);
			    }
			  }
			});
		Update();
		}
		
		public void Update() {
			
			JSONObject toServer = new JSONObject();
			JSONObject data = new JSONObject();
			try {
				data.put("username", Config.USERNAME);
				toServer.put("type", "getRequests");
				toServer.put("data", data);
			} catch (Exception e) {
				
			}
			String toSend = toServer.toString();
			try {
	        	@SuppressWarnings("rawtypes")
				Class[] params = {String.class, Boolean.class};	
		 		ConnectionData connData = new ConnectionData(NotificationsTab.class.getMethod("Callback", params), me, toSend);
		 		new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
			}
		
		public void Callback(String res, Boolean error) {
			Log.v(TAG, "Callback: " + res);
			if(!error) {
				try {
					Log.v(TAG, "NotiTry");
					
					String[] from = new String[]{"Header","Info","Tstamp"};
					int[] to = new int[] {R.id.Header,R.id.Info,R.id.Tstamp};
					
					JSONArray data = new JSONArray(res);
					
					List<HashMap<String,String>> listAll = new ArrayList<HashMap<String,String>>();
					List<HashMap<String,String>> listFriend = new ArrayList<HashMap<String,String>>();
					List<HashMap<String,String>> listGroup = new ArrayList<HashMap<String,String>>();
					List<HashMap<String,String>> listBuzz = new ArrayList<HashMap<String,String>>();
					for(int i=0; i < data.length();i++){
						HashMap<String,String> map = new HashMap<String,String>();
						map.put("Header", data.getJSONObject(i).getString("requester"));
						map.put("Info", data.getJSONObject(i).getString("type"));
						map.put("Tstamp", data.getJSONObject(i).getString("time"));
						listAll.add(map);
						
						if (data.getJSONObject(i).getString("type").contentEquals("friend")) {
							listFriend.add(map);
						}
						if (data.getJSONObject(i).getString("type").contentEquals("group")) {
							listGroup.add(map);
						}
						if (data.getJSONObject(i).getString("type").contentEquals("buzz")) {
							listBuzz.add(map);
						}
						
					}
					
					SimpleAdapter adapter1 = new SimpleAdapter(this,listAll,R.xml.notificationitem,from,to);
					tabAll.setAdapter(adapter1);
					
					SimpleAdapter adapter2 = new SimpleAdapter(this,listFriend,R.xml.notificationitem,from,to);
					tabFriendReq.setAdapter(adapter2);
					
					SimpleAdapter adapter3 = new SimpleAdapter(this,listGroup,R.xml.notificationitem,from,to);
					tabGroupReq.setAdapter(adapter3);
					
					SimpleAdapter adapter4 = new SimpleAdapter(this,listBuzz,R.xml.notificationitem,from,to);
					tabBuzz.setAdapter(adapter4);
					
					noteManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					
				} catch(Exception e){
					Log.v(TAG, "Error: "+ e.toString());
					Log.v(TAG, "Cause: "+ e.getCause());
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
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("username", Config.USERNAME);
					toServer.put("type", "clearRequests");
					toServer.put("data", data);
				} catch (Exception e) {
						
				}
				String toSend = toServer.toString();
				try {
			        @SuppressWarnings("rawtypes")
					Class[] params = {String.class, Boolean.class};	
				 	ConnectionData connData = new ConnectionData(NotificationsTab.class.getMethod("CallbackClear", params), me, toSend);
					new ConnectionHandler().execute(connData);
					}
					catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}
			}
			return true;
		}
		public void CallbackClear(String res, Boolean error) {
			Log.v(TAG, "Callback: " + res);
			if(!error) {
				try {
					NotificationManager nm = (NotificationManager)
				            getSystemService(NOTIFICATION_SERVICE);
					nm.cancelAll();
					Update();
				}
				catch(Exception e){
					Log.v(TAG, "Error: "+ e.toString());
					Log.v(TAG, "Cause: "+ e.getCause());
				}
			}
		}
		public void CallbackGroup(String res, Boolean error) {
			Log.v(TAG, "Callback: " + res);
			if(!error) {
				try {
				}
				catch(Exception e){
					Log.v(TAG, "Error: "+ e.toString());
					Log.v(TAG, "Cause: "+ e.getCause());
				}
			}
		}
}
