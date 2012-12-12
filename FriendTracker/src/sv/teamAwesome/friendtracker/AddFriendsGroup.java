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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class AddFriendsGroup extends Activity {
	private static final String TAG = "ADDFRIENDS";
	  private ListView listv;
	  
	  private ArrayAdapter<String> lv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.addfriendsgroup);
        listv = (ListView) findViewById(R.id.addFriendG);
		
		final Object me = this;
		
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			data.put("groupID", Config.selectedGroupID);
			toServer.put("type", "getFriendsNotGroup");
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
		
		listv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView parent, View view, int position, long id) {
				String item = (String) listv.getAdapter().getItem(position);
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
					lv1.remove(item);
					lv1.notifyDataSetChanged();
				}
			}
		});
		
		Button done = (Button) findViewById(R.id.addFriendDone);
		done.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	public void Callback(String res, Boolean err) {
		try {
			JSONArray friends = new JSONArray(res);
			//String[] listAll = new String[friends.length()];
			List<String> listAll = new ArrayList<String>();
			String username = "";
			for(int i=0; i < friends.length();i++) {
				username = friends.getString(i);
				listAll.add(username);
			}
			lv1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listAll);
			listv.setAdapter(lv1);
		} catch(Exception e) {
			
		}
	}
	public void CallbackAdd(String res, Boolean err) {
		
	}
}
		
