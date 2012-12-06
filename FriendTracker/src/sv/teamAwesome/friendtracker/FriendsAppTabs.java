package sv.teamAwesome.friendtracker;

import android.content.Context;
import android.content.Intent;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class FriendsAppTabs {
	//private static final String TAG = "FRIENDS";
	final Object me = this;
	
    public static void setMyTabs(TabHost tabHost, Context context){

        TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
        TabSpec secondTabSpec = tabHost.newTabSpec("tid1");
 
        firstTabSpec.setIndicator("Online", context.getResources().getDrawable(R.drawable.friends));
        secondTabSpec.setIndicator("All", context.getResources().getDrawable(R.drawable.group));
 
        firstTabSpec.setContent(new Intent(context, OnlineTab.class));
        secondTabSpec.setContent(new Intent(context, AllTab.class));
 
        tabHost.addTab(firstTabSpec);
        tabHost.addTab(secondTabSpec);
 
        tabHost.getTabWidget().setCurrentTab(1);
        tabHost.setOnTabChangedListener(MyOnTabChangeListener);
 
        // Setting BackGround
//      for(int i=0; i<tabHost.getTabWidget().getChildCount(); i++)
//      {
//          tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
//      }
//
//      tabHost.getTabWidget().setCurrentTab(1);
//      tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.GRAY);
 
    }
 
    private static OnTabChangeListener MyOnTabChangeListener = new OnTabChangeListener(){
        public void onTabChanged(String tabId) {
        	
//          for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
//          {
//              tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
//          }
//
//          tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.GRAY);
        }
    };
	
}
