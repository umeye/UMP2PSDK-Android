package com.example.extra.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ControlView extends View {
    private String TAG = "ControlView";
    MatrixUtils matrixUtils;
    float scale = 3f;
    RectF rectF = new RectF();
    Paint whiteRectPaint = new Paint();
    Paint greenRectPaint = new Paint();

    int padding = 10;
    PointF pointF = new PointF();

    boolean isTouch = false;

    public ControlView(Context context) {
        super(context);
        initView();
    }

    public ControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(MatrixUtils matrixUtils) {
        this.matrixUtils = matrixUtils;
        this.scale = matrixUtils.getScale();
        float rectW = widthSize / scale;
        float rectH = heightSize / scale;
        rectF.set((widthSize - rectW) / 2, (heightSize - rectH) / 2, (widthSize - rectW) / 2 + rectW, (heightSize - rectH) / 2 + rectH);
        postInvalidate();
    }

    void initView() {
        whiteRectPaint.setColor(Color.WHITE);
        whiteRectPaint.setStrokeWidth(3f);
        whiteRectPaint.setStyle(Paint.Style.STROKE);
        whiteRectPaint.setAntiAlias(true);
        greenRectPaint.setColor(Color.GREEN);
        greenRectPaint.setAntiAlias(true);
        greenRectPaint.setStrokeWidth(3f);
        greenRectPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rectF != null) {
            Paint paint = isTouch ? greenRectPaint : whiteRectPaint;
            canvas.drawLine(pointF.x, padding, pointF.x, rectF.top - padding, paint);
            canvas.drawLine(pointF.x, rectF.bottom + padding, pointF.x, heightSize - padding, paint);
            canvas.drawLine(padding, pointF.y, rectF.left - padding, pointF.y, paint);
            canvas.drawLine(rectF.right + padding, pointF.y, widthSize - padding, pointF.y, paint);
            canvas.drawRect(rectF, paint);
        }
        super.onDraw(canvas);

    }

    public RectF getRectF() {
        return rectF;
    }

    public void updateView(PointF pointF) {

        float rectW = widthSize / scale;
        float rectH = heightSize / scale;
        this.pointF.x = pointF.x + rectW / 2;
        this.pointF.y = pointF.y + rectH / 2;
        rectF.set(pointF.x, pointF.y, pointF.x + rectW, pointF.y + rectH);
        postInvalidate();
    }

    int widthSize = 0;
    int heightSize = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        heightSize = MeasureSpec.getSize(heightMeasureSpec); //

        float rectW = widthSize / scale;
        float rectH = heightSize / scale;
        pointF.x = (float) widthSize / 2;
        pointF.y = (float) heightSize / 2;
        rectF.set((widthSize - rectW) / 2, (heightSize - rectH) / 2, (widthSize - rectW) / 2 + rectW, (heightSize - rectH) / 2 + rectH);
//        Log.d(TAG, "宽的模式:" + widthMode);
//        Log.d(TAG, "宽的尺寸:" + widthSize);
//        Log.d(TAG, "高的模式:" + heightMode);
//        Log.d(TAG, "高的尺寸:" + heightSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            isTouch = true;
            Log.d(TAG, "MotionEvent.ACTION_DOWN");
            translateView(event);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            Log.d(TAG, "MotionEvent.ACTION_MOVE");
            translateView(event);
        } else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            isTouch = false;
            Log.d(TAG, "MotionEvent.ACTION_UP");
            smoothTranslateView(event);
        }
        return true;
    }


    /**
     * 点击平滑移动
     *
     * @param event
     * @return
     */
    private void smoothTranslateView(MotionEvent event) {
        PointF pointF = translateControlView(event.getX(), event.getY());
        if (matrixUtils != null && pointF != null)
            matrixUtils.translateSmooth(pointF);
    }

    /**
     * 移动
     *
     * @param event
     * @return
     */
    private void translateView(MotionEvent event) {
        PointF pointF = translateControlView(event.getX(), event.getY());
//                if (imagedeal != null && pointF != null)
//                    imagedeal.translate(pointF);
    }


    private PointF translateControlView(float px, float py) {
        RectF rectF = getRectF();
        int top = (int) (py - rectF.height() / 2);
        if (top < 0)
            top = 0;
        else if (top + rectF.height() > getHeight())
            top = (int) (getHeight() - rectF.height());

        int left = (int) (px - rectF.width() / 2);
        if (left < 0)
            left = 0;
        else if (left + rectF.width() > getWidth())
            left = (int) (getWidth() - rectF.width());
        PointF pointF1 = new PointF(left + rectF.width() / 2, top + rectF.height() / 2);
        updateView(new PointF(left, top));
        return pointF1;
    }
}
