package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class StatusText extends Activity{
	private static final String TAG = "StatusText";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Tell your friends what you are doing");
		    final EditText input = new EditText(this); 
		    builder.setView(input);
		    builder.setCancelable(false);
		    builder.setPositiveButton("Post Status!", new DialogInterface.OnClickListener() { 
		               public void onClick(DialogInterface dialog, int which) {
		            	String status = input.getText().toString();
		            	   
		            	JSONObject toServer = new JSONObject();
		   				JSONObject data = new JSONObject();
		   				try {
		   					data.put("username", Config.USERNAME);
		   					data.put("status", status);
		   					toServer.put("type", "setStatus");
		   					toServer.put("data", data);
		   				} catch (Exception e) {
		   					
		   				}
		   				String toSend = toServer.toString();
		   				try {
		   		            Class[] params = {String.class, Boolean.class};
		   					
		   					ConnectionData connData = new ConnectionData(Map.class.getMethod("Callback", params), Config.temp, toSend);

		   					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		   				}
		   				catch(Exception e) {
		   					Log.v(TAG, "Error: " + e.toString());
		   				}
		            	finish();
		           }
		    });
			builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
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
