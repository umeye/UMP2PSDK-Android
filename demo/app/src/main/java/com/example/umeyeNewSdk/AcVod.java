package com.example.umeyeNewSdk;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.Player.Core.PlayerCore;
import com.Player.Source.SDKError;
import com.Player.Source.TVideoFile;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;

/**
 * 录像回放播放界面
 * */
public class AcVod extends Activity {
	private final int SHOW_STATE = 0;// 显示状态
	private final int GO_FORWARD = 1;
	private final int GO_BACKWARD = 0;
	private final int GO_OFFSET = 30;// 000;//30快进后退都30秒

	private RelativeLayout rlTitle;
	private RelativeLayout rlBottom;

	private Button btnPlay, btnPause, btnStop, btnForward, btnBackward,
			btnBack;
	private TextView tvState, tvTitle, tvProgress;
	private ImageView imgVod;
	private SeekBar sbProgress;
	private MyHandler handler;
	private PlayerCore player;// 播放器
	private boolean isShowInfo = true;// 判定是否显示信息
	private String totalTime;
	private int fileTime = 0;
	private int lastStart, lastEnd;
	private int currentpos = 0;
	boolean isSeekingTime = false;;
	int stopCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_vod);

		InitView();

		player = new PlayerCore(this);
		player.InitParam("", -1, imgVod);
		handler = new MyHandler();

		new ShowStateInfoThread().start();
		Play();
	}

	public void InitView() {
		rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);
		rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(Constants.UMID + "");
		imgVod = (ImageView) findViewById(R.id.imgVod);
		tvState = (TextView) findViewById(R.id.tvState);
		tvProgress = (TextView) findViewById(R.id.tvProgress);
		// 底部控制按钮
		OnControlClick onCtontrolClick = new OnControlClick();
		btnPlay = (Button) findViewById(R.id.btnPlay);
		btnPlay.setOnClickListener(onCtontrolClick);

		btnPause = (Button) findViewById(R.id.btnPause);
		btnPause.setOnClickListener(onCtontrolClick);

		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(onCtontrolClick);

		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(onCtontrolClick);

		btnForward = (Button) findViewById(R.id.btnForward);
		btnForward.setOnClickListener(onCtontrolClick);

		btnBackward = (Button) findViewById(R.id.btnBackward);
		btnBackward.setOnClickListener(onCtontrolClick);

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(onCtontrolClick);

		fileTime = GetLen(AcSearchRecordResult.VideoFile);

		sbProgress = (SeekBar) findViewById(R.id.sbProgress);
		sbProgress.setOnSeekBarChangeListener(new OnSeekBarChange());
		sbProgress.setMax(fileTime);
		// if (fileTime < 10000)
		// sbProgress.setVisibility(View.INVISIBLE);

	}

	public int GetLen(TVideoFile t) {
		int day = (t.eday - t.sday) * 24 * 3600;
		int hour = (t.ehour - t.shour) * 3600;
		int minute = (t.eminute - t.sminute) * 60;
		int second = (t.esecond - t.ssecond);
		return (day + hour + minute + second) * 1000;
	}

	public class OnControlClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btnPlay:
					Play();
					break;
				case R.id.btnPause:
					Pause();
					break;
				case R.id.btnStop: {
					currentpos = 0;
					Stop();
				}
				break;
				case R.id.btnForward:
					Forward(GO_OFFSET);
					break;
				case R.id.btnBackward:
					Backward(GO_OFFSET);
					break;
				case R.id.btnBack:
					finish();
					break;
			}
		}

	}

	public class OnSeekBarChange implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
									  boolean fromUser) {
			// System.out.println("onProgressChanged");
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			System.out.println("onStartTrackingTouch");
			isSeekingTime = false;
			lastStart = seekBar.getProgress();
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

			isSeekingTime = true;
			lastEnd = seekBar.getProgress();
			System.out.println("onStopTrackingTouch:"+lastEnd);
			if (lastEnd > lastStart) {
				Forward(seekBar.getProgress());
			} else {
				Backward(seekBar.getProgress());
			}
		}

	}

	public void Pause() {
		// 当已经暂停时，按钮不起作用
		if (player.GetPlayerState() == SDKError.Statue_Pause)
			return;
		if (player.GetPlayerState() == SDKError.Statue_STOP)
			return;
		player.Pause();
		btnPlay.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.GONE);
	}

	public void Play() {
		// 当正在播放时，按钮不起作用
		if (player.GetPlayerState() == SDKError.Statue_PLAYING
				|| player.GetPlayerState() == SDKError.Statue_WaitBuffer)
			return;

		if (player.GetPlayerState() == SDKError.Statue_Pause) {
			player.Resume();
			btnPlay.setVisibility(View.GONE);
			btnPause.setVisibility(View.VISIBLE);
			return;
		}
		// 仅且当停止、就绪、连接失败才可播放
		if (player.GetPlayerState() == SDKError.Statue_STOP
				|| player.GetPlayerState() == SDKError.Statue_Ready
				|| player.GetPlayerState() == SDKError.Statue_ConnectFail
				|| player.GetPlayerState() == SDKError.NET_NODATA_ERROR) {
			btnPlay.setVisibility(View.GONE);
			btnPause.setVisibility(View.VISIBLE);

			// boolean ret = player.Play(AcSearchRecordResult.VideoFile);
			boolean ret = player.PlayP2P(Constants.UMID, Constants.user,
					Constants.password, 0, AcSearchRecordResult.VideoFile);
			stopCount=0;
//			if (ret && currentpos > 0)
//				player.SeekFilePos(currentpos, 0);
			System.out.println("-------------播放-----------------");
		}

	}

	public void Backward(int offset) {
		if (player.GetPlayerState() != SDKError.Statue_PLAYING)
			return;// 只有播放时才能后退
		int seekTime = offset / 1000;// player.GetCurrentPlayTime_Int()-offset;
		if (seekTime < 0) {
			seekTime = 0;
		}
		player.SeekFilePos(seekTime, GO_BACKWARD);
		System.out.println("-------------后退-----------------" + seekTime);
	}

	public void Forward(int offset) {
		if (player.GetPlayerState() != SDKError.Statue_PLAYING)
			return;// 只有播放时才能前进
		int seekTime = offset / 1000;// player.GetCurrentPlayTime_Int()+offset;
		if (seekTime > player.GetFileAllTime_Int()) {
			seekTime = player.GetFileAllTime_Int();
		}
		player.SeekFilePos(seekTime, GO_FORWARD);
		System.out.println("-------------前进-----------------" + seekTime);
	}

	public void Stop() {
		// if(player.GetPlayerState()==SDKError.Statue_STOP ||
		// player.GetPlayerState()==SDKError.Statue_WaitBuffer)
		// return;//如果状态已经是停止，则不执行
		player.Stop();
		player.IsPausing = false;
		btnPlay.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.GONE);
		System.out.println("-------------停止-----------------");
	}

	public void ShowStateInfo(String info, int state) {
		Message msg = Message.obtain();
		msg.arg1 = state;
		msg.obj = info + "  " + player.GetCurrentPlayTime() + "/"
				+ player.GetFileAllTime();
		msg.what = SHOW_STATE;
		handler.sendMessage(msg);
	}

	class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			if (msg.what == SHOW_STATE) {
				tvState.setText(msg.obj.toString());
				tvProgress.setText(player.GetCurrentPlayTime() + "/"
						+ player.GetFileAllTime());
				// 设置进度
				int current = player.GetCurrentPlayTime_Int();// 当前时间
				int total = player.GetFileAllTime_Int();//

				if (total > 10)
					totalTime = player.GetFileAllTime();

				if (current >= 0 && current <= total) {


					if (isSeekingTime) {

						if (current>lastEnd&&current-2000<lastEnd) {
							isSeekingTime = false;
						}
					} else {

						sbProgress.setProgress(current);

					}

				}
				if (player.GetPlayerState() == SDKError.Statue_STOP) {
					tvProgress.setText("00:00/" + totalTime);
					sbProgress.setProgress(0);
				}
				if (msg.arg1 == SDKError.Statue_ConnectFail
						|| msg.arg1 == SDKError.NET_NODATA_ERROR)// 如果发现连接失败或者没有数据，则使用重新连接
				{
					// Reconnect();
				}
				Log.w("totalandcurrent", "current is:" + current + " total is:"
						+ total);
				if (current != 0 && total != 0 && (current >= total - 100)
						&& (current <= total)||current>total)// 证明播放到尽头,停止播放
				{
					stopCount++;
					if (stopCount > 5) {
						Log.w("Stop", "播放完成！");
						stopCount = 0;
						Stop();
					}

				}
			}
		}
	}

	class ShowStateInfoThread extends Thread// 显示线程的状态
	{
		@Override
		public void run() {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (isShowInfo) {
				if (player != null) {
					int state = player.GetPlayerState();
					// System.out.println("状态码："+state);
					// String frameRate = player.GetFrameRate() + "FPS";
					String des = GetDescription(state);
					ShowStateInfo(des, state);
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String GetDescription(int state) {
		String des = getString(R.string.error_code) + ":" + state;
		switch (state) {
			case SDKError.Statue_Ready:
				des = getString(R.string.ready);
				break;
			case SDKError.Statue_PLAYING:
				des = getString(R.string.playing);
				break;
			case SDKError.Statue_Pause:
				des = getString(R.string.pause);
				break;
			case SDKError.Statue_STOP:
				des = getString(R.string.stop);
				break;
			case SDKError.Statue_ConnectingSucess:
				des = getString(R.string.connect_success);
				break;
			case SDKError.Statue_ConnectFail:
				des = getString(R.string.connect_fail);
				break;
			case SDKError.Statue_ConnectingServer:
				des = getString(R.string.connecting);
				break;
			case SDKError.Statue_WaitBuffer:
				des = getString(R.string.buffering);
				break;
			case SDKError.NET_NODATA_ERROR:
				des = getString(R.string.no_data);
			case SDKError.Unknow:
				des = getString(R.string.unknown_error);
				break;
			case SDKError.NET_Protocal_Error:
				des = getString(R.string.protocal_error);
				break;
			case SDKError.NET_ERROR:
				des = getString(R.string.net_error);
				break;
			case SDKError.Exception_ERROR:
				des = getString(R.string.exception_error);
				break;
			case SDKError.NosupportDevice_ERROR:
				des = getString(R.string.unsupport_device);
				break;
			case SDKError.Beyondmaxchannels_ERROR:
				des = getString(R.string.max_channel);
				break;
		}
		return des;
	}

	public void Reconnect() {
		if (player != null) {
			// Stop();
			// try//小范围休眠
			// {
			// Thread.sleep(100);
			// } catch (InterruptedException e)
			// {
			// e.printStackTrace();
			// }
			Play();
			System.out.println("重新连接");
			tvState.setText(R.string.reconnect);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)// 横
		{
			rlTitle.setVisibility(View.GONE);
			rlBottom.setVisibility(View.GONE);
			imgVod.setScaleType(ScaleType.FIT_XY);
		} else// 竖
		{
			rlTitle.setVisibility(View.VISIBLE);
			rlBottom.setVisibility(View.VISIBLE);
			imgVod.setScaleType(ScaleType.FIT_CENTER);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Stop();
		isShowInfo = false;
	}

}
