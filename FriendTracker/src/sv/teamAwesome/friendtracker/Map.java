package sv.teamAwesome.friendtracker;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
	private static final String TAG = "MAIN";
	String tmp;

	MyLocationOverlay myLocation;
	PointerOverlay pointerOverlay;
	List<Overlay> mapOverlays;
	MapView mapView;
	View importPanel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		View inflatedDrawerLayout = getLayoutInflater().inflate(R.layout.drawer, null);
		int width = getWindow().getAttributes().width, height = getWindow().getAttributes().height;
		LayoutParams params = new LayoutParams(width, height);
		getWindow().addContentView(inflatedDrawerLayout, params);
		
		importPanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();
		importPanel.setVisibility(View.INVISIBLE);
		
		final Object me = this;
		
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		Button statusbtn = (Button) findViewById(R.id.statusBtn);
		final EditText statustxt = (EditText) findViewById(R.id.statusTxt);
		
		statusbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				statustxt.setText("");
				importPanel.setVisibility(View.INVISIBLE);
			}
		});
		
		final MapController mc = mapView.getController();
		
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
		
		///////
		
		mapOverlays = mapView.getOverlays();
		
		Drawable drawable = getResources().getDrawable(R.drawable.marker);
		PointerOverlay pointerOverlay = new PointerOverlay(drawable, mapView);
		
		int friendPosLat, friendPosLong; //testpoint at pastavagnen
		friendPosLat = 58395516;
		friendPosLong = 15578195;
		GeoPoint point = new GeoPoint(friendPosLat, friendPosLong);
		
		HashMap<String, String> status = new HashMap<String, String>();
		status.put("Anders", "Sitter och skiter!");
		
		OverlayItem overlayitem = new OverlayItem(point, "Anders", (String) status.get("Anders"));
		
		pointerOverlay.addOverlay(overlayitem);
		mapOverlays.add(pointerOverlay);
		
		mc.animateTo(point);
		mc.setZoom(16);
		
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 1, "Status");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			if(importPanel.getVisibility() != View.VISIBLE)
				importPanel.setVisibility(View.VISIBLE);
			else
				importPanel.setVisibility(View.INVISIBLE);
		}
		return true;
	}
}


