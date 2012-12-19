package sv.teamAwesome.friendtracker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;

public class Map extends MapActivity {
	private static final String TAG = "MAP";
	String tmp;

	MyLocationOverlay myLocation;
	PointerOverlay pointerOverlay;
	List<Overlay> mapOverlays;
	TapControlledMapView mapView;
	GeoPoint point;
	MapController control;
	View importPanel;
	View pointerPanel;
	Boolean FirstLoc;
	Boolean setPoint = false;
	String myGroupID = "";
	Boolean showFriends = true;
	Handler h = new Handler();
	LocationListener listener;
	LocationManager manager;
	
	final Object me = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FirstLoc = true;
		setContentView(R.layout.map);
		
		mapView = (TapControlledMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		control = mapView.getController();

		final Drawable drawableRed = getResources().getDrawable(R.drawable.markerred);
		
		manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		listener = new LocationListener() {
			public void onLocationChanged(Location location) {
				point = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
				if(FirstLoc) {
					Log.v(TAG,"----------------LOCK ON POS -----------------");
					control.animateTo(point);
					control.setZoom(350);
					FirstLoc = false;
				}
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
		            @SuppressWarnings("rawtypes")
					Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), me, toSend);

					new ConnectionHandler().execute(connData);
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
		
		mapView.setOnSingleTapListener(new OnSingleTapListener() {
			public boolean onSingleTap(MotionEvent event) {
				GeoPoint RP = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
				new PointerOverlay(drawableRed, mapView);
				if(setPoint) {
					// POPUP
					Intent go = new Intent(getBaseContext(),PointText.class);
					go.putExtra("lat", (int)RP.getLatitudeE6());
					go.putExtra("lon", (int)RP.getLongitudeE6());
					startActivity(go);
					// SKICKA RP TILL SERVER
					setPoint = false;
				}
				return true;
			}
		});
		
		myLocation = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocation);
		mapOverlays = mapView.getOverlays();
		

		boolean isGPS = manager.isProviderEnabled (LocationManager.GPS_PROVIDER);
		if(!isGPS) {
			Intent GPS = new Intent(this,DialogStartGPS.class);
			startActivity(GPS);
		}
	}
	
	public Runnable myRunnable = new Runnable() {
		public void run() {
		Log.v(TAG, "timer= " + Config.USER_POSITION_UPDATE_INTERVAL);
		JSONObject toServer2 = new JSONObject();
		JSONObject data2 = new JSONObject();
		try {
			data2.put("username", Config.USERNAME);
			data2.put("showFriends", showFriends);
			data2.put("groupID", Config.selectedGroupID);
			toServer2.put("type", "getPositions");
			toServer2.put("data", data2);
		} catch (Exception e) {
			
		}
		String toSend2 = toServer2.toString();
		try {
            @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(Map.class.getMethod("CallbackFriends", params), me, toSend2);

			new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
		if(!FirstLoc) {
		Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		point = new GeoPoint((int)(loc.getLatitude()*1E6), (int)(loc.getLongitude()*1E6));
		JSONObject toServer = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			data.put("lat", (int)(loc.getLatitude()*1E6));
			data.put("lon", (int)(loc.getLongitude()*1E6));
			data.put("username", Config.USERNAME);
			toServer.put("type", "setPos");
			toServer.put("data", data);
		} catch (Exception e) {
			
		}
		String toSend = toServer.toString();
		try {
            @SuppressWarnings("rawtypes")
			Class[] params = {String.class, Boolean.class};
			
			ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), me, toSend);

			new ConnectionHandler().execute(connData);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: " + e.toString());
		}
		}
		h.postDelayed(myRunnable, Config.USER_POSITION_UPDATE_INTERVAL);
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		myLocation.enableMyLocation();
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.USER_POSITION_UPDATE_INTERVAL, 0, listener);
		Intent backgroundService = new Intent(getApplicationContext(), BackgroundService.class);
	    stopService(backgroundService);
	    h.postDelayed(myRunnable, 2000);
	    Log.v(TAG, "Starting manager and listener..");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		myLocation.disableMyLocation();
		manager.removeUpdates(listener);
		Intent backgroundService = new Intent(getApplicationContext(), BackgroundService.class);
	    startService(backgroundService);
	    h.removeCallbacks(myRunnable);
	    Log.v(TAG, "Stopping manager and listener..");
	}
		
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "Status");
		menu.add(0, 2, 2, "Add Point");
		menu.add(0, 3, 3, "Hide Friends");
		menu.add(0, 4, 4, "Show Group");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			Intent go = new Intent(getBaseContext(), StatusText.class);
			Object me = this;
			Config.temp = me;
			startActivity(go);
		}
		if (item.getItemId() == 2) {

			setPoint = true;
		}
		if(item.getItemId() == 3) {
			if(item.getTitle().toString().contentEquals("Hide Friends")) {
				item.setTitle("Show Friends");
				showFriends = false;
			} else {
				item.setTitle("Hide Friends");
				showFriends = true;
			}
		}
		if(item.getItemId() == 4) {
			Intent goDia = new Intent(getBaseContext(), DialogMapGroups.class);
			startActivity(goDia);
		}
		return true;
	}
	public void Callback(String res, Boolean error) {
		if(!error) {
		} else {
		}
	}
	public void CallbackFriends(String res, Boolean error) {
		List<GeoPoint> fPos = new ArrayList<GeoPoint>();
		List<String> fUser = new ArrayList<String>();
		List<GeoPoint> gPos = new ArrayList<GeoPoint>();
		List<String> gUser = new ArrayList<String>();
		List<GeoPoint> rPos = new ArrayList<GeoPoint>();
		List<String> rUser = new ArrayList<String>();
		mapOverlays.clear();
		mapOverlays.add(myLocation);
		if(!error) {
			try {
				JSONObject data =  new JSONObject(res);
				
				///Handle friend data
				JSONArray friends = data.getJSONArray("friends");
				JSONObject pos;
				String username;
				List<String> friendStatus = new ArrayList<String>();
				for(int i=0; i < friends.length();i++){
					username = friends.getJSONObject(i).getString("username");
					Log.v(TAG, "USERNAME = " + username);
					friendStatus.add(friends.getJSONObject(i).getString("status"));
					pos = friends.getJSONObject(i).getJSONObject("pos");
					int lon = Integer.parseInt(pos.getString("lon"));
					Log.v(TAG, "LONGITUDE = " + lon);
					int lat = Integer.parseInt(pos.getString("lat"));
					Log.v(TAG, "LATITUDE = " + lat);
					GeoPoint friend = new GeoPoint(lat, lon);
					fPos.add(friend);
					fUser.add(username);
				}
				
				/// Add friends data to map
				final Drawable drawableFriends = getResources().getDrawable(R.drawable.markerblue);
				Log.v(TAG, "Before: " + fPos.size());
				for(int i = 0; i < fUser.size(); i++) {
					Log.v(TAG, "Round "+i+", Starting..");
						
					PointerOverlay pointerOverlay = new PointerOverlay(drawableFriends, mapView);
					OverlayItem overlayitem = new OverlayItem(fPos.get(i), fUser.get(i), friendStatus.get(i));
					pointerOverlay.addOverlay(overlayitem);
					mapOverlays.add(pointerOverlay);
					Log.v(TAG, "Round "+i+", Done.");
				}
				
				/// Handle group data
				JSONArray group = data.getJSONArray("group");
				List<String> groupStatus = new ArrayList<String>();
				for(int i=0; i < group.length();i++){
					username = group.getJSONObject(i).getString("username");
					Log.v(TAG, "USERNAME = " + username);
					groupStatus.add(group.getJSONObject(i).getString("status"));
					pos = group.getJSONObject(i).getJSONObject("pos");
					int lon = Integer.parseInt(pos.getString("lon"));
					Log.v(TAG, "LONGITUDE = " + lon);
					int lat = Integer.parseInt(pos.getString("lat"));
					Log.v(TAG, "LATITUDE = " + lat);
					GeoPoint groupPoint = new GeoPoint(lat, lon);
					gPos.add(groupPoint);
					gUser.add(username);
				}
				
				/// Add group data to map
				final Drawable drawableGroup = getResources().getDrawable(R.drawable.markergreen);
				for(int i = 0; i < gUser.size(); i++) {
					Log.v(TAG, "Round "+i+", Starting..");
						
					PointerOverlay pointerOverlay = new PointerOverlay(drawableGroup, mapView);
					OverlayItem overlayitem = new OverlayItem(gPos.get(i), gUser.get(i), groupStatus.get(i));
					pointerOverlay.addOverlay(overlayitem);
					mapOverlays.add(pointerOverlay);
					Log.v(TAG, "Round "+i+", Done.");
				}
				
				/// Handle rally data
				JSONArray rallypoints = data.getJSONArray("rallypoints");
				List<String> rallyStatus = new ArrayList<String>();
				Log.v(TAG, "rally_len: " + rallypoints.length());
				for(int i=0; i < rallypoints.length();i++){
					username = rallypoints.getJSONObject(i).getString("created_by");
					Log.v(TAG, "USERNAME = " + username);
					rallyStatus.add(rallypoints.getJSONObject(i).getString("text"));
					pos = rallypoints.getJSONObject(i).getJSONObject("pos");
					int lon = Integer.parseInt(pos.getString("lon"));
					Log.v(TAG, "LONGITUDE = " + lon);
					int lat = Integer.parseInt(pos.getString("lat"));
					Log.v(TAG, "LATITUDE = " + lat);
					GeoPoint groupPoint = new GeoPoint(lat, lon);
					rPos.add(groupPoint);
					rUser.add(username);
				}
				
				/// Add rally data to map
				final Drawable drawableRally= getResources().getDrawable(R.drawable.markerred);
				for(int i = 0; i < rUser.size(); i++) {
					Log.v(TAG, "Round "+i+", Starting..");
						
					PointerOverlay pointerOverlay = new PointerOverlay(drawableRally, mapView);
					OverlayItem overlayitem = new OverlayItem(rPos.get(i), rUser.get(i), rallyStatus.get(i));
					pointerOverlay.addOverlay(overlayitem);
					mapOverlays.add(pointerOverlay);
					Log.v(TAG, "Round "+i+", Done.");
				}

			} catch(Exception e) {
				Log.v(TAG, "eeee: " + e.toString());
				Log.v(TAG, "eeee: " + e.getCause());
			}
			mapView.invalidate();
				
		} else {
			Log.v(TAG, "Error Friends Position");
		}
	}
}
