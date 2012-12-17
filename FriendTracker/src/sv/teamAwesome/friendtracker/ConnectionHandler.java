package sv.teamAwesome.friendtracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class ConnectionHandler extends AsyncTask<ConnectionData, Integer, String> {
	
	private static final String TAG = "CONNECTION";
	private ConnectionData connData;
	private static final int timeout = 5000;
	
	protected void onPostExecute(String result) {
		connData.call(result);
    }

	@Override
	protected String doInBackground(ConnectionData... params) {
		try{
			connData = params[0];
			//Socket s = new Socket(Config.SERVER_IP_ADRESS,Config.SERVER_PORT);
			Socket s = new Socket();
			s.connect(new InetSocketAddress(Config.SERVER_IP_ADRESS,Config.SERVER_PORT), timeout);
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out.println(params[0].data.length()+"\n\n"+params[0].data);
			out.flush();
			String buffer = in.readLine();

			out.close();
			s.close();
			
			return buffer;
		
	    } catch (UnknownHostException e) {
			Log.v(TAG, "UHError: " + e.toString());

	    } catch (IOException e) {
	    	Log.v(TAG, "IOError: " + e.toString());

	    } catch (Exception e) {
	    	Log.v(TAG, "EError: " + e.toString());

	    }
		return null;
	}
}
