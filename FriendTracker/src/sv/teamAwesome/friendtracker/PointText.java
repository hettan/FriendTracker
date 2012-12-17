package sv.teamAwesome.friendtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

public class PointText extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("Type in a header for your point");
		    final EditText input = new EditText(this); 
		    builder.setView(input);
		    builder.setCancelable(false);
		    builder.setPositiveButton("Set Point", new DialogInterface.OnClickListener() { 
		               public void onClick(DialogInterface dialog, int which) {
		            	   
		            	   finish();
		           }
		    });
			builder.setNegativeButton("Dont add point",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
					finish();
				}
			});
		    AlertDialog ad = builder.create();
		    ad.show();
	}
}
