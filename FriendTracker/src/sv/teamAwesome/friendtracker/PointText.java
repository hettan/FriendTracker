package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class PointText extends Activity{
	private static final String TAG = "POINTTEXT";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Type in a header for your point");
		    final EditText input = new EditText(this); 
		    final Object me = this;
		    builder.setView(input);
		    builder.setCancelable(false);
		    builder.setPositiveButton("Set Point", new DialogInterface.OnClickListener() { 
		               public void onClick(DialogInterface dialog, int which) {
		            	   Config.RALLY_POINT_MESSAGE = input.getText().toString();
		            	   Log.v(TAG, "text from Config: " + Config.RALLY_POINT_MESSAGE);
		            	   Log.v(TAG, "text from input: " + input.getText().toString());
		            	   
		            	   Bundle extras = getIntent().getExtras();
		            	   int lon = extras.getInt("lon");
		            	   int lat = extras.getInt("lat");
		            	   Log.v(TAG, "Lat: " + lat);
		            	   Log.v(TAG, "Lon: " + lon);
		            	   JSONObject toServer = new JSONObject();
		            	   JSONObject data = new JSONObject();
							try {
								data.put("lat", lat);
								data.put("lon", lon);
								data.put("username", Config.USERNAME);
								data.put("groupID", Config.selectedGroupID);
								data.put("text", Config.RALLY_POINT_MESSAGE);
								toServer.put("type", "addRallyPoint");
								toServer.put("data", data);
							} catch (Exception e) {
								
							}
							String toSend = toServer.toString();
							try {
					            @SuppressWarnings("rawtypes")
								Class[] params = {String.class, Boolean.class};
								
								ConnectionData connData = new ConnectionData(Map.class.getMethod("Callback", params), me, toSend);

								new ConnectionHandler().execute(connData);
							}
							catch(Exception e) {
								Log.v(TAG, "Error: " + e.toString());
							}
		            	   
		            	   finish();
		           }
		    });
			builder.setNegativeButton("Dont add point",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
					finish();
				}
			});
		    AlertDialog ad = builder.create();
		    ad.show();
	}
}
