package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NotificationHandler extends Activity {
	
	private static final String TAG = "NotificationHandler";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
	      new IntentFilter("NotificationHandler"));
	}

	private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
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
			int nCategory = intent.getIntExtra("category", 0);
			
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
	};
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
		super.onDestroy();
	}
}
