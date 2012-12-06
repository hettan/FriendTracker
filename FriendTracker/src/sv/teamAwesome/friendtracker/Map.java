package sv.teamAwesome.friendtracker;

import java.util.List;

import org.json.JSONArray;
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
import android.view.inputmethod.InputMethodManager;
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
	private static final String TAG = "MAP";
	String tmp;

	MyLocationOverlay myLocation;
	PointerOverlay pointerOverlay;
	List<Overlay> mapOverlays;
	MapView mapView;
	GeoPoint point;
	
	View importPanel;
	//HashMap<String, GeoPoint> friendPos;
	List<String> fUser = null;
	List<GeoPoint> fPos = null;
	
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
		
		final Drawable drawable = getResources().getDrawable(R.drawable.marker);
		
		Button statusbtn = (Button) findViewById(R.id.statusBtn);
		final EditText statustxt = (EditText) findViewById(R.id.statusTxt);
		
		statusbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				String status = statustxt.getText().toString();
				
				JSONObject toServer = new JSONObject();
				JSONObject data = new JSONObject();
				try {
					data.put("username", Config.USERNAME);
					data.put("status", status);
					toServer.put("type", "setStatus");
					toServer.put("data", data);
				} catch (Exception e) {
					
				}
				String toSend = toServer.toString();
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(Map.class.getMethod("Callback", params), me, toSend);

					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
				statustxt.setText("");
				InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(statustxt.getWindowToken(), 0);
				importPanel.setVisibility(View.INVISIBLE);
			}
		});
		
		
		final MapController control = mapView.getController();
		
		LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener listener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				point = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
			
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
				
				JSONObject toServer2 = new JSONObject();
				JSONObject data2 = new JSONObject();
				try {
					data2.put("username", Config.USERNAME);
					toServer2.put("type", "getFriendsPos");
					toServer2.put("data", data2);
				} catch (Exception e) {
					
				}
				String toSend2 = toServer2.toString();
				try {
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(Map.class.getMethod("CallbackFriends", params), me, toSend2);
					//ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), MainActivity.class.newInstance(), toSend);

					AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
				}
				catch(Exception e) {
					Log.v(TAG, "Error: " + e.toString());
				}
				
				//Log.v(TAG, "STORLEK: " + fPos.size());
				if(fPos != null) {
					for(int i = 0; i < fUser.size(); i++) {
						Log.v(TAG, "Round "+i+", Starting..");
						
						PointerOverlay pointerOverlay = new PointerOverlay(drawable, mapView);
						OverlayItem overlayitem = new OverlayItem(fPos.get(i), fUser.get(i), "Derp");
						pointerOverlay.addOverlay(overlayitem);
						mapOverlays.add(pointerOverlay);
						Log.v(TAG, "Round "+i+", Done.");
					}
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
		mapOverlays = mapView.getOverlays();
		
		//control.setCenter(point);
		//control.setZoom(20);
		
		/*
		int friendPosLat, friendPosLong; //testpoint at pastavagnen
		friendPosLat = 58395516;
		friendPosLong = 15578195;
		GeoPoint hardcode = new GeoPoint(friendPosLat, friendPosLong);
				
		HashMap<String, String> status = new HashMap<String, String>();
		status.put("Anders", "Sitter och skiter!");
		
		for(int i = 0; i < fUser.size(); i++) {
			Log.v(TAG, "Round "+i+", Starting..");
			
			PointerOverlay pointerOverlay = new PointerOverlay(drawable, mapView);
			OverlayItem overlayitem = new OverlayItem(fPos.get(i), fUser.get(i), "Derp");
			pointerOverlay.addOverlay(overlayitem);
			mapOverlays.add(pointerOverlay);
			Log.v(TAG, "Round "+i+", Done.");
		}*/

		//pointerOverlay.addOverlay(overlayitem);
		//mapOverlays.add(pointerOverlay);
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
	public void Callback(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Status Updated");

		} else {
			Log.v(TAG, "Status Not Updated");
		}
	}
	public void CallbackFriends(String res, Boolean error) {
		Log.v(TAG, "Callback: " + res);
		if(!error) {
			Log.v(TAG, "Friends Positions");
			try {
				JSONArray data = new JSONArray(res);
				JSONObject pos;
				for(int i=0; i < data.length();i++){
					String username = data.getJSONObject(i).getString("username");
					Log.v(TAG, "USERNAME = " + username);
					data.getJSONObject(i).getString("status");
					pos = data.getJSONObject(i).getJSONObject("pos");
					int lon = Integer.parseInt(pos.getString("lon"));
					Log.v(TAG, "LONGITUDE = " + lon);
					int lat = Integer.parseInt(pos.getString("lat"));
					Log.v(TAG, "LATITUDE = " + lat);
					GeoPoint friend = new GeoPoint(lat, lon);
					fPos.add(friend);
					fUser.add(username);
					//friendPos.put(username, friend);
					
					//if (friends.getJSONObject(i).getBoolean("active"))
				}
				//JSONArray friends = data.getJSONArray("friends");
				//JSONArray requests = data.getJSONArray("requests");
			} catch(Exception e) {
				
			}
				
		} else {
			Log.v(TAG, "Error Friends Position");
		}
	}
}


