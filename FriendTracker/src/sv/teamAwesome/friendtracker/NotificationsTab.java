package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.ProgressDialog;
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
	private static final String LIST2_TAB_TAG = "Friend";
	private static final String LIST3_TAB_TAG = "Group";
	private static final String LIST4_TAB_TAG = "Buzz";
	
	TabHost tabHost;
	private ProgressDialog dialog;
	private ListView tabAll;
	private ListView tabFriendReq;
	private ListView tabGroupReq;
	private ListView tabBuzz;
	private SimpleAdapter adapterAll;
	private SimpleAdapter adapterFriend;
	private SimpleAdapter adapterGroup;
	private SimpleAdapter adapterBuzz;
	NotificationManager noteManager;
    final Object me = this;
    
    
    private List<HashMap<String,String>> listGroup;
	
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

		final Bundle extras = getIntent().getExtras();
		Log.v(TAG, "extras:" + extras.getString("type"));
        tabHost.setCurrentTabByTag(extras.getString("type"));

        noteManager.cancel(extras.getString("user"), extras.getInt("cat"));
        	
		tabAll.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String,String> item = (HashMap<String,String>) tabAll.getAdapter().getItem(position);
				if (item.get("Info").equals("group")) {
					Intent goDia = new Intent(getBaseContext(), DialogAcceptGroup.class);
					String groupID = listGroup.get(position).get("groupID");
					goDia.putExtra("groupID", groupID);
					Config.temp = me;
					//Config.itemPos = position;
					startActivity(goDia);
				}
				else {
					Intent goDia = new Intent(getBaseContext(), DialogNotiTab.class);
					goDia.putExtra("item", item.get("Header"));
					Config.temp = me;
					//Config.itemPos = position;
					startActivity(goDia);
				}
			}
		});
		tabFriendReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogNotiTab.class);
				
				@SuppressWarnings("unchecked")
				HashMap<String,String> item = (HashMap<String,String>) tabFriendReq.getAdapter().getItem(position);
				Log.v(TAG, "pos:"+position);
				Log.v(TAG, "header: "+item.get("Header"));
				goDia.putExtra("item", item.get("Header"));
				Config.temp = me;
				//Config.itemPos = position;
				startActivity(goDia);
			}
		});
		
		tabGroupReq.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogAcceptGroup.class);
				
				//String item = (String) tabGroupReq.getAdapter().getItem(position);
				String groupID = listGroup.remove(position).get("groupID");
				goDia.putExtra("groupID", groupID);
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
					tabAll.setAdapter(adapterAll);
					tabFriendReq.setAdapter(null);
					tabGroupReq.setAdapter(null);
					tabBuzz.setAdapter(null);
			    	noteManager.cancelAll();
			    }
			    else if (i ==1) {
					tabAll.setAdapter(null);
					tabFriendReq.setAdapter(adapterFriend);
					tabGroupReq.setAdapter(null);
					tabBuzz.setAdapter(null);
					noteManager.cancel(extras.getString("user"), 1);
			    }
			    else if (i ==2) {
					tabAll.setAdapter(null);
					tabFriendReq.setAdapter(null);
					tabGroupReq.setAdapter(adapterGroup);
					tabBuzz.setAdapter(null);
					noteManager.cancel(extras.getString("user"), 2);
			    }
			    else if (i ==3) {
					tabAll.setAdapter(null);
					tabFriendReq.setAdapter(null);
					tabGroupReq.setAdapter(null);
					tabBuzz.setAdapter(adapterBuzz);
					noteManager.cancel(extras.getString("user"), 3);
			    }
		    	Update();
			  }
			});
		Update();
		}
		
		public void Update() {
			loading();
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
					listGroup = new ArrayList<HashMap<String,String>>();
					List<HashMap<String,String>> listBuzz = new ArrayList<HashMap<String,String>>();
					for(int i=0; i < data.length();i++){
						HashMap<String,String> map = new HashMap<String,String>();
						map.put("Header", data.getJSONObject(i).getString("requester"));
						map.put("Info", data.getJSONObject(i).getString("type"));
						map.put("Tstamp", data.getJSONObject(i).getString("time"));
						
						if (data.getJSONObject(i).getString("type").contentEquals("friend")) {
							listFriend.add(map);
						}
						if (data.getJSONObject(i).getString("type").contentEquals("group")) {
							map.put("groupID", data.getJSONObject(i).getString("groupID"));
							listGroup.add(map);
						}
						if (data.getJSONObject(i).getString("type").contentEquals("buzz")) {
							listBuzz.add(map);
						}
						listAll.add(map);
					}
					adapterAll = new SimpleAdapter(this,listAll,R.xml.notificationitem,from,to);
					adapterFriend = new SimpleAdapter(this,listFriend,R.xml.notificationitem,from,to);
					adapterGroup = new SimpleAdapter(this,listGroup,R.xml.notificationitem,from,to);
					adapterBuzz = new SimpleAdapter(this,listBuzz,R.xml.notificationitem,from,to);
					
					int tab = tabHost.getCurrentTab();
					if (tab == 0) {
						tabAll.setAdapter(adapterAll);
						tabFriendReq.setAdapter(null);
						tabGroupReq.setAdapter(null);
						tabBuzz.setAdapter(null);
					}
					else if (tab == 1) {
						tabAll.setAdapter(null);
						tabFriendReq.setAdapter(adapterFriend);
						tabGroupReq.setAdapter(null);
						tabBuzz.setAdapter(null);
					}
					else if (tab == 2) {
						tabAll.setAdapter(null);
						tabFriendReq.setAdapter(null);
						tabGroupReq.setAdapter(adapterGroup);
						tabBuzz.setAdapter(null);
					}
					else {
						tabAll.setAdapter(null);
						tabFriendReq.setAdapter(null);
						tabGroupReq.setAdapter(null);
						tabBuzz.setAdapter(adapterBuzz);
					}
					
					noteManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					
				} catch(Exception e){
					Log.v(TAG, "Error: "+ e.toString());
					Log.v(TAG, "Cause: "+ e.getCause());
				}

			} else {
				Log.v(TAG, "Error");
			}
			dialog.dismiss();
		}
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, 0, 1, "Clear All Notifications");
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (item.getItemId() == 0) {
				loading();
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
			dialog.dismiss();
		}
		public void CallbackGroup(String res, Boolean error) {
			Log.v(TAG, "Callback: " + res);
			if(!error) {
				try {
					Update();
				}
				catch(Exception e){
					Log.v(TAG, "Error: "+ e.toString());
					Log.v(TAG, "Cause: "+ e.getCause());
				}
			}
			dialog.dismiss();
		}
		public void loading() {
			dialog = ProgressDialog.show(this, "Loading", "Waiting for server");
		}
}
