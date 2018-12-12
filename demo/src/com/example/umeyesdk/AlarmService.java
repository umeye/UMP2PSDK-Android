package com.example.umeyesdk;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.Player.Core.AlarmServerClient;
import com.Player.Source.TAlarmPushInfor;
import com.example.umeyesdk.entity.MessageInfo;
import com.example.umeyesdk.utils.Constants;

public class AlarmService extends Service {
	public static int HaveData = 0;
	String ServerAddr = "";
	int ServerPort = 0;
	String userName = "";
	AlarmServerClient alarServerClient;
	private boolean isRun = false;
	private static final String SETTING_PATH = "/sdcard/NewUMEye/setting.dat";// 报警设置目录
	NotificationManager nm;
	MediaPlayer mediaPlayerAlarm;
	int i = 0;
	long checkTime = 0;

	@SuppressLint("HandlerLeak")
	public Handler hander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == HaveData) {

				MessageInfo message = (MessageInfo) msg.obj;
				String devName = message.getName();
				String notifyMessage = message.getMessage();
				vibrateAndSound(devName, notifyMessage, message);
			}
		}
	};

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub

		super.onCreate();
	}

	/**
	 * 获取imsi
	 * 
	 * @param con
	 * @return
	 */
	public static String getImsi(Context con) {

		TelephonyManager mTelephonyMgr = (TelephonyManager) con
				.getSystemService(Context.TELEPHONY_SERVICE);
		String secureId = android.provider.Settings.Secure.getString(
				con.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		String subscriberId = mTelephonyMgr.getSubscriberId();
		String deviceId = mTelephonyMgr.getDeviceId();

		// Log.w("imsi", "SubscriberId:" + subscriberId);
		Log.w("imsi", "secureId:" + secureId);
		// Log.w("imsi", "DeviceId:" + deviceId);
		return secureId;

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		String imsi = getImsi(this);
		alarServerClient = AlarmServerClient.getInstanse();
		boolean startRet = alarServerClient.startAlarmServer(Constants.server,
				Constants.port, Constants.Login_user, 3, imsi, "umeye",
				"SimpChinese", Constants.custom_flag);
		if (startRet) {
			notifytitle();
			isRun = true;
			new Thread() {
				@Override
				public void run() {
					int serverState = 0;
					int lastServerState = 0;
					while (isRun) {
						restartServer(serverState, lastServerState);
						TAlarmPushInfor tAlarmPushInfor = alarServerClient
								.getAlarmServerMsg();

						if (tAlarmPushInfor != null) {

							Log.w("tAlarmPushInfor",
									"" + tAlarmPushInfor.toString());
							MessageInfo message = new MessageInfo(
									AlarmService.this, tAlarmPushInfor);
							send(message);
							hander.sendMessage(Message.obtain(hander, HaveData,
									message));
						}

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}.start();

			// }

		}

		// }
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	public void restartServer(int serverState, int lastServerState) {
		if (System.currentTimeMillis() - checkTime > 10000) {// 状态判断
			// 服务器断开连接
			// 重启服务
			serverState = alarServerClient.queryAlarmServerState();
			checkTime = System.currentTimeMillis();
			if (serverState != lastServerState) {
				if (lastServerState == 2
						&& (serverState == 1 || serverState == 0)) {
					stopForeground(true);
					isRun = false;
					alarServerClient.stopAlarmServer();
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startService(new Intent(AlarmService.this,
							AlarmService.class));
				}

				lastServerState = serverState;
			}
		}
	}

	public void notifytitle() {
		Notification notification = new Notification(R.drawable.ic_launcher,
				getText(R.string.app_name), System.currentTimeMillis());
		// if (startRet) {
		//
		// }
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.flags = Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(this, "报警服务", "报警服务正在后台运行",
				pendingIntent);
		startForeground(50, notification);
	}

	public void send(MessageInfo msg) {
		Intent intent = new Intent("android.intent.action.MY_BROADCAST");
		intent.putExtra("msg", msg);
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 消息通知
	 * 
	 * @param devName
	 * @param strJson
	 * @param message
	 */
	public void vibrateAndSound(String devName, String strJson,
			MessageInfo message) {

		myNotify(devName, strJson, message);
		Vibrate(AlarmService.this, 1000);

		try {
			if (mediaPlayerAlarm != null) {
				mediaPlayerAlarm.start();
			} else {
				mediaPlayerAlarm = MediaPlayer.create(AlarmService.this,
						R.raw.alarm);
				mediaPlayerAlarm.setLooping(false);
				mediaPlayerAlarm.start();
			}
			// mediaPlayerAlarm.prepareAsync();

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void myNotify(String devName, String strJson, MessageInfo message) {
		if (nm == null) {
			nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}

		Notification n = new Notification(R.drawable.ic_launcher, strJson,
				System.currentTimeMillis());

		n.flags |= Notification.FLAG_SHOW_LIGHTS;
		n.ledARGB = 0xffffff00;

		n.ledOnMS = 300;
		n.ledOffMS = 1000;

		n.flags |= Notification.FLAG_AUTO_CANCEL;
		// Intent intent = null;
		// // if (playClientIsAlive) {
		//
		// intent = new Intent(AlarmService.this, AcMain.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_NEW_TASK);

		// } else {
		// intent = new Intent(PushService.this, AcLogin.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_NEW_TASK);
		// }

		// i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(
				AlarmService.this, 1, new Intent(),
				PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(AlarmService.this, "", devName + ":" + strJson,
				contentIntent); // 之前为strJson+
		// System.currentTimeMillis()
		i++;
		nm.notify(i, n);

	}

	public void Vibrate(Service service, long milliseconds) {
		Vibrator vib = (Vibrator) service
				.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		stopForeground(true);
		isRun = false;
		alarServerClient.stopAlarmServer();
		Log.w("AlarmService", " AlarmService onDestroy() ");
		// startService(new Intent(AlarmService.this, AlarmService.class));
		super.onDestroy();
	}
}
