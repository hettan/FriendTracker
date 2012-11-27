package sv.teamAwesome.friendtracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class Map extends MapActivity {

	MyLocationOverlay me;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		final MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		
		final MapController control = mapView.getController();
		
		LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener listener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				GeoPoint point = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
				control.setCenter(point);
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
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
		
		me = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(me);
	}	

	@Override
	protected void onPause() {
		super.onPause();
		me.disableMyLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		me.enableMyLocation();
	}
		
		@Override
		protected boolean isRouteDisplayed() {
			// TODO Auto-generated method stub
			return false;
		}
}


