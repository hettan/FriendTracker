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
import android.os.Looper;
import android.util.Log;

//import sv.teamAwesome.friendtracker.Config.SENDER_ID;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	public GCMIntentService() {
		super(Config.SENDER_ID);
	}

	private static final String TAG = "GCMIntentService";

	@Override
	protected void onRegistered(final Context context, final String regId) {
		
		Log.v(TAG, "Registred. Sending PushID to server..");
		
		class LooperThread extends Thread {
			  public void run() {
			    Looper.prepare();
			    JSONObject toServer = new JSONObject();
			    JSONObject data = new JSONObject();
			    try {
			    	data.put("username", Config.USERNAME);
			    	data.put("pushID", regId);
			    	toServer.put("type", "registerPush");
			    	toServer.put("data", data);
			    } catch (Exception e) {}
			    
			    String toSend = toServer.toString();
			    try {
			    	@SuppressWarnings("rawtypes")
					Class[] params = {String.class, Boolean.class};
			    	ConnectionData connData = new ConnectionData(LooperThread.class.getMethod("Callback", params), this, toSend);
			    	new ConnectionHandler().execute(connData);
			    }
			    catch(Exception e) {
			    	Log.v(TAG, "Error: " + e.toString());
			    }
			    Looper.loop();
			  }
			  @SuppressWarnings("unused")
			  public void Callback(String res, Boolean error) {
				  	Log.v("GCMCallback", "Callback: "+res);
				 	try {
				 		Looper.myLooper().quit();
				 	} catch(Exception e) {
				 		Log.v("GCMCallback", "Error: "+e);
				 	}
				 	Log.v(TAG, "Done.");
			  }
		}
		LooperThread TCPThread = new LooperThread();
		TCPThread.start();
	}

	@Override
	protected void onUnregistered(final Context context, final String regId) {
		Log.v(TAG, "Unregistred. Removing PushID from server..");
		/*
		 * Borde kanske kunna avregistrera push i settings??
		 */
		class LooperThread extends Thread {
			  public void run() {
			    Looper.prepare();
			    JSONObject toServer = new JSONObject();
			    JSONObject data = new JSONObject();
			    try {
			    	data.put("username", Config.USERNAME);
			    	toServer.put("type", "removePush");
			    	toServer.put("data", data);
			    } catch (Exception e) {}
			    
			    String toSend = toServer.toString();
			    try {
			    	@SuppressWarnings("rawtypes")
					Class[] params = {String.class, Boolean.class};
			    	ConnectionData connData = new ConnectionData(LooperThread.class.getMethod("Callback", params), this, toSend);
			    	new ConnectionHandler().execute(connData);
			    }
			    catch(Exception e) {
			    	Log.v(TAG, "Error: " + e.toString());
			    }
			    Looper.loop();
			  }
			  @SuppressWarnings("unused")
			  public void Callback(String res, Boolean error) {
				  	Log.v("GCMCallback", "Callback: "+res);
				 	try {
				 		Looper.myLooper().quit();
				 	} catch(Exception e) {
				 		Log.v("GCMCallback", "Error: "+e);
				 	}
				 	Log.v(TAG, "Done.");
			  }
		}
		LooperThread TCPThread = new LooperThread();
		TCPThread.start();
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
		Intent targetIntent = new Intent("sv.teamAwesome.friendtracker.NOTIFICATIONSTAB");
 
        // Construct Notification Manager to handle Notes
        NotificationManager noteManager = (NotificationManager)
            getSystemService(NOTIFICATION_SERVICE);
        
        CharSequence title = "FriendTracker";
        CharSequence message;   
        String notificationType = "All";
        
        switch(nCategory) {
        case 1:
        	title = "You have a friendrequest!";
        	message = nUser+ " wants to add you as a friend!";
        	notificationType = "FriendRequests";
        	Log.v("NotificationManager", "Note: Friendrequest");
        	break;
        case 2:
        	title = "You have been invited to a group!";
        	message = nUser+ " has invited you to join group \""+nGroup+"\"";
        	notificationType = "GroupRequests";
        	Log.v("NotificationManager", "Note: Groupinvite");
        	break;
        case 3:
        	title = "You have been buzzed!";
        	message = nUser+": "+nMessage;
        	notificationType = "Buzz";
        	Log.v("NotificationManager", "Note: Buzz");
        	break;
        default:	
        	title = "FriendTracker is derped :(";
        	message = "No category set. User: "+nUser;
        	notificationType = "All";
        	Log.v("NotificationManager", "Note: ERROR!");
        	break;
        }
        targetIntent.putExtra("type", notificationType);
        targetIntent.putExtra("user", nUser);
        targetIntent.putExtra("cat", nCategory);
        
        Log.v(TAG, "sent: " + notificationType); 
        PendingIntent nIntent = 
                PendingIntent.getActivity(context, nUser.hashCode(), targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
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

