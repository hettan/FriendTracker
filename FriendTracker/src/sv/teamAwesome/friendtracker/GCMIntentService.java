package sv.teamAwesome.friendtracker;

import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
		//Derp
	}

	@Override
	protected void onError(Context context, String errorId) {
		//Derp
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		return super.onRecoverableError(context, errorId);
	}
	
	// Callback for TCP Connection
	public void Callback(String res, Boolean error) {

		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Access Granted");
		} else {
			Log.v(TAG, "Access Denied");
		}
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

