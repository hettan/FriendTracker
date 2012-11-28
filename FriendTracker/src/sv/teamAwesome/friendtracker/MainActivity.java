package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
	private static final String TAG = "MAIN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		/*GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, "377927318664");
		}*/
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final EditText username = (EditText) findViewById(R.id.usernametv);
		final EditText password = (EditText) findViewById(R.id.passwordtv);
		Button login = (Button) findViewById(R.id.loginbtn);
		Button register = (Button) findViewById(R.id.regbtn);
		
		login.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//If something, something, then do something, something.
				String checkUser = username.getText().toString();
				String checkPass = password.getText().toString();
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("username", checkUser);
					data.put("password", checkPass);
					toServer.put("type", "login");
					toServer.put("data", data);
				} catch (Exception e) {
					
				}
				String toSend = toServer.toString();
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), MainActivity.class.newInstance(), toSend);
					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
			}
		});
		
		register.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				Log.v(TAG, "Fuck my lafiw");
				Intent regis = new Intent("sv.teamAwesome.friendtracker.REGISTER");
				startActivity(regis);
			}
		});
	}

	public void Callback(String res, Boolean error) {

		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Access Granted");

		} else {
			Log.v(TAG, "Access Denied");
		}
	}


}
