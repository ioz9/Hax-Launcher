package com.t3hh4xx0r.haxlauncher.menu;

import org.apache.commons.lang3.ArrayUtils;

import com.t3hh4xx0r.haxlauncher.DBAdapter;
import com.t3hh4xx0r.haxlauncher.R;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class DockItemView extends ImageView {
	
    private GestureDetector mGestureDetector;
    private Runnable mSwipeCallback = null;
    private final Point mStartPoint = new Point();
    DockItemView item;
	private int id;
	private ViewGroup parent;
	private String data;
	Context ctx;
    
	public DockItemView(final Context context) {
		super(context);
		item = this;
		ctx = context;
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                        if (mSwipeCallback != null) {
                            if (Math.abs(vX) > Math.abs(vY)) {
                                int id;
                                if (vX > 0) {
                                    id = R.anim.slide_out_right;
                                } else {
                                    id = R.anim.slide_out_left;
                                }
                                Animation animation = AnimationUtils.loadAnimation(context, id);
                                startAnimation(animation);
                            	parent.removeView(item);
                            	DBAdapter db = new DBAdapter(ctx);
                            	db.open();
                            	db.removeHotseat(data);
                            	db.close();
                            	return true;
                            }
                        }
                        return false;
                    }
        });
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSwipeCallback != null) {
            boolean handled = mGestureDetector.onTouchEvent(event);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                    reset();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!handled) {
                        reset();
                    }
                    return handled;
                case MotionEvent.ACTION_MOVE:
                    int diffX = ((int) event.getX()) - mStartPoint.x;
                    scrollTo(-diffX, 0);
                    break;
                case MotionEvent.ACTION_DOWN:
                    mStartPoint.x = (int) event.getX();
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void reset() {
        scrollTo(0, 0);
    }

    public void setOnSwipeCallback(Runnable callback) {
        mSwipeCallback = callback;
    }	
    
    public void setId(int id) {
    	this.id = id;    	
    }
    
    public void setData(String data) {
    	this.data = data;    	
    }
    
    public void setParent(ViewGroup parent) {
    	this.parent = parent;    	
    }
 }
