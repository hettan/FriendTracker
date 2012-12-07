package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;

public class Notifications extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notifications);
		// Tar bort alla notifieringar. Kanske borde ta bort sin kategori i rätt tabb.
		NotificationManager nm = (NotificationManager) 
	            getSystemService(NOTIFICATION_SERVICE);
		nm.cancelAll();
	}
}
