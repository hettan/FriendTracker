package sv.teamAwesome.friendtracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		EditText username = (EditText) findViewById(R.id.usernametv);
		EditText password = (EditText) findViewById(R.id.passwordtv);
		Button login = (Button) findViewById(R.id.loginbtn);
		Button register = (Button) findViewById(R.id.regbtn);
		
		login.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//If something, something, then do something, something.
				
				
			}
		});
	}


}
