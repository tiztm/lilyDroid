
package com.sonyericsson.zoom;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Simple on touch listener for zoom view
 */
public class SimpleZoomListener implements View.OnTouchListener {
	
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	
	private int mode = NONE;
	private float oldDist;
	
	private float startZoom;
	
	private PointF start = new PointF();
	private PointF mid = new PointF();

    /**
     * Which type of control is used
     */
    public enum ControlType {
        PAN, ZOOM
    }

    /** State being controlled by touch events */
    private ZoomState mState;

    /** Current control type being used */
    private ControlType mControlType = ControlType.ZOOM;

    /** X-coordinate of previously handled touch event */
    private float mX;

    /** Y-coordinate of previously handled touch event */
    private float mY;

    /**
     * Sets the zoom state that should be controlled
     * 
     * @param state Zoom state
     */
    public void setZoomState(ZoomState state) {
        mState = state;
    }

    /**
     * Sets the control type to use
     * 
     * @param controlType Control type
     */
    public void setControlType(ControlType controlType) {
        mControlType = controlType;
    }

    // implements View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mY = y;
                mode = DRAG;
                break;
                
            case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				Log.d("oldDist", "oldDist:"+oldDist);
				if (oldDist > 10f) {
					startZoom = mState.getZoom();
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;

            case MotionEvent.ACTION_MOVE: {
            	
            	if (mode == ZOOM) {
            		
            		
            		float newDist = spacing(event);
					if (newDist > 10f) {
						Log.d("newDist", "newDist:"+newDist);
						float scale = newDist / oldDist;
	                  mState.setZoom( startZoom * scale);
	                  mState.notifyObservers();
						
					}
            		


					
				} else if (mode == DRAG) {
            	
            	 float dx =0;
            	 float dy = 0;
            	if((mState.getmPicY()/v.getHeight())<(mState.getmPicX()/v.getWidth()))
            	{
            	
                dx = (x - mX) / (v.getWidth()*mState.getZoom());
                 dy = (y - mY) / (mState.getmPicY()*(v.getWidth()/mState.getmPicX())*mState.getZoom());
            	}
            	else
            	{
            		dx = (x - mX) / ( (mState.getmPicX()*(v.getHeight()/mState.getmPicY())  *mState.getZoom()));
                    dy = (y - mY) / (v.getHeight()*mState.getZoom());
            	}

                    mState.setPanX(mState.getPanX() - dx);
                    mState.setPanY(mState.getPanY() - dy);
                    mState.notifyObservers();

                mX = x;
                mY = y;
				}
                break;
            }

        }

        return true;
    }
    
    private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
