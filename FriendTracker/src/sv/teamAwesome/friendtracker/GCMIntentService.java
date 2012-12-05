package sv.teamAwesome.friendtracker;

import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
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
		Intent noteIntent = new Intent("NotificationHandler");
		intent.putExtra("category", 1);
		LocalBroadcastManager.getInstance(this).sendBroadcast(noteIntent);
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

