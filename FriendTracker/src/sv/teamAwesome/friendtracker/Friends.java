
package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class Friends extends Activity {
	private static final String TAG = "FRIENDS";
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.friends);
		
		final Object me = this;
		
		final Button addFriend = (Button) findViewById(R.id.addFriendbtn);
		final EditText addFriendtxt = (EditText) findViewById(R.id.addFriendtxt);
		
		addFriend.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String addF = addFriendtxt.getText().toString();
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("src", Config.USERNAME);
					data.put("target", addF);
					toServer.put("type", "request");
					toServer.put("data", data);
				} catch (Exception e) {
					
				}
				String toSend = toServer.toString();
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), me, toSend);

					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
				addFriendtxt.setText("");
				
			}
		});
		
	}
}
