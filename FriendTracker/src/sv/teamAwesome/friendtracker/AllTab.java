package sv.teamAwesome.friendtracker;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AllTab extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.andersMamma = this;
        TextView textView = new TextView(this);
        textView.setText("All Friends");
        setContentView(textView);

    }


}
