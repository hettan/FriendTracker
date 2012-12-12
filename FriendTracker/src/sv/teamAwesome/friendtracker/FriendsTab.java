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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class FriendsTab extends TabActivity {
	private static final String TAG = "FRIENDS";
	private static final String LIST1_TAB_TAG = "Active";
	private static final String LIST2_TAB_TAG = "All";
	private static final String LIST3_TAB_TAG = "Requests";
    TabHost tabHost;
	  private ListView listView1;
	  private ListView listView2;
	  private ListView listView3;
	  
	  private ArrayAdapter<String> lv1;
	  private ArrayAdapter<String> lv2;
	  private ArrayAdapter<String> lv3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);
    	final Object me = this;
 
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
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
		
		listView3.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				String item = (String) listView3.getAdapter().getItem(position);
				if(item != null) {			
					JSONObject toServer = new JSONObject();
					JSONObject data = new JSONObject();
					try {
						data.put("src", Config.USERNAME);
						data.put("requester", item);
						data.put("type", "friend");
						toServer.put("type", "acceptReq");
						toServer.put("data", data);
					} catch (Exception e) {
						
					}
					String toSend = toServer.toString();
					Log.v(TAG, "me= " + me.toString());
					try {
			            Class[] params = {String.class, Boolean.class};
						
						ConnectionData connData = new ConnectionData(FriendsTab.class.getMethod("CallbackAccept", params), me, toSend);

						AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
					} catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}

					lv3.remove((String)listView3.getAdapter().getItem(position));
					lv3.notifyDataSetChanged();
				}
			}
		});
		
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				int i = getTabHost().getCurrentTab();
				if(i == 0) {
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
					} catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}
				} if(i == 1) {
				   	JSONObject toServer2 = new JSONObject();
					JSONObject data2 = new JSONObject();
					try {
						data2.put("username", Config.USERNAME);
						toServer2.put("type", "getFriends");
						toServer2.put("data", data2);
					} catch (Exception e) {
							
					}
					String toSend2 = toServer2.toString();
					try {
				        Class[] params = {String.class, Boolean.class};
							
					 	ConnectionData connData = new ConnectionData(FriendsTab.class.getMethod("Callback", params), me, toSend2);
						AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
					} catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}
				}
			}
		});
		
        tabHost.setCurrentTabByTag("All");
     	/*JSONObject toServer = new JSONObject();
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
		}*/
    }
    
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		//lv2.clear();
		//lv1.clear();
		if(!error) {
			try {
				Log.v(TAG, "10");
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

				lv1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listActive);
				lv2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listAll);

				JSONArray requests = data.getJSONArray("requests");
				List<String> listReq = new ArrayList<String>();
				for(int i=0; i < requests.length();i++) {
					listReq.add(requests.getJSONObject(i).getString("requester"));
				}
				lv3 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listReq);
				
				//listView3.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listReq));
				listView1.setAdapter(lv1);
				listView2.setAdapter(lv2);
				listView3.setAdapter(lv3);
				
			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}

		} else {
			Log.v(TAG, "Error");
		}
	}
	
	public void CallbackAccept(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(error) {
			Log.v(TAG, "Error");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 1, "Search");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			Intent search = new Intent("sv.teamAwesome.friendtracker.SEARCH");
			startActivity(search);
		}
		return true;
	}
	
}
