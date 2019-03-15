package com.qdxx.editviewgroupdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.qdxx.editviewgroupdemo.edge.Edge;
import com.qdxx.editviewgroupdemo.handle.CropWindowEdgeSelector;
import com.qdxx.editviewgroupdemo.util.CatchEdgeUtil;
import com.qdxx.editviewgroupdemo.util.UIUtil;

/**
 * @Auther: 齐地小贤
 * @Date: 2019/3/15 11:39
 * @Description:自定义布局容器
 */
public class EditViewGroup extends ViewGroup {
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

    //手指位置距离裁剪框的偏移量
    private PointF mTouchOffset = new PointF();

    //用来表示图片边界的矩形
    private RectF mBitmapRect = new RectF();

    private CropWindowEdgeSelector mPressedCropWindowEdgeSelector;

    public EditViewGroup(Context context) {
        super(context);
        init(context);
    }

    public EditViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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
        mCornerPaint.setColor(Color.BLACK);


        mScaleRadius = UIUtil.dip2px(context, 24);
        mBorderThickness = UIUtil.dip2px(context, 3);
        mCornerThickness = UIUtil.dip2px(context, 5);
        mCornerLength = UIUtil.dip2px(context, 20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //控件宽
        measuredWidth = getMeasuredWidth();
        //控件高
        measuredHeight = getMeasuredHeight();
        Log.e("app", "measuredWidth:" + measuredWidth);
        Log.e("app", "measuredHeight:" + measuredHeight);
    }

    @Override

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e("app",
                "onLayout----" + "changed:" + changed + "---left:" + left + "---top:" + top +
                        "---right:" + right + "---bottom:" + bottom);
        mBitmapRect = getBitmapRect();
        //initCropWindow(mBitmapRect);
        initCropWindow();

    }

    /**
     * 获取图片ImageView周围的边界组成的RectF对象
     */
    private RectF getBitmapRect() {

        /*final Drawable drawable = getDrawable();
        if (drawable == null) {
            return new RectF();
        }

        final float[] matrixValues = new float[9];
        getImageMatrix().getValues(matrixValues);

        final float scaleX = matrixValues[Matrix.MSCALE_X];
        final float scaleY = matrixValues[Matrix.MSCALE_Y];
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        final int drawableIntrinsicWidth = drawable.getIntrinsicWidth();
        final int drawableIntrinsicHeight = drawable.getIntrinsicHeight();

        final int drawableDisplayWidth = Math.round(drawableIntrinsicWidth * scaleX);
        final int drawableDisplayHeight = Math.round(drawableIntrinsicHeight * scaleY);

        final float left = Math.max(transX, 0);
        final float top = Math.max(transY, 0);
        final float right = Math.min(left + drawableDisplayWidth, getWidth());
        final float bottom = Math.min(top + drawableDisplayHeight, getHeight());*/

        return new RectF(0, 0, measuredWidth, measuredHeight);
    }

    //初始化裁剪框
    private void initCropWindow() {
        //裁剪框距离图片左右的padding值
       /* final float horizontalPadding = 0.01f * measuredWidth;
        final float verticalPadding = 0.01f * measuredHeight;*/
        final float horizontalPadding = 0;
        final float verticalPadding = 0;

        //初始化裁剪框上下左右四条边
        Edge.LEFT.initCoordinate(this.getLeft() + horizontalPadding);
        Edge.TOP.initCoordinate(this.getTop() + verticalPadding);
        Edge.RIGHT.initCoordinate(this.getRight() - horizontalPadding);
        Edge.BOTTOM.initCoordinate(this.getBottom() - verticalPadding);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制九宫格引导线
        //drawGuidelines(canvas);
        //绘制裁剪边框
        drawBorder(canvas);
        //绘制裁剪边框的四个角
        drawCorners(canvas);

    }

    private void drawBorder(Canvas canvas) {
        canvas.drawRect(Edge.LEFT.getCoordinate(),
                Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(),
                Edge.BOTTOM.getCoordinate(),
                mBorderPaint);
    }

    private void drawCorners(Canvas canvas) {
        final float left = Edge.LEFT.getCoordinate();
        final float top = Edge.TOP.getCoordinate();
        final float right = Edge.RIGHT.getCoordinate();
        final float bottom = Edge.BOTTOM.getCoordinate();

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
        {

            //获取边框的上下左右四个坐标点的坐标
            final float left = Edge.LEFT.getCoordinate();
            final float top = Edge.TOP.getCoordinate();
            final float right = Edge.RIGHT.getCoordinate();
            final float bottom = Edge.BOTTOM.getCoordinate();


            //获取手指所在位置位于图二种的A，B，C，D位置种哪一种
            mPressedCropWindowEdgeSelector = CatchEdgeUtil.getPressedHandle(x, y, left, top, right,
                    bottom, mScaleRadius);
            //Log.e("app", "mPressedCropWindowEdgeSelector:" + mPressedCropWindowEdgeSelector);

            if (mPressedCropWindowEdgeSelector != null) {
                //计算手指按下的位置与裁剪框的偏移量
                CatchEdgeUtil.getOffset(mPressedCropWindowEdgeSelector, x, y, left, top, right,
                        bottom, mTouchOffset);
                //invalidate();
            }
        }

    }

    private void onActionUp() {
        if (mPressedCropWindowEdgeSelector != null) {
            mPressedCropWindowEdgeSelector = null;
            //invalidate();
        }

    }

    private void onActionMove(float x, float y) {
        if (mPressedCropWindowEdgeSelector == null) {
            return;
        }

        x += mTouchOffset.x;
        y += mTouchOffset.y;
        Log.e("app", "x:" + x + "---------y:" + y);
        mPressedCropWindowEdgeSelector.updateCropWindow(x, y, mBitmapRect);
        Log.e("app", "x:" + x + "-------y:" + y);
        invalidate();
        this.setTranslationX(Edge.LEFT.getCoordinate());
        this.setTranslationY(Edge.TOP.getCoordinate());





      /*  mPressedCropWindowEdgeSelector.updateCropWindow(x, y, mBitmapRect);
        invalidate();
        //控件本身的宽高改变
        *//*canvas.drawRect(Edge.LEFT.getCoordinate(),
                Edge.TOP.getCoordinate(),
                Edge.RIGHT.getCoordinate(),
                Edge.BOTTOM.getCoordinate(),
                mBorderPaint);*//*
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        *//*layoutParams.width = (int) (Edge.RIGHT.getCoordinate() - Edge.LEFT.getCoordinate());
        layoutParams.height = (int) (Edge.BOTTOM.getCoordinate() - Edge.TOP.getCoordinate());
        this.setLayoutParams(layoutParams);*//*
         *//* this.setTranslationX(Edge.LEFT.getCoordinate());
        this.setTranslationY(Edge.TOP.getCoordinate());*/

    }


}
