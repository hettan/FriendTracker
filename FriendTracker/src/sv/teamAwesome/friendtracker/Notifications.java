package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.app.NotificationManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

public class Notifications extends Activity {
	private static final String TAG = "Noti";
		// TODO Auto-generated method stub
		protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.notifications);

		// Tar bort alla notifieringar. Kanske borde ta bort sin kategori i r�tt tabb.
		
		NotificationManager nm = (NotificationManager) 
	            getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
		}
		public void Callback(String res, Boolean error) {
			Log.v(TAG, "Callback: " + res);
			if(!error) {
				Log.v(TAG, "Goodie");

			} else {
				Log.v(TAG, "Error");
			}
		}
}
