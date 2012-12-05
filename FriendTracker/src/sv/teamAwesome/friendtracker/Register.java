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
import android.widget.TextView;

public class Register extends Activity {
	private static final String TAG = "REGISTER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.register);
		
		final EditText regusr = (EditText) findViewById(R.id.reguser);
		final EditText regemail = (EditText) findViewById(R.id.regemail);
		final EditText regpass1 = (EditText) findViewById(R.id.regpass1);
		final EditText regpass2 = (EditText) findViewById(R.id.regpass2);
		final TextView error = (TextView) findViewById(R.id.regerror);
		Button regbtn = (Button) findViewById(R.id.regbtn);
		
		regbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String usr = regusr.getText().toString();
				String mail = regemail.getText().toString();
				String pass1 = regpass1.getText().toString();
				String pass2 = regpass2.getText().toString();
				
				if(pass1.contentEquals(pass2)) {
					JSONObject toServer = new JSONObject();
					JSONObject data = new JSONObject();
					try {
						data.put("username", usr);
						data.put("mail", mail);
						data.put("password", pass1);
						toServer.put("type", "register");
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
				} else {
					error.setText("Passwords does not match");
				}
			}
		});
	}
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Reg Ok");
		} else {
			Log.v(TAG, "Reg Ej Ok");
		}
	}
}
