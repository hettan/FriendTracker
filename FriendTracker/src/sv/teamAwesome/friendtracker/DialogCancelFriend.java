package sv.teamAwesome.friendtracker;

import org.json.JSONObject;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DialogCancelFriend extends FragmentActivity implements ShowPopUp.NoticeDialogListener{
	private static final String TAG = "DiaDeclineFriend";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); {
		Config.PopMsg = "Do you want to cancel the friendrequest?";
		DialogFragment pop = new ShowPopUp();
		pop.show(getSupportFragmentManager(), "PopShow");
		}
	}

	public void onDialogPositiveClick(DialogFragment pop) {
		Bundle extras = getIntent().getExtras();
		String item = extras.getString("item");
		int pos = extras.getInt("pos");
				
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {		
			data.put("username", Config.USERNAME);
			data.put("target", item);
			data.put("type", "friend");
			data.put("itemPos", pos);
			toServer.put("type", "remRequest");
			toServer.put("data", data);
		}
		catch (Exception e) {
		}
		String toSend = toServer.toString();
		try {
            @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(Search.class.getMethod("CallbackCancelReq", params), Config.temp, toSend);
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