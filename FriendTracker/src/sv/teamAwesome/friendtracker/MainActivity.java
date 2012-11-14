package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
				
				
			}
		});
		register.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent regis = new Intent("sv.teamAwesome.friendtracker.REGISTER");
				startActivity(regis);
				
			}
		});
	}


}
