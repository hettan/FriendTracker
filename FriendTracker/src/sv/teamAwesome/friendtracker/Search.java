package sv.teamAwesome.friendtracker;

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

public class Search extends Activity {
	private static final String TAG = "SEARCH";
	private ListView listView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		final Object me = this;
		
		final EditText searchtxt = (EditText) findViewById(R.id.searchTxt);
		Button searchbtn = (Button) findViewById(R.id.searchBtn);
		listView1 = (ListView) findViewById(R.id.list1);
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				String item = (String) listView1.getAdapter().getItem(position);
				if(item != null) {			
					JSONObject toServer = new JSONObject();
					JSONObject data = new JSONObject();
					try {
						data.put("src", Config.USERNAME);
						data.put("target", searchtxt.getText().toString());
						toServer.put("type", "request");
						toServer.put("data", data);
					} catch (Exception e) {
						
					}
					String toSend = toServer.toString();
					try {
			            Class[] params = {String.class, Boolean.class};
						
						ConnectionData connData = new ConnectionData(Search.class.getMethod("CallbackReq", params), me, toSend);

						AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
					} catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}
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
			
				JSONArray data = new JSONArray(res);
				String[] listRes = new String[data.length()];
				String item;
				for(int i = 0; i < data.length(); i++) {
					item = data.getString(i);
					listRes[i] = item.substring(2,item.length()-1); //need to remove u'' 
				}
				
				
				listView1.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1,listRes));
				
				
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
