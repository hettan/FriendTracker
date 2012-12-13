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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
	Boolean FirstLoc = true;
	Boolean setPoint = false;
	String myGroupID = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		View inflatedDrawerLayout = getLayoutInflater().inflate(R.layout.drawer, null);
		int width = getWindow().getAttributes().width, height = getWindow().getAttributes().height;
		LayoutParams params = new LayoutParams(width, height);
		getWindow().addContentView(inflatedDrawerLayout, params);
		
		importPanel = ((ViewStub) findViewById(R.id.stub_import)).inflate();
		importPanel.setVisibility(View.INVISIBLE);
		
		final Object me = this;

		Log.v(TAG, "asdsadsad");
		
		mapView = (TapControlledMapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		control = mapView.getController();

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

		LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener listener = new LocationListener() {
			
			public void onLocationChanged(Location location) {
				point = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
				if(FirstLoc) {
					control.animateTo(point);
					control.setZoom(100);
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
		            Class[] params = {String.class, Boolean.class};
					
					ConnectionData connData = new ConnectionData(MainActivity.class.getMethod("Callback", params), me, toSend);

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

		mapView.setOnSingleTapListener(new OnSingleTapListener() {
			public boolean onSingleTap(MotionEvent event) {
				GeoPoint RP = mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
				PointerOverlay pointerOverlay = new PointerOverlay(drawable, mapView);
				Log.v(TAG, "setPoint: " + setPoint);
				if(setPoint) {
					// POPUP
					
					// SKICKA RP TILL SERVER
					setPoint = false;
				}
				return true;
			}
		});
		

		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Config.USER_POSITION_UPDATE_INTERVAL, 0, listener);
		
		myLocation = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocation);
		mapOverlays = mapView.getOverlays();
		
		/*if(showhide) {
			JSONObject toServer = new JSONObject();
			JSONObject data = new JSONObject();
			try {
				data.put("showFriends", "true");
				data.put("groupID", myGroupID);
				data.put("username", Config.USERNAME);
				toServer.put("type", "getPositions");
				toServer.put("data", data);
			} catch (Exception e) {
				
			}
			String toSend = toServer.toString();
			try {
	            Class[] params2 = {String.class, Boolean.class};
				
				ConnectionData connData = new ConnectionData(Map.class.getMethod("CallbackShow", params2), me, toSend);
	
				AsyncTask<ConnectionData, Integer, String> conn = new ConnectionHandler().execute(connData);
			}
			catch(Exception e) {
				Log.v(TAG, "Error: " + e.toString());
			}
		}*/
		
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
		menu.add(0, 1, 1, "Status");
		menu.add(0, 2, 2, "Add Point");
		menu.add(0, 3, 3, "Hide Friends");
		menu.add(0, 4, 4, "Show Group");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			if(importPanel.getVisibility() != View.VISIBLE)
				importPanel.setVisibility(View.VISIBLE);
			else
				importPanel.setVisibility(View.INVISIBLE);
		}
		if (item.getItemId() == 2) {
			setPoint = true;
		}
		if(item.getItemId() == 3) {
			if(item.getTitle().toString().contentEquals("Hide Friends")) {
				item.setTitle("Show Friends");
				//showFriends = false;
			} else {
				item.setTitle("Hide Friends");
				//showFriends = true;
			}
		}
		if(item.getItemId() == 4) {
			Log.v(TAG, "Inside Item 4");
			Intent goDia = new Intent(getBaseContext(), DialogMapGroups.class);
			startActivity(goDia);
			Log.v(TAG, "After Item 4");
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
		List<GeoPoint> fPos = new ArrayList<GeoPoint>();
		List<String> fUser = new ArrayList<String>();
		mapOverlays.clear();
		mapOverlays.add(myLocation);
		if(!error) {
			Log.v(TAG, "Friends Positions");
			try {
				JSONArray data = new JSONArray(res);
				JSONObject pos;
				List<String> friendStatus = new ArrayList<String>();
				for(int i=0; i < data.length();i++){
					String username = data.getJSONObject(i).getString("username");
					Log.v(TAG, "USERNAME = " + username);
					friendStatus.add(data.getJSONObject(i).getString("status"));
					pos = data.getJSONObject(i).getJSONObject("pos");
					int lon = Integer.parseInt(pos.getString("lon"));
					Log.v(TAG, "LONGITUDE = " + lon);
					int lat = Integer.parseInt(pos.getString("lat"));
					Log.v(TAG, "LATITUDE = " + lat);
					GeoPoint friend = new GeoPoint(lat, lon);
					fPos.add(friend);
					fUser.add(username);
					
				}
				final Drawable drawable = getResources().getDrawable(R.drawable.marker_friends);
				Log.v(TAG, "Before: " + fPos.size());
				for(int i = 0; i < fUser.size(); i++) {
					Log.v(TAG, "Round "+i+", Starting..");
						
					PointerOverlay pointerOverlay = new PointerOverlay(drawable, mapView);
					OverlayItem overlayitem = new OverlayItem(fPos.get(i), fUser.get(i), friendStatus.get(i));
					pointerOverlay.addOverlay(overlayitem);
					mapOverlays.add(pointerOverlay);
					Log.v(TAG, "Round "+i+", Done.");
				}

			} catch(Exception e) {
				Log.v(TAG, "eeee: " + e.toString());
				Log.v(TAG, "eeee: " + e.getCause());
			}
				
		} else {
			Log.v(TAG, "Error Friends Position");
		}
	}
}
