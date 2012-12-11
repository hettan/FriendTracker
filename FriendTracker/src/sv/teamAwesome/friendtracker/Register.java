package sv.teamAwesome.friendtracker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
		
		final Object me = this;
		
		final EditText regusr = (EditText) findViewById(R.id.reguser);
		final EditText regemail = (EditText) findViewById(R.id.regemail);
		final EditText regpass1 = (EditText) findViewById(R.id.regpass1);
		final EditText regpass2 = (EditText) findViewById(R.id.regpass2);
		final TextView errortxt = (TextView) findViewById(R.id.regerror);
		Button regbtn = (Button) findViewById(R.id.regbtn);
		
		regbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String usr = regusr.getText().toString();
				String mail = regemail.getText().toString();
				String pass1 = regpass1.getText().toString();
				String pass2 = regpass2.getText().toString();
				boolean filled = false;
				
				if(usr.length() != 0 && mail.length() != 0 && pass1.length() != 0 && pass2.length() != 0) {
					filled = true;
				}
				
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(errortxt.getWindowToken(), 0);
				
				Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
				Matcher m = p.matcher(mail);
				boolean matchFound = m.matches();
				
				if(filled) {
					if(pass1.contentEquals(pass2)) {
						if(matchFound) {
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
						
								ConnectionData connData = new ConnectionData(Register.class.getMethod("Callback", params), me, toSend);
								AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
							}
							catch(Exception e) {
								Log.v(TAG, "Error: " + e.toString());
							}
						} else {
							errortxt.setText("Email is invalid");
						}
					} else {
						errortxt.setText("Passwords does not match");
					}
				} else {
					errortxt.setText("Please fill in all fields");
				}
			}
		});
	}
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Reg Ok");
			finish();
		} else {
			Log.v(TAG, "Reg Ej Ok");
			final TextView errortxt = (TextView) findViewById(R.id.regerror);
			errortxt.setText(res);
		}
	}
}
