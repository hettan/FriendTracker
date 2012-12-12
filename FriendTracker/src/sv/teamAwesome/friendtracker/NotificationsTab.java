package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
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
		
		final ShowPopUp pop = new ShowPopUp();
		
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
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				ShowPopUp pop = new ShowPopUp();
				pop.show(pop.getFragmentManager(),"PopTry");
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
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
			int i = getTabHost().getCurrentTab();
			    if (i == 0) {
			    	Update();
			    	noteManager.cancelAll();
			    }
			    else if (i ==1) {
					Update();
					noteManager.cancel(1);
			    }
			    else if (i ==2) {
					Update();
					noteManager.cancel(2);
			    }
			    else if (i ==3) {
					Update();
					noteManager.cancel(3);
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
	        	Class[] params = {String.class, Boolean.class};	
		 		ConnectionData connData = new ConnectionData(NotificationsTab.class.getMethod("Callback", params), me, toSend);
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
					Log.v(TAG, "NotiTry");
					
					String[] from = new String[]{"Header","Info","Tstamp"};
					int[] to = new int[] {R.id.Header,R.id.Info,R.id.Tstamp};
					
					JSONArray data = new JSONArray(res);
					
					List<HashMap<String,String>> listAll = new ArrayList<HashMap<String,String>>();
					List<String[]> listFriend = new ArrayList<String[]>();
					List<String[]> listGroup = new ArrayList<String[]>();
					List<String[]> listBuzz = new ArrayList<String[]>();
					String info;
					for(int i=0; i < data.length();i++){
						HashMap<String,String> map = new HashMap<String,String>();
						map.put("Header", data.getJSONObject(i).getString("requester"));
						map.put("Info", data.getJSONObject(i).getString("type"));
						map.put("Tstamp", data.getJSONObject(i).getString("time"));
						listAll.add(map);
						/*
						if (info[2].contentEquals("friend")) {
							listFriend.add(info);
						}
						else if (info[2].contentEquals("group")) {
							listGroup.add(info);
						}
						else if (info[2].contentEquals("buzz")) {
							listBuzz.add(info);
						}
						*/
					}
					
					SimpleAdapter adapter = new SimpleAdapter(this,listAll,R.xml.notificationitem,from,to);
					tabAll.setAdapter(adapter);
					
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
			        Class[] params = {String.class, Boolean.class};	
				 	ConnectionData connData = new ConnectionData(NotificationsTab.class.getMethod("CallbackClear", params), me, toSend);
					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
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
}
