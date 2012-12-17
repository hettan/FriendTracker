package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service{
	
	
	final Object me = this;
	public static String TAG = "BackgroundService";
	
	static LocationManager manager;
	static LocationListener listener;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		
	}
	
	public void onDestroy() {
		manager.removeUpdates(listener);
		Log.v(TAG, "Stopping BackgroundService..");
	}
	
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "Starting BackgroundService..");
		manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		listener = new LocationListener() {
			
			public void onLocationChanged(Location location) {

				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("lat", (int)(location.getLatitude()*1E6));
					data.put("lon", (int)(location.getLongitude()*1E6));
					data.put("username", Config.USERNAME);
					data.put("referer", "BackgroundService");
					toServer.put("type", "setPos");
					toServer.put("data", data);
				} catch (Exception e) {
					
				}
				String toSend = toServer.toString();
				Log.v(TAG, "Sending data to server..");
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(BackgroundService.class.getMethod("Callback", params), me, toSend);

					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
				Log.v(TAG, "Done sending..");
			}

			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub	
			} 

			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub	
			}

			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub
			}
		};
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.USER_BACKGROUND_POSITION_UPDATE_INTERVAL, 0, listener);
	}
	
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Status Updated");

		} else {
			Log.v(TAG, "Status Not Updated");
		}
	}
}