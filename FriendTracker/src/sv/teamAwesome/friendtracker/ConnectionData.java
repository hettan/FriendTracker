package sv.teamAwesome.friendtracker;

import java.lang.reflect.Method;

import org.json.JSONObject;

import android.util.Log;

public class ConnectionData {
	private static final String TAG = "CONNECTIONDATA";
	//public Method callback;
	public String data;
	private Method callb;
	private Object invoke;
	
	public ConnectionData(Method cb, Object inv, String str) {
		data = str;
		callb = cb;
		invoke = inv;
	}
	
	public void call(String res) {
		try {
			Log.v(TAG, "Callback: " + res + "d  d" + res.substring(0, 1));
			Boolean error = (res.substring(0, 1).contentEquals("0"));
			res = res.substring(1, res.length());
			callb.invoke(invoke, res, error);
		}
		catch(Exception e){
			Log.v(TAG, "Error: " + e.toString());
			Log.v(TAG, "Cause: " + e.getCause());
		}
	}
}
