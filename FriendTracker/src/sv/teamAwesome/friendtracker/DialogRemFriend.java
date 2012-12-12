package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DialogRemFriend extends FragmentActivity implements ShowPopUp.NoticeDialogListener{
	private static final String TAG = "DiaAct";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); {
			showNoticeDialog();
		}
	}
	public void showNoticeDialog() {
	// Create an instance of the dialog fragment and show it
		DialogFragment pop = new ShowPopUp();
		pop.show(getSupportFragmentManager(), "PopShow");
	}
	// The dialog fragment receives a reference to this Activity through the
	public void onDialogPositiveClick(DialogFragment pop) {
		// User touched the dialog's positive button
		Bundle extras = getIntent().getExtras();
		String item = extras.getString("item");
		
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("username", Config.USERNAME);
			data.put("target", item);
			toServer.put("type", "remFriend");
			toServer.put("data", data);
		} catch (Exception e) {
			
		}
		String toSend = toServer.toString();
		Log.v(TAG, "me= " + Config.temp.toString());
		try {
            Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(FriendsTab.class.getMethod("CallbackRem", params),Config.temp, toSend);

			AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
		} catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
    	finish();
	}

	public void onDialogNegativeClick(DialogFragment pop) {
		// User touched the dialog's negative button
    	finish();
	}

}