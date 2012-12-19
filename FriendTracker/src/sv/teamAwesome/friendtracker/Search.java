package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Search extends Activity {
	private static final String TAG = "SEARCH";
	private ListView listView1;
	private ArrayList<HashMap<String,String>> listRes;
	private SimpleAdapter adapter;
	private ArrayList<String> disabledItems;
	private String searcht;
	final Object me = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		//listRes = new ArrayList<HashMap<String,String>>();
		
		final Button done = (Button) findViewById(R.id.searchDoneBtn);
		done.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();	
			}
		});
		
		
		final EditText searchtxt = (EditText) findViewById(R.id.searchTxt);
		disabledItems = new ArrayList<String>();
		Button searchbtn = (Button) findViewById(R.id.searchBtn);
		listView1 = (ListView) findViewById(R.id.list1);
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String,String> selItem = (HashMap<String,String>) listView1.getAdapter().getItem(position);
				Log.v(TAG,"DERPMODE");
				String user = selItem.get("searchUser");
					Log.v(TAG,"DERPMODE2");
					disabledItems.add(user);
					if(selItem.get("searchRequest").length() == 0) {	
						Log.v(TAG,"DERPMODE3");
						//send request
						Intent go = new Intent(getBaseContext(),DialogAskFriend.class);
						String sendItem = user;
						go.putExtra("item", sendItem);
						go.putExtra("pos", position);
						Config.temp = me;
						startActivity(go);			
					}
					else {
						//remove request
						Intent go = new Intent(getBaseContext(),DialogCancelFriend.class);
						String sendItem = user;
						go.putExtra("item", sendItem);
						go.putExtra("pos", position);
						Config.temp = me;
						startActivity(go);
					}
			}
		});
			
		searchbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				searcht = searchtxt.getText().toString();
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchtxt.getWindowToken(), 0);
				getSearchRes();
			}
		});
		
	}
	
	public void getSearchRes() {
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
            @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(Search.class.getMethod("Callback", params), me, toSend);

			@SuppressWarnings("unused")
			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
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
			try {
				JSONObject data = new JSONObject(res);
				getSearchRes();
				/*
				JSONObject data = new JSONObject(res);
				HashMap<String,String> item = new HashMap<String,String>();
				item.put("searchUser", data.getString("username"));
				item.put("searchRequest", "Request Sent");
				listRes.set(data.getInt("itemPos"), item);
				disabledItems.remove(data.getString("username"));
				
				adapter.notifyDataSetChanged();
				*/
			} catch(Exception e){
				
			}
		} else {
			disabledItems.clear();
			Log.v(TAG, "Err");
		}
	}
	public void CallbackCancelReq(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Ej Err");
			try {
				getSearchRes();
				/*
				JSONObject data = new JSONObject(res);
				HashMap<String,String> item = new HashMap<String,String>();
				item.put("searchUser", data.getString("username"));
				item.put("searchRequest", "");
				listRes.set(data.getInt("itemPos"), item);
				disabledItems.remove(data.getString("username"));
				
				adapter.notifyDataSetChanged();*/
			} catch(Exception e){
				
			}
		} else {
			disabledItems.clear();
			Log.v(TAG, "Err");
		}
	}
}
