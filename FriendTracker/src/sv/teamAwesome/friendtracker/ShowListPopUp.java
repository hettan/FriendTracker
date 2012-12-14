package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ShowListPopUp extends DialogFragment {
private static final String TAG = "ShowListPopUp";
private static int pos = -1;
public static ArrayAdapter<String> GroupAdapter;

public interface NoticeDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
}

NoticeDialogListener mListener;

@Override
public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
        mListener = (NoticeDialogListener) activity;
    } catch (ClassCastException e) {
        throw new ClassCastException(activity.toString()
                + " must implement NoticeDialogListener");
    }
}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
		Log.v(TAG, "Starting OnCreateDialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		if(GroupAdapter == null || GroupAdapter.isEmpty()) {
			Log.v(TAG, "derpsdasd");
			builder.setTitle("You dont have any groups to show");
			builder.setNegativeButton("No Groups", new DialogInterface.OnClickListener() { 
	               public void onClick(DialogInterface dialog, int which) {
	            	   pos = -1;
	            	   mListener.onDialogNegativeClick(ShowListPopUp.this);
	               }
				});
		}
		else {
		builder.setTitle("Choose A Group To Show");
		builder.setCancelable(false);
    	builder.setSingleChoiceItems(GroupAdapter,pos,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					pos = which;
				}
			});
		builder.setNegativeButton("No Groups", new DialogInterface.OnClickListener() { 
               public void onClick(DialogInterface dialog, int which) {
            	   pos = -1;
            	   mListener.onDialogNegativeClick(ShowListPopUp.this);
               }
			});
    	builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
               public void onClick(DialogInterface dialog, int which) {
            	   Log.v(TAG,"ID for click: " + pos);
            	   mListener.onDialogPositiveClick(ShowListPopUp.this);
           }
    	});
		}
    return builder.create();
}

	public static int GivePos() {
		return pos;
	}
	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setOnDismissListener(null);
		super.onDestroyView();
	}
}