package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//import sv.teamAwesome.friendtracker.Config.SENDER_ID;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	public GCMIntentService() {
		super(Config.SENDER_ID);
	}

	private static final String TAG = "GCMIntentService";


	@Override
	protected void onRegistered(Context context, String regId) {
		
		//DERP!
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		
		//DERP!
	}	

	@Override
	protected void onMessage(Context context, Intent intent) {
		/*
		 * Get data from Sender.
		 * 
		 * 	Category to update:
		 * 			ID	Category
		 * ------------------------------
		 * 			1 	FriendRequest
		 * 			2 	GroupInvite
		 * 			3 	Buzz
		 */
		int nCategory = 0;
		
		// Construct Intent to be started when note is clicked
		Intent targetIntent = new Intent("sv.teamAwesome.friendtracker.FRONTPAGE");
		targetIntent.putExtra(String.valueOf(nCategory), 1);
        PendingIntent nIntent = 
            PendingIntent.getActivity(context, 0, targetIntent, 0);
 
        // Construct Notification Manager to handle Notes
        NotificationManager noteManager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
        
        CharSequence title = "FriendTracker";
        CharSequence message;   
        
        switch(nCategory) {
        case 1:
        	message = "You have a friendrequest!";
        	Log.v(TAG, "Note: Friendrequest");
        case 2:
        	message = "You have been invited to a group!";
        	Log.v(TAG, "Note: Groupinvite");
        case 3:
        	message = "You have been buzzed!";
        	Log.v(TAG, "Note: Buzz");
        default:	
        	message = "FriendTracker is derped :(";
        	Log.v(TAG, "Note: ERROR!");
        }
        
        Notification note = new Notification(
            R.drawable.ic_launcher, 
            title,
            System.currentTimeMillis());
      
        note.setLatestEventInfo(context, title, message, nIntent);
        noteManager.notify(nCategory, note);
	}

	@Override
	protected void onError(Context context, String errorId) {
		//Derp
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
}


/*	== Ska finnas under onCreate() ==
 * GCMRegistrar.checkDevice(this);
 * GCMRegistrar.checkManifest(this);
 * final String regId = GCMRegistrar.getRegistrationId(this);
 * if (regId.equals("")) {
 *    GCMRegistrar.register(this, "377927318664");
 * }
 **/

