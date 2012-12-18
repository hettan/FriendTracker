package sv.teamAwesome.friendtracker;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DialogMapGroups extends FragmentActivity implements ShowListPopUp.NoticeDialogListener{
	private static final String TAG = "DiaMapGroup";
	private String[] groupIDs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); {
			Object me = this;
			JSONObject toServer = new JSONObject();
			JSONObject data = new JSONObject();
			try {
				data.put("username", Config.USERNAME);
				toServer.put("type", "getGroups");
				toServer.put("data", data);
			} catch (Exception e) {
				Log.v(TAG, "JSON: " + e.toString());
			}
			String toSend = toServer.toString();
			Log.v(TAG, "1");
			try {
				@SuppressWarnings("rawtypes")
				Class[] params = {String.class, Boolean.class};

				ConnectionData connData = new ConnectionData(DialogMapGroups.class.getMethod("Callback", params), me, toSend);
				new ConnectionHandler().execute(connData);
			}
			catch(Exception e) {
				Log.v(TAG, "Error: " + e.toString());
			}
		}
	}
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.v(TAG,"Listposition: " + ShowListPopUp.GivePos());
		Config.selectedGroupID = groupIDs[ShowListPopUp.GivePos()];
		finish();
	}
	public void onDialogNegativeClick(DialogFragment dialog) {
		finish();
	}

	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Got Groups");
			try {
				ArrayAdapter<String> groupadapter;
				JSONArray data = new JSONArray(res);
				if(data.length() > 0) {
				JSONObject group;
				String[] list = new String[data.length()];
				groupIDs = new String[data.length()];

				for(int i=0; i < (data.length());i++){
					group = data.getJSONObject(i);
					Log.v(TAG, "13"+group);
					list[i] = group.getString("name");
					groupIDs[i] = group.getString("groupID");
				}

				groupadapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.select_dialog_singlechoice, list);
				ShowListPopUp.GroupAdapter = groupadapter;
				}
				DialogFragment pop = new ShowListPopUp();
				pop.show(getSupportFragmentManager(), "GroupPop");
				Log.v(TAG, "should show GroupPop");

			} catch(Exception e){
				Log.v(TAG, "Error: "+ e.toString());
				Log.v(TAG, "Cause: "+ e.getCause());
			}

		} else {
			Log.v(TAG, "Le Error");
		}
	}
			
}