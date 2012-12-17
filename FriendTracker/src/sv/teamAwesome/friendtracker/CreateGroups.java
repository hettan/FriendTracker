package sv.teamAwesome.friendtracker;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CreateGroups extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.create_groups);
		final Object me = this;
		
		Button createBtn = (Button) findViewById(R.id.createBtn);
		final EditText createTxt = (EditText) findViewById(R.id.createTxt);
		
		createBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String text = createTxt.getText().toString();
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("username", Config.USERNAME);
					data.put("name", text);
					toServer.put("type", "createGroup");
					toServer.put("data", data);
				} catch (Exception e) {

				}

				String toSend = toServer.toString();

				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(CreateGroups.class.getMethod("Callback", params), me, toSend);
					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {

				}
			}
		});
		
	}
	public void Callback(String res, Boolean error) {
		if(!error) {
			Intent ok = new Intent("sv.teamAwesome.friendtracker.GROUPS");
			startActivity(ok);
			finish();
		} else {

		}
	}

}
