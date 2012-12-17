package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class ResetSettings extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
	 super.onCreate(savedInstanceState);
	 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

     Editor editor = prefs.edit();
     editor.clear();
     editor.commit();
     
     finish();
     startActivity(new Intent(this, Setting.class));
 	}
 }