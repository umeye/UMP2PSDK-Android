package com.example.extra.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.Player.Core.PlayerCore;
import com.Player.Source.TDateTime;
import com.Player.Source.TVideoFile;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Show;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SeekTimeBar extends View {
    boolean isSmallscaleMode = true;// 是否是小刻度模式，即一个刻度长度比较短
    final int LONG_MARGIN = 160; // 大刻度
    final int SHORT_MARGIN = 40;// 小刻度
    int MARGIN = 40; // 刻度间隔
    final int SCALE_TOTAL = 144;// 刻度总数
    final int HOUR_SCALES = 6;// 每小时刻度数
    final int DAY_TIME_MILLES = 24 * 3600; // 一天总时间秒
    final String SPLITS = "&";
    final String SPLITS1 = ":";
    long dateStart = 0;
    int PANDDING = 20;
    final int PANDDING_TOP = 30;
    public boolean mAlwaysOverrideTouch = true;
    private boolean isSeeking = false; // 是否在拖动中

    private boolean isSeeked = false; // 是否拖动了 如果拖动了 将要将播放到拖动后指定的时间
    protected int mCurrentX;
    protected double mNextX;
    private int mMaxX = Integer.MAX_VALUE;
    protected Scroller mScroller;
    private GestureDetector mGesture;
    private Bitmap bmpLongScale, bmpShortScale, bmpCenter;
    private boolean mDataChanged = false;
    Context context;
    Paint paint, txtPaint, timePaint, fileFildGreenPaint, fileFildRedPaint,
            fileFildYellowPaint;
    int txtPaintWidth = 0, txtPaintHeight = 0;// 显示刻度时间 文本 宽高
    int txtDateWidth = 0, txtDateHeight = 0;// 显示日期文本 宽高
    int txtTimeWidth = 0, txtTimeHeight = 0;// 显示当前指针时间文本 宽高
    int scaleLong = 0;// 总刻度长度 单位 像素
    double lastX = 0;
    boolean isStoping = false;
    double currentIndex = 0;
    String date = "";
    OnTimeListener timeListener;
    double tempTimes = 0;

    PlayerCore playCore;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy&HH:mm:ss");
    private TDateTime tPlayEndTime;
    private int iPlayType;
    private TDateTime tPlayBeginTime;
    private List<TVideoFile> data;

    public SeekTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public SeekTimeBar(Context context) {
        super(context);
    }

    /**
     * TypedValue.COMPLEX_UNIT_*
     *
     * @param size
     * @return
     */
    public float getRawSize(float size) {
        Resources r;
        if (context == null)
            r = Resources.getSystem();
        else
            r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size,
                r.getDisplayMetrics());
    }

    private synchronized void initView() {
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextSize(getRawSize(12));
        txtPaint.setColor(Color.GRAY);

        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setTextSize(getRawSize(16));
        timePaint.setColor(Color.GRAY);

        fileFildGreenPaint = new Paint();
        fileFildGreenPaint.setAntiAlias(true);
//		fileFildGreenPaint.setColor(Color.GREEN);
        fileFildGreenPaint.setColor(Color.GREEN);
        fileFildRedPaint = new Paint();
        fileFildRedPaint.setAntiAlias(true);
        fileFildRedPaint.setColor(Color.RED);
        fileFildYellowPaint = new Paint();
        fileFildYellowPaint.setAntiAlias(true);
        fileFildYellowPaint.setColor(Color.YELLOW);

        // 计算显示刻度时间文本的宽高

        String format = "00:00";
        Rect rect = computeTextScale(txtPaint, format);
        txtPaintWidth = rect.width();
        txtPaintHeight = rect.height();
        PANDDING = txtPaintWidth / 2 + 5;

        String format1 = "24/07/2014";
        Rect rect1 = computeTextScale(timePaint, format1);
        txtDateWidth = rect1.width();
        txtDateHeight = rect1.height();

        String format2 = "14:55:55";
        Rect rect2 = computeTextScale(timePaint, format2);
        txtTimeWidth = rect2.width();
        txtTimeHeight = rect2.height();

        bmpLongScale = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.nordd0429_img_long);

        bmpShortScale = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.nordd0429_img_short);
        bmpCenter = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.nordd0429_img_center);


//		bmpLongScale = BitmapFactory.decodeResource(this.getResources(),
//				R.drawable.shap_white_point);
//
//		bmpShortScale = BitmapFactory.decodeResource(this.getResources(),
//				R.drawable.shap_white_point_samll);
//		bmpCenter = BitmapFactory.decodeResource(this.getResources(),
//				R.drawable.time_line);

        mScroller = new Scroller(getContext());
        mGesture = new GestureDetector(getContext(), mOnGesture);
    }

    public PlayerCore getPlayCore() {
        return playCore;
    }

    public void setPlayCoreAndParameters(PlayerCore playCore,
                                         TDateTime tPlayBeginTime, TDateTime tPlayEndTime, int iPlayType) {
        this.playCore = playCore;
        if (tPlayBeginTime != null) {
            TDateTime startTmp = new TDateTime();
            startTmp.iDay = tPlayBeginTime.iDay;
            startTmp.iYear = tPlayBeginTime.iYear;
            startTmp.iHour = tPlayBeginTime.iHour;
            startTmp.iMonth = tPlayBeginTime.iMonth;
            startTmp.iMinute = tPlayBeginTime.iMinute;
            startTmp.iSecond = tPlayBeginTime.iSecond;
            this.tPlayBeginTime = startTmp;
        } else {
            this.tPlayBeginTime = null;
        }
        if (tPlayEndTime != null) {
            TDateTime endTmp = new TDateTime();
            endTmp.iDay = tPlayEndTime.iDay;
            endTmp.iYear = tPlayEndTime.iYear;
            endTmp.iHour = tPlayEndTime.iHour;
            endTmp.iMonth = tPlayEndTime.iMonth;
            endTmp.iMinute = tPlayEndTime.iMinute;
            endTmp.iSecond = tPlayEndTime.iSecond;
            this.tPlayEndTime = endTmp;
        } else {
            this.tPlayEndTime = null;
        }

        this.iPlayType = iPlayType;
        isSeeked = false;
        isSeeking = false;

    }

    public void setSeekFlag(boolean isSeeked) {
        this.isSeeked = isSeeked;
        this.isSeeking = false;
    }

    public void setPlayCore(PlayerCore playCore) {
        this.playCore = playCore;

    }

    public OnTimeListener getTimeListener() {
        return timeListener;
    }

    public void setTimeListener(OnTimeListener timeListener) {
        this.timeListener = timeListener;
    }

    public void setDate(String date) {
        // Calendar c = Calendar.getInstance();
        // try {
        // c.setTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(date));
        // dateStart = c.getTimeInMillis();
        // Log.i("mNextX", "时间转化后的毫秒数为：" + dateStart);
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //
        // Log.i("mNextX", "时间毫秒数转化日期为：" + sdf.format(new Date(dateStart)));
        // } catch (ParseException e) {
        //
        // e.printStackTrace();
        // }
        this.date = date;

    }

    public int getScaleLong() {
        return scaleLong;
    }

    public void setScaleLong(int scaleLong) {
        this.scaleLong = scaleLong;
    }

    @Override
    protected synchronized void onLayout(boolean changed, int left, int top,
                                         int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {


        int left = 0;
        int center = (int) (getWidth() / 2.0f);
        // if (scaleLong == 0) {
        scaleLong = computeScaleLong();
        // }
        if (scaleLong != 0) {

            if (mNextX > scaleLong - center + PANDDING
                    || mNextX < -(center - PANDDING)) {
                mNextX = lastX;

                mScroller.forceFinished(true);
                // return;
            } else {
                lastX = mNextX;
            }
        }
        currentIndex = mNextX - PANDDING + center; // 当前 指向的位置
        left = (int) (-mNextX + PANDDING) - 3;
        if (data != null && data.size() > 0) {
            tempIndexEnd = 0;
            for (int i = 0; i < data.size(); i++) {
                drawFildRect(canvas, data.get(i));
            }
        }
        for (int i = 0; i < SCALE_TOTAL + 1; i++) {

            if (i % HOUR_SCALES == 0) {

                canvas.drawBitmap(bmpLongScale, left, txtPaintHeight
                        + txtDateHeight + 35, paint);
                canvas.drawText(formatTime((i / HOUR_SCALES)), left
                        - txtPaintWidth / 2, txtPaintHeight + txtDateHeight
                        + 20, txtPaint);
                left = left + bmpLongScale.getWidth();
            } else {

                canvas.drawBitmap(bmpShortScale, left, txtPaintHeight
                        + txtDateHeight + 45, paint);
                left = left + bmpShortScale.getWidth();
            }

            left = left + MARGIN;
        }

        // canvas.drawBitmap(bmpCenter, center, 0, paint);
        // canvas.drawLine(, paint);

        Rect rect = new Rect(center, 2, center + 4, getHeight() - 2);
        // Log.i("drawFildRect", "rect:" + rect.toString());
        canvas.drawRect(rect, paint);
        //
        canvas.drawText(date, center - txtDateWidth - 15, txtDateHeight + 10,
                timePaint);
        canvas.drawText(hms, center + 15, txtDateHeight + 10, timePaint);

        super.onDraw(canvas);
    }

    public String formatTime(int time) {
        return (time < 9 ? "0" + time : "" + time) + ":00";

    }

    public double getProgress() {
        return currentIndex;

    }

    public void setTime(int hour, int min, int sec) {
        if (isSeeking) {
            return;
        }
        int dtime = hour * 3600 + min * 60 + sec;
        currentIndex = 1.0f * scaleLong * dtime / DAY_TIME_MILLES;
        mNextX = currentIndex + PANDDING - getWidth() / 2.0f;
        postInvalidate();
    }

    double tempIndexEnd = 0;

    public void drawFildRect(Canvas canvas, TVideoFile tvideFile) {
        int stime = tvideFile.shour * 3600 + tvideFile.sminute * 60
                + tvideFile.ssecond;
        int etime = tvideFile.ehour * 3600 + tvideFile.eminute * 60
                + tvideFile.esecond;
        // int stime = 9 * 3600 + 35 * 60 + 40;
        // int etime = 12 * 3600 + 35 * 60 + 50;
        double sIndex = 1.0f * scaleLong * stime / DAY_TIME_MILLES;
        double eIndex = 1.0f * scaleLong * etime / DAY_TIME_MILLES;
        // canvas.drawRect((float) sIndex, txtPaintHeight + PANDDING_TOP,
        // (float) eIndex, txtPaintHeight + PANDDING_TOP + 100,
        // fileFildPaint);
        sIndex = PANDDING - mNextX + sIndex;
        eIndex = PANDDING - mNextX + eIndex;
        //拉宽两个录像文件显示的间隔，间隔太小的，+2像素，避免录像文件显示的区域线条乱闪
        if (sIndex != tempIndexEnd && Math.abs(tempIndexEnd - sIndex) < 5)
            sIndex += 3;
        tempIndexEnd = eIndex;
//        int w = (int) Math.abs(eIndex - sIndex);
//        if (w <= 6) {
//            sIndex = sIndex - (6 - w) / 2.0f;
//            eIndex = sIndex + (6 - w) / 2.0f;
//        }

        //   Log.d("PlayTimeFile", "tvideFile.start=" + (int) Math.ceil(sIndex) + ",end=" + (int) Math.ceil(eIndex));
        Rect rect = new Rect((int) Math.ceil(sIndex), txtPaintHeight + txtDateHeight + 25,
                (int) Math.ceil(eIndex), getHeight() - 2);
        if (tvideFile.nFileType == 2) {
            canvas.drawRect(rect, fileFildRedPaint);
        } else {
            canvas.drawRect(rect, fileFildGreenPaint);
        }
    }

    public void setTimeArea(List<TVideoFile> data) {
        this.data = data;
        postInvalidate();
    }

    private long tempTime = 0;// 记录上一次设置时间长度
    private String hms = "";
    private int count;
    private long fTime;

    private long beforeTime = 0;

    public void setTime(long l) {
        if (l == 0) {
            return;
        }
        if (isSeeking) {
            return;
        }
        if (isSeeked) {
            beforeTime = l;
            isSeeked = false;
            seekToTime(currentIndex, scaleLong);
            return;
        }
        //拖动之后防止时间刻度弹回拖动前时刻
        if (beforeTime != 0 && Math.abs(l - beforeTime) < 5) {
            return;
        } else {
            beforeTime = 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(l * 1000));
        String sdate = sdf.format(new Date(l * 1000));

        int dtime = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
//        int dtime = 0;
        if (sdate.contains(SPLITS)) {
            date = sdate.split(SPLITS)[0];
            hms = sdate.split(SPLITS)[1];
        }
        currentIndex = 1.0f * scaleLong * dtime / DAY_TIME_MILLES;
        mNextX = currentIndex + PANDDING - getWidth() / 2.0f;
        postInvalidate();

    }

    /**
     * 拖动到某个时间点
     *
     * @param currentIndex 当前进度条位置
     * @param scaleLong    进度条的总长度
     */
    public void seekToTime(double currentIndex, double scaleLong) {
        if (currentIndex < 0) {
            currentIndex = 0;
        }

        double dtime = DAY_TIME_MILLES * currentIndex / scaleLong;

        // 计算时间点的小时、分钟和秒
        int hour = (int) (dtime / 3600);
        int min = (int) (dtime % 3600 / 60);
        int sec = (int) (dtime % 3600 % 60);

        // 设置当前时间点信息
        TDateTime currentDateTime = new TDateTime();
        currentDateTime.iYear = tPlayBeginTime.iYear;
        currentDateTime.iMonth = tPlayBeginTime.iMonth;
        currentDateTime.iDay = tPlayBeginTime.iDay;
        currentDateTime.iHour = hour;
        currentDateTime.iMinute = min;
        currentDateTime.iSecond = sec;
        int startTime = tPlayBeginTime.iHour * 3600 + tPlayBeginTime.iMinute
                * 60 + tPlayBeginTime.iSecond;
        Log.d("PlayTimeFile", "currentIndex:" + currentIndex + ",scaleLong:"
                + scaleLong + ",startTime:" + startTime + ",dtime:" + dtime);
        // 拖动时间
        int seektime = (int) (dtime - startTime);

        // 将目标时间转换为毫秒
        Calendar c = Calendar.getInstance();
        boolean isOutTime = false;
        long currentTime = System.currentTimeMillis();
        try {
            c.setTime(new SimpleDateFormat("yyyyMMddHHmmss")
                    .parse(currentDateTime.iYear
                            + String.format("%02d", currentDateTime.iMonth)
                            + String.format("%02d", currentDateTime.iDay)
                            + String.format("%02d", currentDateTime.iHour)
                            + String.format("%02d", currentDateTime.iMinute)
                            + "00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentTime > c.getTimeInMillis()) {
            isOutTime = false;
        } else {
            isOutTime = true;
        }

        // Show.toast(context, "所选时间：" + hour + ":" + min + ":" + sec);
        if (!isOutTime && seektime >= 0) {

            // playCore.PlayTimeFile(tPlayBeginTime, tPlayEndTime);
            if (playCore != null)
                playCore.SeekFilePos(seektime, 0);
        } else {
            if (currentTime < c.getTimeInMillis()) {
                Show.toast(context, R.string.start_bes_low_current);
            } else if (seektime < 0) {
                // /Show.toast(context, R.string.seektime_below_start);
            }
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
    }

    public String getTime() {

        double dtime = DAY_TIME_MILLES * currentIndex / scaleLong;
        int hour = (int) (dtime / 3600);
        int min = (int) (dtime % 3600 / 60);
        int sec = (int) (dtime % 3600 % 60);
        // Log.i("mNextX", "当前位置：" + currentIndex + ",滑动位置：" + mNextX);
        if (timeListener != null) {
            timeListener.setTime(hour, min, sec);
            timeListener.setTime((int) dtime);
        }
        String stime = (hour > 9 ? String.valueOf(hour) : "0" + hour) + ":"
                + (min > 9 ? String.valueOf(min) : "0" + min) + ":"
                + (sec > 9 ? String.valueOf(sec) : "0" + sec);

        return stime;
    }

    public int computeScaleLong() {
        int scaleLong = 0;
        for (int i = 0; i < SCALE_TOTAL + 1; i++) {

            if (i % HOUR_SCALES == 0) {

                scaleLong = scaleLong + bmpLongScale.getWidth();
            } else {
                scaleLong = scaleLong + bmpShortScale.getWidth();
            }

            scaleLong = scaleLong + MARGIN;
        }
        // Log.i("mNextX", "长度：" + (scaleLong - MARGIN));
        return scaleLong - MARGIN - bmpLongScale.getWidth();

    }

    /**
     * 计算文本宽高
     *
     * @param paint
     * @param format
     * @return
     */
    public Rect computeTextScale(Paint paint, String format) {
        Rect rect = new Rect();
        paint.getTextBounds(format, 0, format.length(), rect);
        return rect;
    }

    // public synchronized void scrollTo(int x) {
    // mScroller.startScroll(mNextX, 0, x - mNextX, 0);
    // requestLayout();
    // }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        handled |= mGesture.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            isSeeking = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            isSeeking = false;
        }
        return handled;
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                              float velocityY) {
        synchronized (SeekTimeBar.this) {
            // mNextX += (int) velocityX;
            // currentIndex = mNextX - PANDDING + getWidth() / 2; // 当前 指向的位置
            // mScroller.fling((int) mNextX, 0, (int) -(velocityX), 0, 0, mMaxX,
            // 0, 0);
            Log.d("onFling", "onFling:" + velocityX);
        }
        postInvalidate();

        return false;
    }

    protected boolean onDown(MotionEvent e) {
        if (isDoubleClick()) {
            Log.i("isDoubleClick", "检测到双击~~");
            if (isSmallscaleMode) {
                isSmallscaleMode = false;
                MARGIN = LONG_MARGIN;
            } else {
                isSmallscaleMode = true;
                MARGIN = SHORT_MARGIN;
            }
            scaleLong = computeScaleLong();
            postInvalidate();
        }
        mScroller.forceFinished(true);
        return true;
    }

    /**
     * 判断双击
     */
    public boolean isDoubleClick() {

        if (count == 0) {
            ++count;
            fTime = System.currentTimeMillis();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    count = 0;
                }
            }, 250);
        } else if (count == 1) {
            if (System.currentTimeMillis() - fTime < 250) {

                return true;
            } else {
                count = 0;
            }
        }

        return false;
    }

    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {

            return SeekTimeBar.this.onDown(e);

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            return SeekTimeBar.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // if (isOutTime(currentIndex, tPlayBeginTime, tPlayEndTime)) {
            // return true;
            // }
            synchronized (SeekTimeBar.this) {

                mNextX += distanceX;
                currentIndex = mNextX - PANDDING + getWidth() / 2.0f; // 当前 指向的位置
                if (currentIndex < 0) {
                    currentIndex = 0;
                }
                hms = getTime();
            }
            if (Math.abs(distanceX) > 5) {
                isSeeked = true;
            }
            postInvalidate();

            return true;
        }

    };

    /**
     * 是否超出结束时间，少于开始时间
     *
     * @param currentIndex
     * @param tStartDateTime
     * @param tEndDateTime
     * @return
     */
    boolean isOutTime(double currentIndex, TDateTime tStartDateTime,
                      TDateTime tEndDateTime) {
        double dtime = DAY_TIME_MILLES * currentIndex / scaleLong;

        int startTime = tStartDateTime.iHour * 3600 + tStartDateTime.iMinute
                * 60 + tStartDateTime.iSecond;
        int endTime = tEndDateTime.iHour * 3600 + tEndDateTime.iMinute * 60
                + tEndDateTime.iSecond;
        if (startTime <= dtime && endTime >= dtime) {
            return false;
        } else {

            return true;
        }

    }

    public interface OnTimeListener {
        public abstract void setTime(int secondMilles);

        public abstract void setTime(int hour, int min, int sec);
    }

}
