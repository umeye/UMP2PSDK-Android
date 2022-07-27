//package com.example.umeyesdk;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import com.Player.Core.PlayGlCore;
//import com.Player.Core.Utils.Imagedeal;
//import com.Player.Source.SDKError;
//import com.SPWipet.Jni.GLViewFragment;
//
//import androidx.fragment.app.FragmentActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//public class AcVrPlay extends FragmentActivity implements OnTouchListener,
//		OnClickListener {
//	// 云台控制命令
//	public static final byte MD_STOP = 0; // 停止
//	public static final byte MD_LEFT = 11; // 左
//	public static final byte MD_RIGHT = 12; // 右
//	public static final byte MD_UP = 9; // 上
//	public static final byte MD_DOWN = 10; // 下
//	public static final byte ACTION_ZOOMADD = 6; // 拉近
//	public static final byte ACTION_ZOOMReduce = 5;// 拉远
//	public static final byte ACTION_FOCUSADD = 7;// 焦距+
//	public static final byte ACTION_FOCUSReduce = 8;// 焦距减
//	public static final byte ACTION_Circle_Add = 13; // 光圈+
//	public static final byte ACTION_Circle_Reduce = 14;// 光圈-
//
//	public static final byte SHOW_STATE = 0;
//	private PlayGlCore pc;
//	private String id = "";
//	private FrameLayout parentView;
//	private TextView txtState, txtRec;
//	private boolean isStopCloudCommand = false;
//	private ImageButton btnUp, btnDown, btnLeft, btnRight, btnZoomIn,
//			btnZoomOut, btnNear, btnFar, btnCircleAdd, btnCircleReduce,
//			btnTalk, btnSound;
//	private Button btnPlay, btnPause, btnSnap, btnVideo;
//	private boolean isRun = true;
//	private Imagedeal deal;
//	private Handler handler = new Handler() {
//
//		@SuppressLint("HandlerLeak")
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			if (msg.what == SHOW_STATE) {
//				txtState.setText(GetDescription(AcVrPlay.this, msg.arg1));
//				// 是否显示录像
//				txtRec.setVisibility(msg.arg2 == 1 ? View.VISIBLE : View.GONE);
//
//			}
//
//			super.handleMessage(msg);
//		}
//
//	};
//	private GLViewFragment mTopFragment;
//	private Handler mHandler = new Handler() {
//		@Override
//		public void dispatchMessage(Message msg) {
//			switch (msg.what) {
//				case 1: // BKMusic
//
//					break;
//				case 111:
//
//					break;
//				default:
//					break;
//			}
//		}// End dispatchMessage
//	};// End Handler;
//	private FragmentManager mFragmentMgr;
//	private Button button6, button1;
//	private Button button2;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		this.setContentView(R.layout.ac_vrplay);
//		initeView();
//
//		initeGlSurfaceView();
//
//	}
//
//	private void initeGlSurfaceView() {
//		// TODO Auto-generated method stub
//		mTopFragment = new GLViewFragment(AcVrPlay.this, mHandler);
//		mTopFragment.renderView.setOnMyTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				int _Action = event.getAction();
//				switch (_Action & MotionEvent.ACTION_MASK) //
//				{
//					case MotionEvent.ACTION_DOWN:
//						Log.d("MotionEvent", "MotionEvent.ACTION_DOWN");
//					case MotionEvent.ACTION_POINTER_DOWN:
//						Log.d("MotionEvent", "MotionEvent.ACTION_POINTER_DOWN");
//					case MotionEvent.ACTION_MOVE:
//						Log.d("MotionEvent", "MotionEvent.ACTION_MOVE");
//					case MotionEvent.ACTION_POINTER_UP:
//						Log.d("MotionEvent", "MotionEvent.ACTION_POINTER_UP");
//					case MotionEvent.ACTION_UP:
//						Log.d("MotionEvent", "MotionEvent.ACTION_UP");
//						break;
//
//					default:
//						break;
//				}
//				return false;
//			}
//		});
//		pc = new PlayGlCore(AcVrPlay.this);
//		id = getIntent().getStringExtra("id");
//		pc.glYuvDisplayMode = 1;
//		pc.isQueryDevInfo = true;
//		pc.InitParam(id, -1, mTopFragment.renderView);
//		// pc.setOnFrameListenter(new OnFrameListenter() {
//		//
//		// @Override
//		// public void onFrameDisplay(int w, int h, byte[] y, byte[] u,
//		// byte[] v) {
//		// // TODO Auto-generated method stub
//		// mTopFragment.renderView.dataInYUV(w, h, y, u, v);
//		// }
//		// });
//		mFragmentMgr = getSupportFragmentManager();
//		FragmentTransaction fragmentTransaction = mFragmentMgr
//				.beginTransaction();
//		fragmentTransaction.add(R.id.imgLive, mTopFragment);
//		fragmentTransaction.commit();
//
//	}
//
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//
//		pc.Play();
//		isRun = true;
//		new StateThread().start();
//
//		super.onResume();
//	}
//
//	void initeView() {
//		parentView = (FrameLayout) findViewById(R.id.imgLive);
//		txtState = (TextView) findViewById(R.id.txt_state);
//		txtRec = (TextView) findViewById(R.id.tvwRec);
//		btnPlay = (Button) findViewById(R.id.btnPlay);
//		btnPause = (Button) findViewById(R.id.btnPause);
//		btnSnap = (Button) findViewById(R.id.btnSnap);
//		btnVideo = (Button) findViewById(R.id.btnVideo);
//		button6 = (Button) findViewById(R.id.button6);
//		button1 = (Button) findViewById(R.id.button1);
//
//		button2 = (Button) findViewById(R.id.button2);
//		btnPlay.setOnClickListener(this);
//		btnPause.setOnClickListener(this);
//		btnSnap.setOnClickListener(this);
//		btnVideo.setOnClickListener(this);
//		button6.setOnClickListener(this);
//		button1.setOnClickListener(this);
//
//		button2.setOnClickListener(this);
//		// 云台控制按钮
//
//		btnUp = (ImageButton) findViewById(R.id.btnUp);
//		btnUp.setOnTouchListener(this);
//		btnUp.setBackgroundResource(R.drawable.btn_down_0);
//		btnDown = (ImageButton) findViewById(R.id.btnDown);
//		btnDown.setOnTouchListener(this);
//
//		btnLeft = (ImageButton) findViewById(R.id.btnLeft);
//		btnLeft.setOnTouchListener(this);
//
//		btnRight = (ImageButton) findViewById(R.id.btnRight);
//		btnRight.setOnTouchListener(this);
//	}
//
//	/**
//	 * 执行云台命令
//	 *
//	 * @param action
//	 *            按钮的上下
//	 * @param command
//	 *            云台命令
//	 */
//	private void ExcuteCommand(View btn, int bg0, int bg1, int action,
//							   byte command) {
//		if (action == MotionEvent.ACTION_DOWN) {
//			btn.setBackgroundResource(bg1);
//			isStopCloudCommand = false;
//			new ColudThread(command).start();
//
//		} else if (action == MotionEvent.ACTION_UP) {
//
//			btn.setBackgroundResource(bg0);
//			isStopCloudCommand = true;
//
//		}
//	}
//
//	class ColudThread extends Thread {
//		private byte cmd;
//
//		public ColudThread(byte cmd) {
//			this.cmd = cmd;
//		}
//
//		@Override
//		public void run() {
//			while (!isStopCloudCommand
//					&& pc.GetPlayerState() == SDKError.Statue_PLAYING) {
//
//				pc.SetPtz(cmd, 5);
//				System.out.println("发送云台命令：" + cmd);
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//				pc.SetPtz(MD_STOP, 0);
//			}
//			System.out.println("停止命令");
//		}
//	}
//
//	/**
//	 * 状态显示线程
//	 *
//	 * @author Simula
//	 *
//	 */
//	class StateThread extends Thread {
//
//		@Override
//		public void run() {
//
//			try {
//				while (isRun) {
//
//					Thread.sleep(500);
//					Message msg = new Message();
//					msg.what = SHOW_STATE;
//					msg.arg1 = pc.PlayCoreGetCameraPlayerState();
//					if (pc.GetIsSnapVideo()) {
//						msg.arg2 = 1;
//					}
//					Log.w("GetIsPPT", "GetIsPPT: " + pc.GetIsPPT());
//					handler.sendMessage(msg);
//
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//
//		}
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//			case R.id.btnPlay:
//				pc.Play();
//
//				break;
//			case R.id.btnPause:
//				new Thread() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						pc.Stop();
//						super.run();
//					}
//
//				}.start();
//
//				break;
//			case R.id.btnSnap:// 默认路径 //sdcard/snapShot
//				// 判断SDCard
//				// .......
//				// pc.SetAlbumPath(TempALBUM_PATH); 设置图片，视频保存路径
//				// pc.SetSnapPicture(SnapPicture, TempFilenamePrefix),截图，设置图片保存路径
//				pc.SetSnapPicture(true);
//
//				break;
//			case R.id.btnVideo:// 默认路径 //sdcard/snapShot
//				// 判断SDCard
//				// .......
//				// pc.SetVideoPath(TempVIDEO_PATH);
//				if (pc.GetIsSnapVideo()) {
//					pc.SetSnapVideo(false);
//				} else {
//					pc.SetSnapVideo(true);
//				}
//
//				break;
//			case R.id.button6:
//				mTopFragment.ChangeShowType(6);
//				break;
//			case R.id.button1:
//				mTopFragment.ChangeShowType(1);
//				break;
//			case R.id.button2:
//				setMediaStreamType();
//				break;
//			default:
//				break;
//		}
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		int action = event.getAction();
//
//		switch (v.getId()) {
//			case R.id.btnUp:
//				ExcuteCommand(btnUp, R.drawable.btn_up_0, R.drawable.btn_up_1,
//						action, MD_UP);
//				break;
//			case R.id.btnDown:
//				ExcuteCommand(btnDown, R.drawable.btn_down_0,
//						R.drawable.btn_down_1, action, MD_DOWN);
//				break;
//			case R.id.btnLeft:
//				ExcuteCommand(btnLeft, R.drawable.btn_left_0,
//						R.drawable.btn_left_1, action, MD_LEFT);
//				break;
//			case R.id.btnRight:
//				ExcuteCommand(btnRight, R.drawable.btn_right_0,
//						R.drawable.btn_right_1, action, MD_RIGHT);
//				break;
//			// case R.id.imgLive:
//			// if (action == MotionEvent.ACTION_DOWN) {
//			// img.setScaleType(ScaleType.MATRIX);
//			// deal= Imagedeal.getdeal(img);
//			//
//			// } else if (action == MotionEvent.ACTION_UP) {
//			// img.setScaleType(ScaleType.FIT_XY);
//			// }
//			// deal.set(v, event);
//			// break;
//			//
//		}
//		return true;
//
//	}
//
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		isRun = false;
//		if (pc != null) {
//			pc.Stop();
//		}
//
//		super.onPause();
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		// pc.Stop();
//		isRun = false;
//
//		if (pc != null) {
//			pc.Stop();
//		}
//
//		super.onDestroy();
//	}
//
//	// public static String GetDescription(Context con, int state) {
//	// String des = con.getString(R.string.error_code) + ":" + state;
//	// switch (state) {
//	// case SDKError.Statue_Ready:
//	// des = con.getString(R.string.ready);
//	// break;
//	// case SDKError.Statue_PLAYING:
//	// des = con.getString(R.string.playing);
//	// break;
//	// case SDKError.Statue_STOP:
//	// des = con.getString(R.string.stop);
//	// break;
//	// case SDKError.Statue_ConnectingServer:
//	// des = con.getString(R.string.connecting);
//	// break;
//	// case SDKError.Statue_ConnectingSucess:
//	// des = con.getString(R.string.connect_success);
//	// break;
//	// case SDKError.Statue_ConnectFail:
//	// des = con.getString(R.string.connect_fail);
//	// break;
//	// case SDKError.NET_LOGIN_ERROR_PASSWORD:
//	// des = con.getString(R.string.passworderro);
//	// break;
//	// case SDKError.NET_LOGIN_ERROR_TIMEOUT:
//	// des = con.getString(R.string.time_out);
//	// break;
//	// case SDKError.NET_LOGIN_ERROR_USER:
//	// des = con.getString(R.string.usererro);
//	// break;
//	//
//	// case SDKError.Unknow:
//	// des = con.getString(R.string.unknown_error);
//	// break;
//	// case SDKError.NET_ERROR:
//	// des = con.getString(R.string.net_error);
//	// break;
//	// case SDKError.NET_NODATA_ERROR:
//	// des = con.getString(R.string.no_data);
//	//
//	// case SDKError.Exception_ERROR:
//	// des = con.getString(R.string.exception_error);
//	// break;
//	// case SDKError.NosupportDevice_ERROR:
//	// des = con.getString(R.string.unsupport_device);
//	// break;
//	// case SDKError.Beyondmaxchannels_ERROR:
//	// des = con.getString(R.string.max_channel);
//	// break;
//	// }
//	// return des;
//	// }
//	public String GetDescription(Context con, int state) {
//		Log.i("GetDescription", "GetDescription:" + state);
//		String des = con.getString(R.string.connect_fail);
//		switch (state) {
//			case 0:
//				des = con.getString(R.string.ready);
//				break;
//			case 1:
//				des = con.getString(R.string.connecting);
//				break;
//			case 2:
//				des = con.getString(R.string.playing);
//				break;
//			case 3:
//				des = con.getString(R.string.connect_fail);
//				break;
//			case 4:
//				des = con.getString(R.string.stop);
//				break;
//			case 10:
//				des = con.getString(R.string.buffering);
//				break;
//			case 7:
//				des = con.getString(R.string.stop);
//				break;
//			case SDKError.NPC_D_MPI_MON_ERROR_USERID_ERROR:
//				des = con.getString(R.string.usererro);
//				break;
//			case SDKError.NPC_D_MPI_MON_ERROR_USERPWD_ERROR:
//				des = con.getString(R.string.passworderro);
//				break;
//			case SDKError.NPC_D_MPI_MON_ERROR_REJECT_ACCESS:
//				des = con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);
//				break;
//			case -112:
//				des = con.getString(R.string.NPC_D_MPI_MON_ERROR_CAMERA_OFFLINE);
//				break;
//		}
//		return des;
//
//	}
//
//	public int getStreamType() {
//		int ret = 1;
//		if (pc != null) {
//			if (pc.tDevNodeInfor != null) {
//				return pc.tDevNodeInfor.streamtype;
//			}
//
//		}
//		return ret;
//	}
//
//	boolean isSetStream = false;
//
//	public int CameraSwitchChannel() {
//		// TODO Auto-generated method stub
//		int ret = -1;
//		if (isPlayed()) {
//			int stream = getStreamType();
//			if (stream == 1) {
//				stream = 0;
//			} else {
//				stream = 1;
//			}
//			Log.d("CameraSwitchChannel", "CameraSwitchChannel:" + stream);
//			ret = pc.CameraSwitchChannel(stream);
//		}
//		return ret;
//	}
//
//	public void setMediaStreamType() {
//		if (isPlayed()) {
//			if (!isSetStream) {
//				new Thread() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						isSetStream = true;
//						CameraSwitchChannel();
//						isSetStream = false;
//						super.run();
//					}
//				}.start();
//			}
//
//		}
//
//	}
//
//	public boolean isPlayed() {
//
//		return pc != null && !TextUtils.isEmpty(pc.DeviceNo) ? (pc
//				.PlayCoreGetCameraPlayerState() == 2) : false;
//	}
//}
