package sv.teamAwesome.friendtracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import com.google.android.maps.MapView;
import com.readystatesoftware.maps.OnSingleTapListener;

public class TapControlledMapView extends MapView implements OnGestureListener {

    private GestureDetector gd;    
    private OnSingleTapListener singleTapListener;
 //   private OnDoubleTapListener doubleTapListener;

	public TapControlledMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupGestures();
    }

    public TapControlledMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupGestures();
    }

    public TapControlledMapView(Context context, String apiKey) {
        super(context, apiKey);
        setupGestures();
    }
    
    private void setupGestures() {
    	gd = new GestureDetector(this);  
       
        //set the on Double tap listener  
        gd.setOnDoubleTapListener(new OnDoubleTapListener() {

			public boolean onSingleTapConfirmed(MotionEvent e) {
			    //final MapController mc = TapControlledMapView.this.getController();
				if (singleTapListener != null) {
//		              GeoPoint p = TapControlledMapView.this.getProjection().fromPixels(
//			                   (int) e.getX(),
//			                   (int) e.getY());
//					mc.animateTo(p);
					return singleTapListener.onSingleTap(e);
				} else {
					return false;
				}
			}

			public boolean onDoubleTap(MotionEvent e) {
				TapControlledMapView.this.getController().zoomInFixing((int) e.getX(), (int) e.getY());
				return false;
//				if(doubleTapListener != null) {
//					return doubleTapListener.onDoubleTap(e);
//				}
//				else{
//				return false;
//				}
			}

			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}
			
        });
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (this.gd.onTouchEvent(ev)) {
			return true;
		} else {
			return super.onTouchEvent(ev);
		}
	}
	
	public void setOnSingleTapListener(OnSingleTapListener singleTapListener) {
		this.singleTapListener = singleTapListener;
	}
//	public void setOnDoubleTapListener(OnDoubleTapListener doubleTapListener) {
//		this.doubleTapListener = doubleTapListener;
//	}
//	public void setOnLongClickListener(OnLongClickListener longlistener)
//	{
//		this.longClickListener = longlistener;
//	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public void onShowPress(MotionEvent e) {}

	public boolean onSingleTapUp(MotionEvent e) {
     return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}
}