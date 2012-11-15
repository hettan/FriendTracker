package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class FrontPage extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frontpage);
	}
	
	public void MenuBtn(View v) {
		EditText err = (EditText) findViewById(R.id.err);
		err.setText(v.getTag().toString());
	}	
}