package sv.teamAwesome.friendtracker;

import org.json.JSONObject;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DialogAcceptGroup extends FragmentActivity implements ShowPopUp.NoticeDialogListener{
	private static final String TAG = "DiaAskFriend";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); {
		Config.PopMsg = "Do you want accept the group?";
		DialogFragment pop = new ShowPopUp();
		pop.show(getSupportFragmentManager(), "PopShow");
		}
	}

	public void onDialogPositiveClick(DialogFragment pop) {
		Bundle extras = getIntent().getExtras();
		String item = extras.getString("item");
				
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {		
			data.put("src", Config.USERNAME);
			data.put("requester", item);
			data.put("type","group");
			toServer.put("type", "acceptReq");
			toServer.put("data", data);
		}
		catch (Exception e) {
		}
		String toSend = toServer.toString();
		try {
            @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(NotificationsTab.class.getMethod("CallbackGroup", params), Config.temp, toSend);
			new ConnectionHandler().execute(connData);
		} catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
		finish();
	}    	

	public void onDialogNegativeClick(DialogFragment pop) {
    	finish();
	}

}