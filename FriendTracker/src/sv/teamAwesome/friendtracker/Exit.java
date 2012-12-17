package sv.teamAwesome.friendtracker;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class Exit extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent backgroundService = new Intent(getApplicationContext(), BackgroundService.class);
	    stopService(backgroundService);
		
		Log.v("EXIT", "USERNAME: "+Config.USERNAME);
		Log.v("EXIT", "USERNAME: "+Config.SESSION_ID);
		
		Config.USERNAME = "";
		Config.SESSION_ID = "";
		GCMRegistrar.unregister(this);
		
		Log.v("EXIT", "USERNAME: "+Config.USERNAME);
		Log.v("EXIT", "USERNAME: "+Config.SESSION_ID);
		
		Intent killer = new Intent("logout");
		killer.setType("kill");
		sendBroadcast(killer);
		
		Intent intent = new Intent(this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
	              Intent.FLAG_ACTIVITY_NEW_TASK);
		super.finish();
		startActivity(intent);
		
	}
	
}
