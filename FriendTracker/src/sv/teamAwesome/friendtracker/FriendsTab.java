package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Intent;
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

	  final Object me = this;
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);
 
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
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogRemFriend.class);
				String item = (String) listView1.getAdapter().getItem(position);
				goDia.putExtra("item", item);
				Config.temp = me;
				startActivity(goDia);
			}
		});
		listView2.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogRemFriend.class);
				String item = (String) listView2.getAdapter().getItem(position);
				goDia.putExtra("item", item);
				Config.temp = me;
				startActivity(goDia);
			}
		});
		listView3.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent goDia = new Intent(getBaseContext(), DialogAct.class);	
				String item = (String) listView3.getAdapter().getItem(position);
				goDia.putExtra("item", item);
				Config.temp = me;
				startActivity(goDia);
			}
		});
		
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
					if(tabId.equals("Active")) {
						listView1.setAdapter(lv1);
						listView2.setAdapter(null);
						listView3.setAdapter(null);
					}
					else if (tabId.equals("All")) {
						listView2.setAdapter(lv2);
						listView1.setAdapter(null);
						listView3.setAdapter(null);
					}
					else {
						listView3.setAdapter(lv3);
						listView2.setAdapter(null);
						listView1.setAdapter(null);
					}
					getUpdates();
			}
		});
		getUpdates();
        tabHost.setCurrentTabByTag("All");

    }
    
    private void getUpdates() {
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
	        @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
				
		 	ConnectionData connData = new ConnectionData(FriendsTab.class.getMethod("Callback", params), me, toSend);
			new ConnectionHandler().execute(connData);
			
		} catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
    }
    
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);

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

				lv1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listActive);
				lv2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listAll);

				JSONArray requests = data.getJSONArray("requests");
				List<String> listReq = new ArrayList<String>();
				for(int i=0; i < requests.length();i++) {
					listReq.add(requests.getJSONObject(i).getString("requester"));
				}
				lv3 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listReq);
				
				//listView3.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listReq));
				
				int tab = tabHost.getCurrentTab();
				if (tab == 0) {
					listView1.setAdapter(lv1);
					listView2.setAdapter(null);
					listView3.setAdapter(null);
				}
				else if (tab == 1) {
					listView2.setAdapter(lv2);
					listView1.setAdapter(null);
					listView3.setAdapter(null);
				}
				else {
					listView3.setAdapter(lv3);	
					listView1.setAdapter(null);
					listView2.setAdapter(null);
				}
				
			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}

		} else {
			Log.v(TAG, "Error");
		}
	}

	public void CallbackRem(String res, Boolean error) {
		if(!error) {
			getUpdates();
		}
		else {	
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
			//finish();
		}
		return true;
	}

	
}
