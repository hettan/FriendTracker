package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AddFriendsGroup extends Activity {
	private static final String TAG = "ADDFRIENDS";
	  private ListView listView1;
	  
	  private ArrayAdapter<String> lv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.addfriendsgroup);
        listView1 = (ListView) findViewById(R.id.addFriendG);
		
		final Object me = this;
		
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
				
		 	ConnectionData connData = new ConnectionData(AddFriendsGroup.class.getMethod("Callback", params), me, toSend);
			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		} catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
		
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				String item = (String) listView1.getAdapter().getItem(position);
				if(item != null) {			
					JSONObject toServer = new JSONObject();
					JSONObject data = new JSONObject();
					try {
						data.put("username", Config.USERNAME);
						data.put("groupID", Config.selectedGroupID);
						data.put("target", item);
						toServer.put("type", "addGroupMember");
						toServer.put("data", data);
					} catch (Exception e) {
						
					}
					String toSend = toServer.toString();
					Log.v(TAG, "me= " + me.toString());
					try {
			            Class[] params = {String.class, Boolean.class};
						
						ConnectionData connData = new ConnectionData(AddFriendsGroup.class.getMethod("CallbackAdd", params), me, toSend);

						AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
					} catch(Exception e) {
						Log.v(TAG, "Error: " + e.toString());
					}
					//lv1.remove((String)listView1.getAdapter().getItem(position));
					//lv1.notifyDataSetChanged();

				}
			}
		});
		
	}
	public void Callback(String res, Boolean err) {
		try {
			JSONObject data = new JSONObject(res);	
			JSONArray friends = data.getJSONArray("friends");
			String[] listAll = new String[friends.length()];
			List<String> listActive = new ArrayList<String>();
			String username = "";
			for(int i=0; i < friends.length();i++) {
				username = friends.getJSONObject(i).getString("username");
				listAll[i] = username;
				if (friends.getJSONObject(i).getBoolean("active")) {
					listActive.add(username);
				}
			}
			lv1 = new ArrayAdapter(this, R.layout.groups_list_item,listAll);
			listView1.setAdapter(lv1);
		} catch(Exception e) {
			
		}
	}
	public void CallbackAdd(String res, Boolean err) {
		
	}
}
		
