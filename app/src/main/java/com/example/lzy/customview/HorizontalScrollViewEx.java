package com.example.lzy.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by kson on 2016/9/2.
 */
public class HorizontalScrollViewEx extends ViewGroup {
    private static final String TAG = "lzy";
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    //分别记录上次滑动的坐标(onInterceptTouchEvent)
    private int mLastXIntercept;
    private int mLastYIntercept;
    //上次滑动的坐标
    private int mLastX;
    private int mLastY;
    private int mChildIndex;
    private int mChildWith;
    private int mChildrenSize;


    public HorizontalScrollViewEx(Context context) {
        super(context);
        init();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (mScroller == null) {
            mScroller = new Scroller(getContext());
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //down事件不拦截，否则无法传给子元素
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastXIntercept;
                int deltaY = y - mLastYIntercept;
                //水平滑动则拦截
                if (Math.abs(deltaX) > Math.abs(deltaY) + 5) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                //不拦截，否则子元素无法收到
                intercepted = false;
                break;
        }
        //因为当ViewGroup中的子View可能消耗了down事件，在onTouchEvent无法获取，
        // 无法对mLastX赋初值，所以在这里赋值一次
        mLastX = x;
        mLastY = y;
        mLastYIntercept = y;
        mLastXIntercept = x;
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (getScrollX() < 0) {
                    scrollTo(0, 0);
                }
                scrollBy(-deltaX, 0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocityTracker = mVelocityTracker.getXVelocity();
                if (Math.abs(xVelocityTracker) > 50) {//速度大于50则滑动到下一个
                    mChildIndex = xVelocityTracker > 0 ? mChildIndex - 1 : mChildIndex + 1;
                } else {
                    mChildIndex = (scrollX + mChildWith / 2) / mChildWith;
                }
                mChildIndex = Math.max(0, Math.min(mChildIndex, mChildrenSize - 1));
                int dx = mChildIndex * mChildWith - scrollX;
                smoothScrollBy(dx, 0);
                mVelocityTracker.clear();
                break;
        }
        mLastY = y;
        mLastX = x;
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = 0;
        int measureHeight = 0;
        int childCount = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //这里是处理wrap_content的情况
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            View child = getChildAt(0);
            measureWidth = child.getMeasuredWidth() * childCount;
            //假定子View高度与ViewGroup相同
            measureHeight = child.getMeasuredHeight();
            setMeasuredDimension(measureWidth, measureHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            View child = getChildAt(0);
            measureWidth = child.getMeasuredWidth() * childCount;
            setMeasuredDimension(measureWidth, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            View child = getChildAt(0);
            measureHeight = child.getMeasuredHeight();
            setMeasuredDimension(widthSize, measureHeight);
        }
    }

    private void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), 0, dx, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        int childCount = getChildCount();
        mChildrenSize = childCount;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() != View.GONE) {
                int measuredWidth = childAt.getMeasuredWidth();
                mChildWith = measuredWidth;
                childAt.layout(childLeft, 0, childLeft + measuredWidth, getMeasuredHeight());
                childLeft += measuredWidth;
            }
        }
    }

    //当所在Activity退出或者view被remove时会被调用
    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
}
