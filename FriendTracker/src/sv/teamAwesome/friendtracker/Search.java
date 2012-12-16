package sv.teamAwesome.friendtracker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Search extends Activity {
	private static final String TAG = "SEARCH";
	private ListView listView1;
	private ArrayList<HashMap<String,String>> listRes;
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		//listRes = new ArrayList<HashMap<String,String>>();
		final Object me = this;
		
		final EditText searchtxt = (EditText) findViewById(R.id.searchTxt);
		Button searchbtn = (Button) findViewById(R.id.searchBtn);
		listView1 = (ListView) findViewById(R.id.list1);
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				HashMap<String,String> item = (HashMap<String,String>) listView1.getAdapter().getItem(position);
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				String status = "";
				try {
				if(item.get("searchRequest").length() == 0) {			
					data.put("src", Config.USERNAME);
					data.put("target", item.get("searchUser"));
					toServer.put("type", "request");
					toServer.put("data", data);
					status = "Request sent";
				}
				else {
					data.put("username", Config.USERNAME);
					data.put("target", item.get("searchUser"));
					data.put("type", "friend");
					toServer.put("type", "remRequest");
					toServer.put("data", data);
				}
					String toSend = toServer.toString();
					try {
			            Class[] params = {String.class, Boolean.class};
						
						ConnectionData connData = new ConnectionData(Search.class.getMethod("CallbackReq", params), me, toSend);

						AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
					} catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}
					if(listRes != null) {
						Log.v(TAG, "NOT NULL");
						//HashMap<String,String> temp = item;
						//HashMap<String,String> temp = listRes.get(position);
						item.put("searchRequest", status);
						listRes.set(position, item);
						//listRes.add(position, temp);
						//listRes.remove(position);
						adapter.notifyDataSetChanged();
					}
					Log.v(TAG, "NULL");
			} catch (Exception e) {
			}
			}
		});
		
		searchbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String searcht = searchtxt.getText().toString();
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("query", searcht);
					data.put("username", Config.USERNAME);
					toServer.put("type", "userSearch");
					toServer.put("data", data);
				} catch (Exception e) {
					
				}
				String toSend = toServer.toString();
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(Search.class.getMethod("Callback", params), me, toSend);

					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
			}
		});
		
	}
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Ej Err");
			try {
				String[] from = new String[]{"searchUser","searchRequest"};
				int[] to = new int[] {R.id.searchUser,R.id.searchRequest};
				
				listRes = new ArrayList<HashMap<String,String>>();
				JSONArray data = new JSONArray(res);
				//listRes = new ArrayList<String>();
				JSONObject jsonObj;
				for(int i = 0; i < data.length(); i++) {
					jsonObj = data.getJSONObject(i);
					HashMap<String,String> newItem = new HashMap<String,String>();
					newItem.put("searchUser", jsonObj.getString("username"));
					if (!jsonObj.getBoolean("requestSent")) {
						newItem.put("searchRequest", "");
					}
					else {
					newItem.put("searchRequest", "Request Sent");
					}
					listRes.add(newItem); //need to remove u'' 
				}
				adapter = new SimpleAdapter(this,listRes,R.xml.search_item,from,to);
				//arrAdapt = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listRes);
				listView1.setAdapter(adapter);
				
				
			} catch(Exception e) {
				Log.v(TAG, "Error: " + e.toString());
				Log.v(TAG, "Cause: " + e.getCause());
			}

		} else {
			Log.v(TAG, "Err");
		}
	}
	public void CallbackReq(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Ej Err");

		} else {
			Log.v(TAG, "Err");
		}
	}
}
