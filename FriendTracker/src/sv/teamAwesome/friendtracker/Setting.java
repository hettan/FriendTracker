package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setting extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
	    
	/*	CheckBox repeatChkBx = (CheckBox) findViewById( R.id.check1 );
		
			repeatChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			    {
			        if ( isChecked )
			        {
			        	android.provider.Settings.System.putInt(getContentResolver(),
			        		     android.provider.Settings.System.SCREEN_BRIGHTNESS,255);
			        }

			    }
			});*/
	}
}
