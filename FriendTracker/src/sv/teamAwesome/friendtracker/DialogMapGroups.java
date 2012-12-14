package sv.teamAwesome.friendtracker;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class DialogMapGroups extends FragmentActivity implements ShowListPopUp.NoticeDialogListener{
	private static final String TAG = "DiaMapGroup";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); {
			showNoticeDialog();
		}
	}
	public void showNoticeDialog() {
	// Create an instance of the dialog fragment and show it
		DialogFragment pop = new ShowListPopUp();
		pop.show(getSupportFragmentManager(), "GroupPop");
	}
	public void onDialogPositiveClick(DialogFragment dialog) {
		Log.v(TAG,"Listposition: " + ShowListPopUp.GivePos());
		finish();
	}
	public void onDialogNegativeClick(DialogFragment dialog) {
		finish();
	}

}