package sv.teamAwesome.friendtracker;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Group extends Activity {
	private static final String TAG = "GROUP";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.group);
		
		final Object me = this;
		
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			data.put("groupID", Config.selectedGroupID);
			toServer.put("type", "getGroupInfo");
			toServer.put("data", data);
		} catch (Exception e) {
			
		}
		String toSend = toServer.toString();
		try {
            Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(Group.class.getMethod("Callback", params), me, toSend);

			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
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
				Log.v(TAG, "11");
				JSONArray members = data.getJSONArray("members");
				String[] listMembers = new String[members.length()];

				Log.v(TAG, "12"+data.toString());
				for(int i=0; i < (members.length());i++){
					listMembers[i] = members.getString(i);
				}
				Log.v(TAG, "13");
				TextView tv1 = (TextView)findViewById(R.id.groupname);
				TextView tv2 = (TextView)findViewById(R.id.groupadmin);
				String text = data.getString("name");
				Log.v(TAG, "14" + text);

				tv1.setText(text);
				tv2.setText(data.getString("admin"));
				Log.v(TAG, "15");
				ListView lv = (ListView)findViewById(R.id.groupmembers);
				lv.setAdapter(new ArrayAdapter<String>(this, R.layout.group, listMembers));

				Log.v(TAG, "16");
				
			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}
		}
	}
}