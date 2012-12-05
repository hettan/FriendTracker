package sv.teamAwesome.friendtracker;

import java.util.Dictionary;

public class Config {
	
	public static long USER_POSITION_UPDATE_INTERVAL = 2000;
	public static String USERNAME = "";
	public static String SESSION_ID = "";
	public static Object andersMamma = null;
	public static Dictionary<String, String> STATUS;
	
	// Sender id for GCM registration
	public static String SENDER_ID = "377927318664";
	
	// IP-adress and port for server.
	public static String SERVER_IP_ADRESS = "130.236.187.198";
	public static int SERVER_PORT = 8080;
}
