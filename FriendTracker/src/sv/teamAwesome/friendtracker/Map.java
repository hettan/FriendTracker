package sv.teamAwesome.friendtracker;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class Map extends MapActivity {
	private static final String TAG = "MAIN";

	MyLocationOverlay myLocation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		final Object me = this;
		
		final MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		
		final MapController control = mapView.getController();
		
		LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener listener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				GeoPoint point = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
				control.setCenter(point);
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("lat", (int)(location.getLatitude()*1E6));
					data.put("lon", (int)(location.getLongitude()*1E6));
					data.put("username", Config.USERNAME);
					toServer.put("type", "setPos");
					toServer.put("data", data);
				} catch (Exception e) {
					
				}
				String toSend = toServer.toString();
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), me, toSend);
					//ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), MainActivity.class.newInstance(), toSend);

					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
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
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.USER_POSITION_UPDATE_INTERVAL, 0, listener);
		
		myLocation = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocation);
	}	

	@Override
	protected void onPause() {
		super.onPause();
		myLocation.disableMyLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		myLocation.enableMyLocation();
	}
		
		@Override
		protected boolean isRouteDisplayed() {
			// TODO Auto-generated method stub
			return false;
		}
}


