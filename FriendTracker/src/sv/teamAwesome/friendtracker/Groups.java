package sv.teamAwesome.friendtracker;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

public class Groups extends ListActivity {
	private static final String TAG = "GROUPS";
	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Object me = this;
	    
	    Log.v(TAG, "1");
		JSONObject toServer = new JSONObject();
		Log.v(TAG, "2");
		JSONObject data = new JSONObject();
		try {
			data.put("username", "1");
			toServer.put("type", "getGroups");
			toServer.put("data", data);
		} catch (Exception e) {
			Log.v(TAG, "JSON: " + e.toString());
		}
		Log.v(TAG, "3");
		String toSend = toServer.toString();
		Log.v(TAG, "4");
		try {
            Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(Groups.class.getMethod("UpdateGroups", params), me, toSend);
			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
	  }
	/*
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//listView = (ListView) findViewById(R.id.grouplist);
		super.onCreate(savedInstanceState);
		Log.v(TAG, "0");
		setContentView(R.layout.groups);
		/*setListAdapter(new ArrayAdapter<String>(this,
                   		android.R.layout.simple_list_item_1,
                   		new ArrayList<String>()));*//*
		Log.v(TAG, "1");
		JSONObject toServer = new JSONObject();
		Log.v(TAG, "2");
		JSONObject data = new JSONObject();
		try {
			data.put("username", "1");
			toServer.put("type", "getGroups");
			toServer.put("data", data);
		} catch (Exception e) {
			Log.v(TAG, "JSON: " + e.toString());
		}
		Log.v(TAG, "3");
		String toSend = toServer.toString();
		Log.v(TAG, "4");
		try {
            Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(Groups.class.getMethod("UpdateGroups", params), Groups.class.newInstance(), toSend);
			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
		
	}*/
	public void UpdateGroups(String res, Boolean error) {

		Log.v(TAG, "Callback: " + res);
		if(!error) {
			try {
				Log.v(TAG, "10");
				JSONArray data = new JSONArray(res);
				Log.v(TAG, "11");
				String[] list = new String[data.length()];
				//ArrayAdapter<String> durp =  (ArrayAdapter<String>)getListAdapter();
				Log.v(TAG, "12"+data.toString());
				for(int i=0; i < (data.length());i++){
					Log.v(TAG, "13"+data.getString(i));
					list[i] = data.getString(i);
				}
				this.setListAdapter(new ArrayAdapter<String>(this, R.layout.groups, R.id.label, list));
			    
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

		} else {
			Log.v(TAG, "Error");
		}
	}

}