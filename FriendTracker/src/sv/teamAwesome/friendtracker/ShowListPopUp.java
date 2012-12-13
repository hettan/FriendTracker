package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ShowListPopUp extends DialogFragment {
private static final String TAG = "ShowListPopUp";
private static int pos = -1;
/* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
public interface NoticeDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
}

// Use this instance of the interface to deliver action events
NoticeDialogListener mListener;

// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
@Override
public void onAttach(Activity activity) {
    super.onAttach(activity);
    // Verify that the host activity implements the callback interface
    try {
        // Instantiate the NoticeDialogListener so we can send events to the host
        mListener = (NoticeDialogListener) activity;
    } catch (ClassCastException e) {
        // The activity doesn't implement the interface, throw exception
        throw new ClassCastException(activity.toString()
                + " must implement NoticeDialogListener");
    }
}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Choose A Group To Show")
    		.setSingleChoiceItems(R.array.MapGroupValues,pos,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					pos = which;
				}
			})
			.setNegativeButton("No Groups", new DialogInterface.OnClickListener() { 
               public void onClick(DialogInterface dialog, int which) {
            	   pos = -1;
            	   mListener.onDialogNegativeClick(ShowListPopUp.this);
               }
			})
    		.setPositiveButton("Ok", new DialogInterface.OnClickListener() { 
               public void onClick(DialogInterface dialog, int which) {
            	   Log.v(TAG,"ID for click: " + pos);
            	   mListener.onDialogPositiveClick(ShowListPopUp.this);
           }
    });
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