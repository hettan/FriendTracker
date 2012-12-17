package sv.teamAwesome.friendtracker;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class FrontPage extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Logged in, registering PUSH!!
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		if ((GCMRegistrar.getRegistrationId(this)).equals("")) {
			GCMRegistrar.register(this, Config.SENDER_ID);
		}
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.frontpage);
		
		Intent backgroundService = new Intent(getApplicationContext(), BackgroundService.class);
	    startService(backgroundService);
	}
	
	public void MenuBtn(View v) {
		String Target = v.getTag().toString();
		
		ClassLoader classLoader = getClassLoader();
		
    	Class<?> aClass = null;
        if(Target != null) {
            try {
            	aClass = classLoader.loadClass("sv.teamAwesome.friendtracker." + Target);
                //c = Class.forName(Target);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Intent menuSwap = new Intent(v.getContext(), aClass);
        menuSwap.putExtra("type", "All");
        startActivity(menuSwap);
	}
	/*@Override
	public void onResume() {
		super.onResume();
		Log.v("FRONTPAGE", "resumed, USERNAME: "+Config.USERNAME);
		if(Config.USERNAME == "")
			finish();
	}*/
}