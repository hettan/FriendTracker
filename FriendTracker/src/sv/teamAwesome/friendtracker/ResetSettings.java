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

 
/*
  SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
  StringBuilder builder = new StringBuilder(); 
  builder.append("\n" + sharedPrefs.getBoolean("perform_updates", false));
  builder.append("\n" + sharedPrefs.getString("updates_interval", "-1"));
  builder.append("\n" + sharedPrefs.getString("status_msg", "NULL"));

  TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
  settingsTextView.setText(builder.toString());
*/
 }