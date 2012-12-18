package sv.teamAwesome.friendtracker;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Groups extends Activity {
	private static final String TAG = "GROUPS";
	private ListView listViewGroups;
	private String[] groupIDs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.groups);
		final Object me = this;

		Log.v(TAG, "1");
		JSONObject toServer = new JSONObject();
		Log.v(TAG, "2");
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			toServer.put("type", "getGroups");
			toServer.put("data", data);
		} catch (Exception e) {
			Log.v(TAG, "JSON: " + e.toString());
		}
		Log.v(TAG, "3");
		String toSend = toServer.toString();
		Log.v(TAG, "4");
		try {
			@SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};

			ConnectionData connData = new ConnectionData(Groups.class.getMethod("UpdateGroups", params), me, toSend);
			@SuppressWarnings("unused")
			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
		listViewGroups = (ListView) findViewById(R.id.groupslist);
		listViewGroups.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Config.selectedGroupID = groupIDs[position];
				Intent group = new Intent("sv.teamAwesome.friendtracker.GROUP");
				startActivity(group);		
			}
		});
		
		
	}


	public void UpdateGroups(String res, Boolean error) {

		Log.v(TAG, "Callback: " + res);
		if(!error) {
			try {
				Log.v(TAG, "10");
				//JSONObject json = new JSONObject(res);
				JSONArray data = new JSONArray(res);
				Log.v(TAG, "11");
				JSONObject group;
				String[] list = new String[data.length()];
				groupIDs = new String[data.length()];
				//ArrayAdapter<String> durp =  (ArrayAdapter<String>)getListAdapter();
				Log.v(TAG, "12"+data.toString());
				for(int i=0; i < (data.length());i++){
					group = data.getJSONObject(i);
					Log.v(TAG, "13"+group);
					list[i] = group.getString("name");
					groupIDs[i] = group.getString("groupID");
				}

				ListView lv = (ListView)findViewById(R.id.groupslist);
				lv.setAdapter(new ArrayAdapter<String>(this, R.layout.groups_list_item, list));

				Log.v(TAG, "15");
				/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(that,
						  android.R.layout.simple_list_item_1, list);

						// Assign adapter to ListView
						listView.setAdapter(adapter); 
				 */
			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}
		}
	}
	public void CallbackReq(String res, Boolean error) {
		if(!error) {
			Log.v(TAG, "Res: " + res);
		} else {
			Log.v(TAG, "Error!");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 1, "Create Group");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			Intent create = new Intent("sv.teamAwesome.friendtracker.CREATEGROUPS");
			startActivity(create);
			finish();
		}
		return true;
	}
}




