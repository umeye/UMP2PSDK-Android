package com.example.umeyesdk.utils;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
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
	private float MAX_SCALE = 10f;
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
	public static final int PHOTOHRAPH = 1;// 
	public static final int COPHOTOHRAPH = 2;//
	public static final int PHOTOZOOM = 3; //
	public static final int COPHOTOZOOM = 4; 
	public static final int PHOTORESOULT = 5;//
	public static final int ASPECTX = 1;
	public static final int ASPECTY = 1;
	public static final int OUTX = 324;
	public static final int OUTY = 240;
	public static final int COOUTX = 72;
	public static final int COOUTY = 54;
	public static final float seed = 30f;
	private static final float min = 0.75f;
	private static final float mout = 1.25f;

	private long beginTime = 0;
	private long endTime = 0;
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
	private float currentScale = 1f;

	private Uri m_uri = null;



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

	public void choose(final int flag, final Activity activity,
			final UriCallback callback) {
		if (flag != 0) {
			new AlertDialog.Builder(activity)
					.setTitle("选择你想上传的照")
					.setItems(
							new String[] { "相机拍摄", "手机相册" },
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										ContentValues values = new ContentValues();
										;
										Uri photoUri = activity
												.getContentResolver()
												.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
														values);
										callback.Load(photoUri);
										m_uri = photoUri;
										Intent takephoto = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										takephoto
												.putExtra(
														MediaStore.EXTRA_OUTPUT,
														photoUri);
										activity.startActivityForResult(
												takephoto, requestCode1);
										break;
									case 1:
										Intent i = new Intent(
												Intent.ACTION_PICK,
												MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
										activity.startActivityForResult(i,
												requestCode2);
										break;
									default:
										break;
									}
								}
							}).show();
		}
	}

	public float scale = 1;

	public boolean set(View v, MotionEvent event, ViewPager pager) {
		img = (ImageView) v;
		if (img == null) {
			return true;
		}
		boolean ret = false;
		Log.i("js671", "v" + v.getClass().toString());
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			beginTime = System.currentTimeMillis();
			savedMatrix.set(matrix);

			start.set(event.getX(), event.getY());
			Log.d(TAG, "mode=DRAG");
			mode = DRAG;
			// h.sendEmptyMessage(0x12);
			// ret = setmaxtrix();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if(pager != null) {
				pager.requestDisallowInterceptTouchEvent(false);
			}

			endTime = System.currentTimeMillis();

			if (mode != NONE) {
				center(true, true, true, img);
			}
			mode = NONE;
			ret = setmaxtrix();
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			center(true, true, true, img);
			Log.d(TAG, "mode=NONE");

			ret = setmaxtrix();
			break;

		case MotionEvent.ACTION_POINTER_DOWN:

			if (event.getPointerCount() > 1) {
				img.setScaleType(ImageView.ScaleType.MATRIX);
			}


			oldDist = spacing(event);

			if (oldDist > 10f) {
				midPoint(mid, event);
				if (scale == 1) {

					Drawable drf = img.getDrawable();
					if (drf != null) {
						Bitmap rf = ((BitmapDrawable) drf).getBitmap();
						if (rf != null) {
							matrix.set(savedMatrix);
							float s1 = v.getWidth() / (float) rf.getWidth();
							float s2 = v.getHeight() / (float) rf.getHeight();
							// float sc =(s2- s1) / s1;
							Log.d(TAG, "scale1=" + s1 + ",scale2=" + s2
									+ ",getWidth:" + v.getWidth()
									+ ",getHeight:" + v.getWidth()
									+ ",rf getWidth:" + rf.getWidth()
									+ ",rf getHeight:" + rf.getHeight());
							matrix = new Matrix();
							matrix.postScale(s1, s2);
						}
					}



				}

				savedMatrix.set(matrix);
				mode = ZOOM;
			}
			ret = setmaxtrix();
			// h.sendEmptyMessage(0x12);
			break;

		case MotionEvent.ACTION_MOVE:
			if(pager != null) {
				if (Math.abs(event.getX() - start.x) > v.getWidth() * 1.0 / 6 && System.currentTimeMillis() - beginTime < 100) {
					if (!isScale()) {
						pager.requestDisallowInterceptTouchEvent(false);
					} else {
						pager.requestDisallowInterceptTouchEvent(false);
					}
				} else {
					if (!isScale()) {
						pager.requestDisallowInterceptTouchEvent(false);
					} else {
						pager.requestDisallowInterceptTouchEvent(true);
					}
				}
			}

			RectF rectF = getRect(img);
			if (rectF == null) {
				return true;
			}

			if (mode == DRAG) {
				if (scale > 1) {
					int w = img.getWidth();
					int h = img.getHeight();
					Log.d("position", "rectF:" + rectF.toString()
							+ ",getWidth:" + w + ",getHeight:" + h);
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



					float tscale = newDist / oldDist;
					// currentScale = currentScale + scale;

					tscale = CheckView(tscale);
					matrix.postScale(tscale, tscale, mid.x, mid.y);
					// Log.w("currentScale", "currentScale:"+currentScale);

				}

			}
			ret = setmaxtrix();
			break;
		}

		return ret;
	}






	boolean setmaxtrix() {
		img.setImageMatrix(matrix);
//		CheckView();

		Drawable drf = img.getDrawable();
		if (drf != null) {
			Bitmap rf = ((BitmapDrawable) drf).getBitmap();
			if (rf != null) {
				float s1 = img.getWidth() / (float) rf.getWidth();
				scale = getScaleValues()[0] / s1;
			}
		}

		if (scale < 1.0f) {
			scale = 1.0f;
			return true;
		}
		return false;
	}




	public boolean isScale(){
		return scale > 1;
	}


	public boolean resetImageAndMatrix() {
		img.setScaleType(ImageView.ScaleType.FIT_XY);
		Drawable drf = img.getDrawable();
		if (drf != null) {
			Bitmap rf = ((BitmapDrawable) drf).getBitmap();
			if (rf != null) {
				float s1 = img.getWidth() / (float) rf.getWidth();
				float s2 = img.getHeight() / (float) rf.getHeight();
				matrix = new Matrix();
				matrix.postScale(s1, s2);

				savedMatrix.set(matrix);
				setmaxtrix();
			}
		}
		scale = 1.0f;

		return true;
	}



	public Drawable getDrawable() {
		return drawable;
	}


	private float[] getScaleValues() {
		float p[] = new float[9];
		matrix.getValues(p);
		return p;
	}

	private float CheckView(float scale) {
		float p[] = getScaleValues();
		if (mode == ZOOM) {
			Drawable drf = img.getDrawable();
			if (drf != null) {
				Bitmap rf = ((BitmapDrawable) drf).getBitmap();
				if (rf != null) {
					float s1 = img.getWidth() / (float) rf.getWidth();
					float s2 = img.getHeight() / (float) rf.getHeight();

					if (p[Matrix.MSCALE_X] * scale < minScaleR * s1 || p[Matrix.MSCALE_Y] * scale < minScaleR * s2) {
						return minScaleR * s1 / p[Matrix.MSCALE_X];
				    }

					if (p[Matrix.MSCALE_X] * scale > MAX_SCALE * s1 || p[Matrix.MSCALE_Y] * scale > MAX_SCALE * s2) {
						return MAX_SCALE * s1 / p[Matrix.MSCALE_X];
					}

				}
			}
		}
		return scale;
	}




	/**
	 * 根据URL加载图片
	 * 
	 * @param imageUrl
	 *            图片路径
	 * @return Drawable对象
	 */
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
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	private float spacing(float x, float y) {
		return (float) Math.sqrt(x * x + y * y);
	}

	public boolean decide() {
		boolean flag = false;
		return flag;
	}

	public void center(boolean horizontal, boolean vertical, boolean fl,
			ImageView img) {
		this.img = img;
		RectF rect = getRect(img);
		if (rect == null) {
			return;
		}
		float height = get(img)[0];
		float width = get(img)[1];
		float deltaX = 0, deltaY = 0;
		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下放留空则�??���??
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
		h.sendMessageDelayed(msg2, 5);
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
		frm.setMessage("请稍�??");
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

	public void bitmaptoDraw(Bitmap bmp) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 - 100)压缩文件
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

	public RectF getoRect(ImageView v) {

		Drawable drawable = v.getDrawable();
		if (drawable == null) {
			return null;
		}
		Rect rect1 = drawable.getBounds();
		RectF rect = new RectF(0, 0, rect1.width(), rect1.height());
		return rect;
	}

	public interface UriCallback {
		public void Load(Uri uri);
	}

}