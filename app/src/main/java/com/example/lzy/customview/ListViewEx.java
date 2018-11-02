package com.example.lzy.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by kson on 2016/9/2.
 */
public class ListViewEx extends ListView {

    private static final String TAG = "lzy";
    private int mLastX;
    private int mLastY;

    public ListViewEx(Context context) {
        super(context);
    }

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //子View的所有父ViewGroup都会跳过onInterceptTouchEvent的回调
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) > Math.abs(deltaY) + 5) {//水平滑动，使得父类可以执行onInterceptTouchEvent
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(ev);
    }
}
