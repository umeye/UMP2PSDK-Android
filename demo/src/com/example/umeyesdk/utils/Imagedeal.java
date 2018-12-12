package com.example.umeyesdk.utils;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 
 * @author
 * 
 */
public class Imagedeal {

	private static Imagedeal imagedeal = null;
	private static final String TAG = "ImageViewerActivity";
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private float MAX_SCALE = 4f;
	private float minScaleR = 1f;

	// These matrices will be used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Matrix tempMatrix = new Matrix();

	// We can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	public static final int PHOTOHRAPH = 1;
	public static final int COPHOTOHRAPH = 2;
	public static final int PHOTOZOOM = 3; 
	public static final int COPHOTOZOOM = 4; 
	public static final int PHOTORESOULT = 5;
	public static final int ASPECTX = 1;
	public static final int ASPECTY = 1;
	public static final int OUTX = 324;
	public static final int OUTY = 240;
	public static final int COOUTX = 72;
	public static final int COOUTY = 54;
	public static final float seed = 10f;
	private static final float min = 0.75f;
	private static final float mout = 1.25f;

	// private long beginTime = 0;
	// private long endTime = 0;
	// Remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1.5f;
	private Drawable drawable;
	private ImageView img;
	// private DisplayMetrics dm;
	private float ratio = 1f;
	private float ratio1 = 1f;
	public boolean fin = false;
	int requestCode1 = 1;
	int requestCode2 = 2;
	// private float currentScale = 1f;

	private Uri m_uri = null;
	@SuppressLint("HandlerLeak")
	Handler h = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0x11) {
				matrix.postTranslate(msg.getData().getFloat("deltax"), msg
						.getData().getFloat("deltay"));
				img.setImageMatrix(matrix);
				img.invalidate();
				if (Math.abs(msg.getData().getFloat("deltax")) == ratio * seed
						&& Math.abs(msg.getData().getFloat("deltay")) == ratio1
								* seed) {
					setMess(con(msg.getData().getFloat("sum") - seed, msg
							.getData().getFloat("deltax")),
							con(msg.getData().getFloat("sum") - seed, msg
									.getData().getFloat("deltay")), msg);
				}
			} else if (msg.what == 0x12) {
				h.removeMessages(0x11);
			}
		}
	};

	public static Imagedeal getdeal(ImageView img) {
		if (imagedeal == null) {
			imagedeal = new Imagedeal();
		}
		imagedeal.center(true, true, false, img);
		return imagedeal;
	}

	public Uri getUri() {
		return m_uri;
	}


	public void set(View v, MotionEvent event) {
		img = (ImageView) v;
		if (img == null) {
			return;
		}
		Log.i("js671", "v  " + v.getClass().toString());
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// beginTime = System.currentTimeMillis();
			savedMatrix.set(matrix);

			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			h.sendEmptyMessage(0x12);
			break;

		case MotionEvent.ACTION_UP:
			// endTime = System.currentTimeMillis();

			if (mode != NONE) {
				center(true, true, true, img);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			center(true, true, true, img);
			Log.d(TAG, "mode=NONE");
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			// Log.d(TAG, "oldDist=" + oldDist);
			if (oldDist > 10f) {

				savedMatrix.set(matrix);
				tempMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			h.sendEmptyMessage(0x12);
			break;

		case MotionEvent.ACTION_MOVE:
			RectF rectF = getRect(img);

			if (mode == DRAG) {
				if (rectF.bottom > 50 && rectF.top < img.getHeight() - 50
						&& rectF.left < img.getWidth() - 50 && rectF.right > 50) {
					matrix.set(savedMatrix);
					// if(Math.abs(start.x-event.getX())<(width*3/4)){
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				}

				// }
			} else if (mode == ZOOM) {

				float newDist = spacing(event);

				// Log.d(TAG, "newDist=" + newDist);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					// currentScale = currentScale + scale;

					matrix.postScale(scale, scale, mid.x, mid.y);
					// Log.w("currentScale", "currentScale:"+currentScale);
				}

			}

			break;
		}
		img.setImageMatrix(matrix);
		CheckView();
	}

	public Drawable getDrawable() {
		return drawable;
	}

	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {
			if (p[0] < minScaleR) {
				matrix.setScale(minScaleR, minScaleR);
			}
			if (p[0] > MAX_SCALE) {
				matrix.set(savedMatrix);
			}
		}
	}


	@SuppressWarnings("unused")
	private Drawable loadImageFromUrl(String imageUrl) {
		try {
			return Drawable.createFromStream(new URL(imageUrl).openStream(),
					"src");
		} catch (Exception e) {

		}
		return null;
	}

	private void midPoint(PointF mid, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		mid.set(x / 2, y / 2);
	}

	/**
	 * @param event
	 * @return
	 */
	@SuppressLint("FloatMath")
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	public boolean decide() {
		boolean flag = false;
		return flag;
	}

	public void center(boolean horizontal, boolean vertical, boolean fl,
			ImageView img) {
		this.img = img;
		RectF rect = getRect(img);
		if (rect==null) {
			return;
		}
		float height = get(img)[0];
		float width = get(img)[1];
		float deltaX = 0, deltaY = 0;
		if (vertical) {

			int screenHeight = img.getHeight();
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = screenHeight - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = img.getWidth();
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		if (fl == true) {
			Bundle bun = new Bundle();
			ratio = 1f;
			ratio1 = 1f;
			if (deltaX == 0) {
				ratio = 0;
			} else {
				ratio = (float) Math.cos(Math.atan2(Math.abs(deltaY),
						Math.abs(deltaX)));
			}
			ratio1 = (float) Math.sin(Math.atan2(Math.abs(deltaY),
					Math.abs(deltaX)));
			float sum = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
			bun.putFloat("deltax", ratio * con(sum, deltaX));
			bun.putFloat("deltay", ratio1 * con(sum, deltaY));
			bun.putFloat("sum", sum);
			Message mm = new Message();
			mm.setData(bun);
			mm.what = 0x11;
			h.sendMessage(mm);
		} else {
			matrix.postTranslate(deltaX, deltaY);
			img.setImageMatrix(matrix);
		}
	}

	public void setMess(float x, float y, Message msg) {
		Message msg2 = new Message();
		Bundle bun1 = new Bundle();
		bun1.putFloat("deltax", ratio * x);
		bun1.putFloat("deltay", ratio1 * y);
		bun1.putFloat("sum", msg.getData().getFloat("sum") - seed);
		msg2.setData(bun1);
		msg2.what = msg.what;
		h.sendMessageDelayed(msg2, 20);
	}

	public float con(float f1, float f2) {
		float temp = 1f;
		if (f2 > 0) {
			temp = 1f;
		} else if (f2 == 0) {
			temp = 0f;
		} else {
			temp = -1f;
		}
		if (f1 < seed) {
			return temp * f1;
		} else {
			return temp * seed;
		}

	}

	public void trun(int id, ImageView v, Context context, boolean flag) {
		float smin = 1f;
		if (flag == true) {
			if (get(v)[0] > get(v)[1]) {
				smin = v.getHeight() / get(v)[0];
			} else {
				smin = v.getWidth() / get(v)[1];
			}
		} else {
			smin = min;
		}
		img = v;
		ProgressDialog frm = new ProgressDialog(context);
		frm.show();
		switch (id) {
		case 0:
			circle(v, 90);
			break;
		case 1:
			circle(v, -90);
			break;
		case 2:
			bs(smin, smin);
			break;
		case 3:
			bs(mout, mout);
			break;
		case 4:
			circle(v, 360);
			break;
		}
		frm.dismiss();
		center(true, true, false, v);

	}

	public void circle(ImageView v, float degrees) {
		matrix.postRotate(degrees);
	}

	public void bs(float x, float y) {
		matrix.postScale(x, y);
	}

	@SuppressWarnings("deprecation")
	public void bitmaptoDraw(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);//
		drawable = null;
		drawable = new BitmapDrawable(bmp);
	}

	public void stop() {
		if (imagedeal != null) {
			imagedeal = null;
		}
	}

	public interface Imageset {
		public void image(Drawable drawable, Bitmap myBitmap);
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

	public interface UriCallback {
		public void Load(Uri uri);
	}

}
