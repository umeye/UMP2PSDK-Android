package com.example.extra.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author
 */
public class MatrixUtils {

    private static MatrixUtils matrixUtils = null;
    private static final String TAG = "MatrixUtils";

    private Matrix matrix = new Matrix();
    private ImageView img;

    /**
     * @param img   双屏子视图
     * @param scale 放大倍数
     * @return
     */
    public static MatrixUtils getInstance(ImageView img, float scale) {
        if (matrixUtils == null) {
            matrixUtils = new MatrixUtils();
        }
        img.setScaleType(ImageView.ScaleType.MATRIX);
        matrixUtils.img = img;
        matrixUtils.initScale(new PointF((float) img.getWidth() / 2, (float) img.getHeight() / 2), scale);
        return matrixUtils;
    }


    private float scale = 2f;

    PointF preCenterF;
    int index = 0;

    /**
     * 初始化
     *
     * @param centerF 中心点
     * @param scale   放大倍数
     */
    public void initScale(final PointF centerF, float scale) {
        this.scale = scale;
        Drawable drf = img.getDrawable();
        if (drf != null) {
            Bitmap rf = ((BitmapDrawable) drf).getBitmap();
            if (rf != null) {
                float radioX;
                float radioY;
                radioX = img.getWidth() / (float) rf.getWidth();
                radioY = img.getHeight() / (float) rf.getHeight();
                preCenterF = centerF;
                Log.d(TAG, "scale1=" + radioX + ",scale2=" + radioY
                        + ",getWidth:" + img.getWidth()
                        + ",getHeight:" + img.getHeight()
                        + ",rf getWidth:" + rf.getWidth()
                        + ",rf getHeight:" + rf.getHeight());
                float scaleX, scaleY;
                scaleX = radioX * scale;
                scaleY = radioY * scale;
                matrix = new Matrix();
                matrix.postScale(scaleX, scaleY);
                setMaxtrix();
                RectF rectF = getRect(img);
                Log.d(TAG, rectF.toString()
                        + ",getRect getWidth:" + rectF.width()
                        + ",getRect getHeight:" + rectF.height() + ",getRect center=" + rectF.centerX() + "," + rectF.centerY());
                matrix.postTranslate(-(rectF.centerX() - centerF.x), -(rectF.centerY() - centerF.y));
                setMaxtrix();

            }
        }
    }

    public float getScale() {
        return scale;
    }

    /**
     * 平滑移动
     *
     * @param centerF 中心点
     */
    public void translateSmooth(final PointF centerF) {
        if (matrix == null) {
            Log.e(TAG, "no  initScale!!!!");
            return;
        }
        if (index != 0)
            return;
        final int times = 20;
        h.post(new Runnable() {
            @Override
            public void run() {
                index++;
                float px, py;
                Log.d(TAG, "translateSmooth centerF:" + centerF.toString()
                        + ",preCenterF:" + preCenterF.toString());
//                Log.d(TAG, rectF.toString()
//                        + ",getRect getWidth:" + rectF.width()
//                        + ",getRect getHeight:" + rectF.height() + ",getRect center=" + rectF.centerX() + "," + rectF.centerY());
                if (centerF.x > preCenterF.x) {
                    px = -((Math.abs(centerF.x - preCenterF.x) * scale)) / times;
                } else {
                    px = ((Math.abs(centerF.x - preCenterF.x) * scale)) / times;
                }
                if (centerF.y > preCenterF.y) {
                    py = -((Math.abs(centerF.y - preCenterF.y) * scale)) / times;
                } else {
                    py = ((Math.abs(centerF.y - preCenterF.y) * scale)) / times;
                }
                matrix.postTranslate(px, py);
                setMaxtrix();
                Log.i(TAG, "centerF:" + centerF.toString() + ",px=" + px + ",py=" + py);
//                RectF rectF1 = getRect(img);
//                Log.d(TAG, rectF1.toString()
//                        + ",getRect getWidth:" + rectF1.width()
//                        + ",getRect getHeight:" + rectF1.height() + ",getRect center=" + rectF1.centerX() + "," + rectF1.centerY());

                if (index != times) {
                    h.postDelayed(this, 20);
                } else {
                    preCenterF = centerF;
                    index = 0;
                    Log.i(TAG, "End");
                }
            }
        });
    }

    /**
     * 移动
     *
     * @param centerF 中心点
     */
    public void translate(final PointF centerF) {
        if (matrix == null) {
            Log.e(TAG, "no  initScale!!!!");
            return;
        }
        if (centerF.x == preCenterF.x && centerF.y == preCenterF.y)
            return;
        float px, py;
        Log.d(TAG, "translate centerF:" + centerF.toString()
                + ",preCenterF:" + preCenterF.toString());
        if (centerF.x > preCenterF.x) {
            px = -((Math.abs(centerF.x - preCenterF.x) * scale));
        } else {
            px = ((Math.abs(centerF.x - preCenterF.x) * scale));
        }
        if (centerF.y > preCenterF.y) {
            py = -((Math.abs(centerF.y - preCenterF.y) * scale));
        } else {
            py = ((Math.abs(centerF.y - preCenterF.y) * scale));
        }
        Log.i(TAG, "centerF:" + centerF.toString() + ",px=" + px + ",py=" + py);
        matrix.postTranslate(px, py);
        setMaxtrix();
        preCenterF = centerF;
        Log.i(TAG, "End");

    }

    boolean setMaxtrix() {
        img.setImageMatrix(matrix);
        if (scale < 1) {
            scale = 1;
            return true;
        }
        return false;
    }

    Handler h = new Handler();

    public void stop() {
        if (matrixUtils != null) {
            matrixUtils = null;
        }
    }


    public float[] get(ImageView v) {
        float[] t = new float[2];
        t[0] = getRect(v).height();
        t[1] = getRect(v).width();
        return t;
    }

    public RectF getRect(ImageView v) {
        Matrix m = new Matrix();
        m.set(matrix);
        Drawable drawable = v.getDrawable();
        if (drawable == null) {
            return null;
        }
        Rect rect1 = drawable.getBounds();
        RectF rect = new RectF(0, 0, rect1.width(), rect1.height());
        m.mapRect(rect);
        return rect;
    }


}
