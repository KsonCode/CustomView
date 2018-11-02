package com.example.lzy.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by kson on 2016/9/2.
 */
public class CircleView extends View {
    //默认的宽高，指定为wrap_content时
    private static final int DefaultWidth = 200;
    private static final int DefaultHeight = 200;
    private Paint mPaint;
    private int mColor;

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取布局中的属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = typedArray.getColor(R.styleable.CircleView_circle_color, Color.RED);
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //使得xml中设置的padding有效
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingBottom - paddingTop;
        int radius = Math.min(width, height) / 2;
        canvas.drawCircle(width / 2 + paddingLeft, height / 2 + paddingTop, radius, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //处理为wrap_content的情况
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        //AT_MOST模式下，宽高是等于父类可剩余的大小，这种状态下是和match_parent相同的
        //所以此时我们给他一个默认的值
        if (modeWidth == MeasureSpec.AT_MOST && modeHeight == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DefaultWidth, DefaultHeight);
        } else if (modeWidth == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DefaultWidth, sizeHeight);
        } else if (modeHeight == MeasureSpec.AT_MOST) {
            setMeasuredDimension(sizeWidth, DefaultHeight);
        }
    }
}
