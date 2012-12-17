package sv.teamAwesome.friendtracker;

import org.json.JSONObject;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DialogDeclineFriend extends FragmentActivity implements ShowPopUp.NoticeDialogListener{
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
		
    	finish();
	}

	public void onDialogNegativeClick(DialogFragment pop) {
		
    	finish();
	}

}