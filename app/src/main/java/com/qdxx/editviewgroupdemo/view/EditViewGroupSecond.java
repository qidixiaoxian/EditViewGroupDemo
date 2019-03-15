package com.qdxx.editviewgroupdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.qdxx.editviewgroupdemo.util.UIUtil;

/**
 * @Auther: 齐地小贤
 * @Date: 2019/3/15 17:13
 * @Description:自定义布局容器
 */
public class EditViewGroupSecond extends ViewGroup {
    private int measuredWidth;
    private int measuredHeight;
    //裁剪框边框画笔
    private Paint mBorderPaint;
    //绘制裁剪边框四个角的画笔
    private Paint mCornerPaint;
    //判断手指位置是否处于缩放裁剪框位置的范围：如果是当手指移动的时候裁剪框会相应的变化大小
    //否则手指移动的时候就是拖动裁剪框使之随着手指移动
    private float mScaleRadius;

    private float mCornerThickness;

    private float mBorderThickness;
    //四个角小短边的长度
    private float mCornerLength;
    //滑动事件类型
    private String LEFT = "LEFT";
    private String RIGHT = "RIGHT";
    private String TOP = "TOP";
    private String BOTTOM = "BOTTOM";
    private String CENTER = "CENTER";
    private String TOP_LEFT = "TOP_LEFT";
    private String TOP_RIGHT = "TOP_RIGHT";
    private String BOTTOM_LEFT = "BOTTOM_LEFT";
    private String BOTTOM_RIGHT = "BOTTOM_RIGHT";

    public EditViewGroupSecond(Context context) {
        super(context);
        init(context);
    }

    public EditViewGroupSecond(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditViewGroupSecond(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制裁剪边框
        drawBorder(canvas);
        //绘制裁剪边框的四个角
        drawCorners(canvas);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //控件宽
        measuredWidth = getMeasuredWidth();
        //控件高
        measuredHeight = getMeasuredHeight();
       /* Log.e("app", "measuredWidth:" + measuredWidth);
        Log.e("app", "measuredHeight:" + measuredHeight);*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        initCropWindow();

    }

    private void initCropWindow() {

    }

    //初始化
    private void init(Context context) {
        this.setBackgroundColor(Color.WHITE);
        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(UIUtil.dip2px(context, 3));
        //mBorderPaint.setColor(Color.parseColor("#AAFFFFFF"));
        mBorderPaint.setColor(Color.GRAY);

        mCornerPaint = new Paint();
        mCornerPaint.setStyle(Paint.Style.STROKE);
        mCornerPaint.setStrokeWidth(UIUtil.dip2px(context, 5));
        mCornerPaint.setColor(Color.RED);


        mScaleRadius = UIUtil.dip2px(context, 24);
        mBorderThickness = UIUtil.dip2px(context, 3);
        mCornerThickness = UIUtil.dip2px(context, 5);
        mCornerLength = UIUtil.dip2px(context, 20);
    }


    private void drawBorder(Canvas canvas) {
        canvas.drawRect(this.getLeft(),
                this.getTop(),
                this.getRight(),
                this.getBottom(),
                mBorderPaint);
    }


    private void drawCorners(Canvas canvas) {
        final float left = this.getLeft();
        final float top = this.getTop();
        final float right = this.getRight();
        final float bottom = this.getBottom();

        //简单的数学计算

        final float lateralOffset = (mCornerThickness - mBorderThickness) / 2f;
        final float startOffset = mCornerThickness - (mBorderThickness / 2f);

        //左上角左面的短线
        canvas.drawLine(left - lateralOffset, top - startOffset, left - lateralOffset,
                top + mCornerLength, mCornerPaint);
        //左上角上面的短线
        canvas.drawLine(left - startOffset, top - lateralOffset, left + mCornerLength,
                top - lateralOffset, mCornerPaint);

        //右上角右面的短线
        canvas.drawLine(right + lateralOffset, top - startOffset, right + lateralOffset,
                top + mCornerLength, mCornerPaint);
        //右上角上面的短线
        canvas.drawLine(right + startOffset, top - lateralOffset, right - mCornerLength,
                top - lateralOffset, mCornerPaint);

        //左下角左面的短线
        canvas.drawLine(left - lateralOffset, bottom + startOffset, left - lateralOffset,
                bottom - mCornerLength, mCornerPaint);
        //左下角底部的短线
        canvas.drawLine(left - startOffset, bottom + lateralOffset, left + mCornerLength,
                bottom + lateralOffset, mCornerPaint);

        //右下角左面的短线
        canvas.drawLine(right + lateralOffset, bottom + startOffset, right + lateralOffset,
                bottom - mCornerLength, mCornerPaint);
        //右下角底部的短线
        canvas.drawLine(right + startOffset, bottom + lateralOffset, right - mCornerLength,
                bottom + lateralOffset, mCornerPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                onActionDown(event.getX(), event.getY());
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onActionUp();
                return true;

            case MotionEvent.ACTION_MOVE:
                onActionMove(event.getX(), event.getY());
                getParent().requestDisallowInterceptTouchEvent(true);
                return true;

            default:
                return false;
        }
    }

    private void onActionDown(float x, float y) {
        //获取边框的上下左右四个坐标点的坐标
        final float left = this.getLeft();
        final float top = this.getTop();
        final float right = this.getRight();
        final float bottom = this.getBottom();

    }

    private void onActionMove(float x, float y) {

    }

    private void onActionUp() {

    }
}
