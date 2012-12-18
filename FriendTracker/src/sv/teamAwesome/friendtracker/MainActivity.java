package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;

/*
 * TODO:
 * 	onResume() m�ste registrera push.
 * 	Om man inte registrerat google-konto, loggar in i appen och sen registrerar google-konto
 * 	kommer inte push registreras f�rens appen startar om.
 */

public class MainActivity extends Activity {
	private static final String TAG = "MAIN";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		final Object me = this;
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.activity_main);

		final EditText username = (EditText) findViewById(R.id.usernametv);
		final EditText password = (EditText) findViewById(R.id.passwordtv);
		final ProgressBar mProgress = (ProgressBar) findViewById(R.id.loginProg);
		Button login = (Button) findViewById(R.id.loginbtn);
		Button register = (Button) findViewById(R.id.regbtn);
		
		mProgress.setVisibility(ProgressBar.INVISIBLE);
		
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TextView denied = (TextView) findViewById(R.id.logintv);
				denied.setText("");	
				
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
					mProgress.setVisibility(ProgressBar.VISIBLE);
		            @SuppressWarnings("rawtypes")
					Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), me, toSend);
					new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
			}

		});
		
		register.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				
				Intent regis = new Intent("sv.teamAwesome.friendtracker.REGISTER");
				startActivity(regis);
			}
		});
	}
	public void Callback(String res, Boolean error) {
		Intent granted = new Intent("sv.teamAwesome.friendtracker.FRONTPAGE");
		TextView denied = (TextView) findViewById(R.id.logintv);
		final ProgressBar mProgress = (ProgressBar) findViewById(R.id.loginProg);
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			final EditText username = (EditText) findViewById(R.id.usernametv);
			Config.USERNAME = username.getText().toString();
			Config.SESSION_ID = res;
			denied.setText("");
			mProgress.setVisibility(ProgressBar.INVISIBLE);
			startActivity(granted);
			Log.v(TAG, "Access Granted");

		} else {
			mProgress.setVisibility(ProgressBar.INVISIBLE);
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(denied.getWindowToken(), 0);
			Log.v(TAG, "Access Denied");
			denied.setText(res);
		}
	}
}
