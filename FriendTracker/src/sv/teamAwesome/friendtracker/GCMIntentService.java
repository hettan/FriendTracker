package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
		
		/*
		 * TODO:
		 * M�ste registrera mot servern h�r. Annars s� kommer push att bli derpat om google registrerar om.
		 */
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		
		/*
		 * Borde kanske kunna avregistrera push i settings??
		 */
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
		
		int nCategory = Integer.parseInt(intent.getStringExtra("type"));
		String nUser = intent.getStringExtra("user");
		String nGroup = intent.getStringExtra("group");
		String nMessage = intent.getStringExtra("message");
		Log.v("NotificationManager", "Got note of type: "+nCategory);
		
		// Construct Intent to be started when note is clicked
		Intent targetIntent = new Intent("sv.teamAwesome.friendtracker.NOTIFICATIONS");
        PendingIntent nIntent = 
            PendingIntent.getActivity(context, 0, targetIntent, 0);
 
        // Construct Notification Manager to handle Notes
        NotificationManager noteManager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
        
        CharSequence title = "FriendTracker";
        CharSequence message;   
        
        switch(nCategory) {
        case 1:
        	title = "You have a friendrequest!";
        	message = nUser+ " wants to add you as a friend!";
        	Log.v("NotificationManager", "Note: Friendrequest");
        	break;
        case 2:
        	title = "You have been invited to a group!";
        	message = nUser+ " has invited you to join group \""+nGroup+"\"";
        	Log.v("NotificationManager", "Note: Groupinvite");
        	break;
        case 3:
        	title = "You have been buzzed!";
        	message = nUser+": "+nMessage;
        	Log.v("NotificationManager", "Note: Buzz");
        	break;
        default:	
        	title = "FriendTracker is derped :(";
        	message = "No category set. User: "+nUser;
        	Log.v("NotificationManager", "Note: ERROR!");
        	break;
        }
        
        Notification note = new Notification(
            R.drawable.ic_launcher, 
            title,
            System.currentTimeMillis());
      
        note.setLatestEventInfo(context, title, message, nIntent);
        noteManager.notify(nUser, nCategory, note);
        
        //plays the standard message sound, should work when you get a notification if placed here. otherwise move it.
		 try {
		        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
		    } catch (Exception e) {}
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

