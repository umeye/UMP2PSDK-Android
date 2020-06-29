package com.example.umeyesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.Player.Core.PlayerLocalFileCore;
import com.Player.Source.SDKError;
import com.Player.Source.TMp4FileInfo;

import java.util.Timer;
import java.util.TimerTask;

public class AcVideoPlayback extends Activity {

	private static final int PDSEEKING = 2;

	private ImageButton btnPlay;
	private TextView tvProgress, tvfps;
	private SurfaceView imgVod;
	private SeekBar sbProgress, fpsProgress;
	private RelativeLayout rlTitle;
	private PlayerLocalFileCore player;// 播放器
	private boolean IsinPlayerView = true;
	private boolean isShowInfo = true;// 判定是否显示信息
	private MyHandler handler;

	private int fileTime = 0;// 秒

	private  int playTimeSev = 0;// 播放时长

	private String fileName;
	private TMp4FileInfo tmpMp4FileInfo;

	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_videoplayback);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		fileName = getIntent().getStringExtra("fileName");

		InitView();


		handler = new MyHandler();
		new ShowStateInfoThread().start();
	}

	public void InitView() {

		imgVod = (SurfaceView) findViewById(R.id.imgLive);
		tvProgress = (TextView) findViewById(R.id.tvState);
		rlTitle = (RelativeLayout) findViewById(R.id.title_layout);
		// 底部控制按钮
		OnControlClick onCtontrolClick = new OnControlClick();
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(onCtontrolClick);

		sbProgress = (SeekBar) findViewById(R.id.sbProgressNew);
		fpsProgress = (SeekBar) findViewById(R.id.fpsProgress);
		tvfps = (TextView) findViewById(R.id.fpstextview);
		player = new PlayerLocalFileCore(this);
		// fileName="/sdcard/vMEyeIPC/video/20140202201007032.mp4";
		player.InitParam(fileName, imgVod);
		System.out.println("MP4 fileName is " + fileName);

		// player.Play();
		tmpMp4FileInfo = player.GetMp4FileInfo(fileName, Integer.parseInt(fileName.substring(fileName.lastIndexOf("fps")+3,fileName.lastIndexOf(".mp4"))));
		if (tmpMp4FileInfo != null) {
			System.out.println(player.GetCurrentPlayTime() + "信息:帧"
					+ tmpMp4FileInfo.fps + ",长度" + tmpMp4FileInfo.totaltime
					+ ",宽" + tmpMp4FileInfo.width + "x高"
					+ tmpMp4FileInfo.height);
		}
		Play();
		fileTime = tmpMp4FileInfo.totaltime / 1000;
		sbProgress.setMax(fileTime);
		fpsProgress.setMax(99);
		fpsProgress.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				int fps = arg0.getProgress() + 1;

				player.ControlMp4PlaySpeed(fps);

				int tmpfileTime = (int) ((float)player.getDuration()/1000);
				tvfps.setText(fps + "fps");

				playTimeSev = (int) ((float)player.getCurrentPosition()/1000);
				Log.d("playTimeSev", "playTimeSev " + playTimeSev);
				fileTime=tmpfileTime;
				sbProgress.setMax(fileTime);
				tvProgress.setText(getTotalTime(playTimeSev) + "/"
						+ getTotalTime(fileTime));

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}
		});

		sbProgress.setOnSeekBarChangeListener(new OnSeekBarChange());

	}



	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {

			super.handleMessage(msg);
			if(isFinishing()) {
				return;
			}
			int ret = msg.what;// GetPlayerState();
			// if (ret==SDKError.Statue_PLAYING) {
			// fpsProgress.setEnabled(false);
			// }else {
			// fpsProgress.setEnabled(true);
			// }

			if (ret == SDKError.Statue_ConnectFail) {
				Log.e("Reconect", "SDKError.Statue_ConnectFail");
				btnPlay.setImageResource(R.drawable.live_play_selector);
				player.Stop();
				// mPlayerCore.Player_Start(CurrentChannel, mImageView);
			} else if (ret == SDKError.NET_ERROR) {
				Log.e("Reconect", "SDKError.Exception_ERRO");
				player.Stop();
				// mPlayerCore.Player_Start(CurrentChannel, mImageView);
			} else if (ret == SDKError.NET_NODATA_ERROR) {
				Log.e("Reconect", "SDKError.NET_NODATA_ERROR");
				player.Stop();
				player.Play();
			} else if (msg.what == SDKError.Exception_ERROR) {
				Log.e("Reconect", "SDKError.Exception_ERROR");
				// mStatusBar.setText(R.string.networkerro);
				// mPlayerCore.Player_Stop();
				player.Play();
			} else if (msg.what == SDKError.Statue_RecordFileOver)// 录像结束
			{
				Log.e("Reconect", "SDKError.Statue_RecordFileOver");
				player.Stop();
			} else if (msg.what == SDKError.Statue_Pause)// 播放暂停
			{
				Log.e("Reconect", "SDKError.Statue_PAUSE");
				btnPlay.setImageResource(R.drawable.live_play_selector);
			} else if (msg.what == SDKError.Statue_PLAYING) {
				btnPlay.setImageResource(R.drawable.live_pause_selector);
				// 设置进度
				int current = playTimeSev;
				tvProgress.setText(getTotalTime(current) + "/"
						+ getTotalTime(fileTime));// +"/"+player.GetFileAllTime());
				sbProgress.setProgress(current);
				int total = fileTime;
				if (msg.arg1 == SDKError.Statue_ConnectFail
						|| msg.arg1 == SDKError.NET_NODATA_ERROR) {// 如果发现连接失败或者没有数据，则使用重新连接
					Reconnect();
				}
				if (current != 0 && total != 0 && (current >= total)) {// 证明播放到尽头,停止播放
					Stop();
				}
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);// 设置成有重力感应模式就是自由切换横竖屏模式
			} else if (ret == SDKError.NET_LOGIN_ERROR_PASSWORD) {
				openOptionsDialog(getResources().getString(
						R.string.passworderro));// "Connect server failed");
				player.Stop();
			} else if (ret == SDKError.NET_LOGIN_ERROR_USER) {
				openOptionsDialog(getResources().getString(R.string.usererro));
				player.Stop();
			} else if (ret == SDKError.NET_LOGIN_ERROR_TIMEOUT) {
				// mStatusBar.setText(R.string.loginfail);
				openOptionsDialog(getResources().getString(R.string.loginfail));
				player.Stop();
			} else if (ret == SDKError.NET_LOGIN_ERROR_LOCKED) {
				player.Stop();
			} else if (ret == SDKError.NET_LOGIN_ERROR_BUSY) {
				openOptionsDialog("Server is busying now!");
				player.Stop();
			} else if (ret == SDKError.Statue_STOP) {
				btnPlay.setImageResource(R.drawable.live_play_selector);
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				// sbProgress.setEnabled(false);
				// mIsPlaying = false;
			}
			if (ret != SDKError.Statue_PLAYING && ret != SDKError.Statue_Pause) {
				playTimeSev = 0;
				sbProgress.setProgress(playTimeSev);
			}

			if (player.IsPausing) {
				btnPlay.setImageResource(R.drawable.live_play_selector);
				return;
			}

		}
	}

	public void Reconnect() {
		if (player != null) {
			Stop();
			try// 小范围休眠
			{
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Play();
			System.out.println("重新连接");
		}
	}

	public void Pause() {
		// 当已经暂停时，按钮不起作用
		if (player.GetPlayerState() == SDKError.Statue_Pause)
			return;
		player.Pause();
		btnPlay.setImageResource(R.drawable.live_play_selector);
	}

	public void Play() {
		// 当正在播放时，按钮不起作用
		if (player.GetPlayerState() == SDKError.Statue_PLAYING)
			return;

		if (player.GetPlayerState() == SDKError.Statue_Pause) {
			player.Resume();
			btnPlay.setImageResource(R.drawable.live_pause_selector);
			return;
		}
		// 仅且当停止、就绪、连接失败才可播放
		if (player.GetPlayerState() != SDKError.Statue_PLAYING) {
			btnPlay.setImageResource(R.drawable.live_pause_selector);
			player.InitParam(fileName, imgVod);
			player.Play();
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (!player.IsPausing) {
						try {
							playTimeSev = (int) ((float)player.getCurrentPosition()/1000);
							Log.d("playTimeSev", "playTimeSev " + playTimeSev);
							System.out.println("playTimeSev：" + playTimeSev);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
	

				}
			}, 0, 1000);

		}
		// boolean ret = player.Play(CommonData.VideoFile, imgVod);
		// if(ret && currentpos>0)
		// player.
		// player.SeekFilePos(currentpos,0);

	}

	public void Stop() {
		if (player.GetPlayerState() == SDKError.Statue_STOP)
			return;// 如果状态已经是停止，则不执行
		player.Stop();
		tvProgress.setText(getTotalTime(0) + "/" + getTotalTime(fileTime));
		btnPlay.setImageResource(R.drawable.live_play_selector);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		System.out.println("-------------停止-----------------");
	}



	public void Release() {
		player.Release();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		System.out.println("-------------释放-----------------");
	}


	private void openOptionsDialog(String MessageTip) {
		new AlertDialog.Builder(this)
				.setMessage(MessageTip)
				.setPositiveButton(getResources().getString(R.string.positive),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	class ShowStateInfoThread extends Thread// 显示线程的状态
	{
		@Override
		public void run() {
			while (isShowInfo) {
				if (IsinPlayerView) {
					if (player != null) {
						// System.out.println("时间长度:"+player.AVCountTime+"  fps:"+player.GetFrameBitRate()+"当前时间："+player.GetCurrentPlayTime());
						try {
							int state = player.GetPlayerState();
							Message message = handler.obtainMessage();
							message.what = state;
							handler.sendMessage(message);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d("Changel", "横屏" + newConfig);
//			imgVod.setScaleType(ImageView.ScaleType.FIT_XY);
			player.changeVideoSize();
			rlTitle.setVisibility(View.GONE);
			findViewById(R.id.bottom_layout).setVisibility(View.GONE);
		} else {
			Log.d("Changel", "竖屏" + newConfig);
//			imgVod.setScaleType(ImageView.ScaleType.FIT_CENTER);\
			player.changeVideoSize();
			rlTitle.setVisibility(View.VISIBLE);
			findViewById(R.id.bottom_layout).setVisibility(View.VISIBLE);
		}

	}

	public class OnControlClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnPlay:
				if (player.GetPlayerState() == SDKError.Statue_STOP) {
					Play();
					break;
				}
				if (player.IsPausing) {
					Play();
				} else
					Pause();

				break;

			case R.id.back_btn:
				finish();
				break;
			// case R.id.btnSnap:
			// if (player.GetPlayerState() == SDKError.Statue_PLAYING) {
			// // 未装入SD卡异常
			// if (android.os.Environment.getExternalStorageState()
			// .equals(android.os.Environment.MEDIA_MOUNTED)) {
			// player.SetAlbumPath(Config.UserImageDir);
			// player.SetSnapPicture(true);
			//
			// Toast.makeText(
			// AcVideoPlayback.this,
			// getString((R.string.savetips))
			// + Config.UserImageDir
			// + getString(R.string.dirctory),
			// Toast.LENGTH_SHORT).show();
			// } else {
			// Toast.makeText(AcVideoPlayback.this,
			// R.string.sdcard_unavaible, Toast.LENGTH_SHORT)
			// .show();
			// }
			// } else {
			// Toast.makeText(AcVideoPlayback.this, R.string.nopictips,
			// Toast.LENGTH_SHORT).show();
			// }
			// break;
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Release();
		isShowInfo = false;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == PDSEEKING) {
			ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage(getString(R.string.loading));
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			return pd;
		}

		return super.onCreateDialog(id);
	}

	public class OnSeekBarChange implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			playTimeSev = seekBar.getProgress();
			tvProgress.setText(getTotalTime(playTimeSev) + "/"
					+ getTotalTime(fileTime));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			player.Pause();
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			playTimeSev = seekBar.getProgress();
			showDialog(PDSEEKING);
			Thread t = new Thread(SeekThread);
			t.start();
		}

	}

	// public class OnFpsSeekBarChange implements OnSeekBarChangeListener {
	//
	// @Override
	// public void onProgressChanged(SeekBar seekBar, int progress,
	// boolean fromUser) {
	// }
	//
	// @Override
	// public void onStartTrackingTouch(SeekBar seekBar) {
	// }
	//
	// @Override
	// public void onStopTrackingTouch(SeekBar seekBar) {
	// int selectFps = seekBar.getProgress() + 2;
	// fpstextview.setText(selectFps + " fps");
	// player.ControlMp4PlaySpeed(selectFps);
	// // Thread t = new Thread(SeekThread);
	// // t.start();
	// }
	//
	// }

	public String getTotalTime(int fileTime) {

		long hour, min, sec;
		hour = fileTime / 3600;
		min = (fileTime % 3600) / 60;
		sec = fileTime % 60;
		return getStartTime((int) hour, (int) min, (int) sec);
	}

	public String getStartTime(int hour, int min, int sec) {
		String startHour = String.valueOf(hour);
		String startMin = String.valueOf(min);
		String startSec = String.valueOf(sec);
		if (hour < 10)
			startHour = "0" + startHour;
		if (min < 10)
			startMin = "0" + startMin;
		if (sec < 10)
			startSec = "0" + sec;

		if (hour <= 0) {
			startHour = "00";
		}
		if (min <= 0) {
			startMin = "00";
		}
		if (sec <= 0) {
			startSec = "00";
		}
		return startHour + ":" + startMin + ":" + startSec;
	}

	private Runnable SeekThread = new Runnable() {
		@Override
		public void run() {

			try {
				player.SetCurrentPlayTime(playTimeSev * 1000);
				System.out.println(playTimeSev + "+" + player.GetCurrentPlayTime());
				player.SeekFilePos(playTimeSev);
				player.Resume();
			} catch (Exception e) {
				e.printStackTrace();
			}
			SeekHandler.sendEmptyMessage(PDSEEKING);
		};
	};
	@SuppressLint("HandlerLeak")
	private Handler SeekHandler = new Handler() {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			dismissDialog(PDSEEKING);
			super.handleMessage(msg);
		}
	};






}
