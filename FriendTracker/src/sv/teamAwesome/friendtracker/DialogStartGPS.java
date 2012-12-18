package sv.teamAwesome.friendtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class DialogStartGPS extends FragmentActivity implements ShowPopUp.NoticeDialogListener{
	//private static final String TAG = "DiaAskFriend";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); {
		Config.PopMsg = "GPS is not enabled, do you want to start the GPS?";
		DialogFragment pop = new ShowPopUp();
		pop.show(getSupportFragmentManager(), "PopShow");
		}
	}

	public void onDialogPositiveClick(DialogFragment pop) {
		startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
		finish();
	}    	

	public void onDialogNegativeClick(DialogFragment pop) {
    	finish();
	}

}