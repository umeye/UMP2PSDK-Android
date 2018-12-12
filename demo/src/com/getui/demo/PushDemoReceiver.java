package com.getui.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.Player.Source.LogOut;
import com.alibaba.fastjson.JSONObject;
import com.example.umeyesdk.AcLogo;
import com.example.umeyesdk.R;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class PushDemoReceiver extends BroadcastReceiver {
	NotificationManager nm;
	public static int notifyId = 0;
	private Intent update_intent = new Intent(
			"com.qq.xgdemo.activity.UPDATE_LISTVIEW");
	/**
	 * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView ==
	 * null)
	 */
	public static StringBuilder payloadData = new StringBuilder();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

		switch (bundle.getInt(PushConsts.CMD_ACTION)) {
		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			// smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(
					context, taskid, messageid, 90001);
			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

			if (payload != null) {
				String data = new String(payload);

				Log.d("GetuiSdkDemo", "receiver payload : " + data);
				if (!TextUtils.isEmpty(data)) {
					AlarmMessage alarmMessage = JSONObject.parseObject(data,
							AlarmMessage.class);

					if (alarmMessage != null && alarmMessage.aps != null
							&& alarmMessage.aps.alert != null
							&& !TextUtils.isEmpty(alarmMessage.aps.alert.body)) {
						context.sendBroadcast(update_intent);
						alarm(context, context.getString(R.string.app_name),
								alarmMessage);
					}

				}

				payloadData.append(data);
				payloadData.append("\n");

			}
			break;

		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			AlarmUtils.GETUI_CID = cid;
			Log.d("clientid", cid);
			break;
		case PushConsts.GET_SDKONLINESTATE:
			boolean online = bundle.getBoolean("onlineState");
			Log.d("GetuiSdkDemo", "online = " + online);
			break;

		case PushConsts.SET_TAG_RESULT:
			String sn = bundle.getString("sn");
			String code = bundle.getString("code");

			String text = "设置标签失败, 未知异常";
			switch (Integer.valueOf(code)) {
			case PushConsts.SETTAG_SUCCESS:
				text = "设置标签成功";
				break;

			case PushConsts.SETTAG_ERROR_COUNT:
				text = "设置标签失败, tag数量过大, 最大不能超过200个";
				break;

			case PushConsts.SETTAG_ERROR_FREQUENCY:
				text = "设置标签失败, 频率过快, 两次间隔应大于1s";
				break;

			case PushConsts.SETTAG_ERROR_REPEAT:
				text = "设置标签失败, 标签重复";
				break;

			case PushConsts.SETTAG_ERROR_UNBIND:
				text = "设置标签失败, 服务未初始化成功";
				break;

			case PushConsts.SETTAG_ERROR_EXCEPTION:
				text = "设置标签失败, 未知异常";
				break;

			case PushConsts.SETTAG_ERROR_NULL:
				text = "设置标签失败, tag 为空";
				break;

			case PushConsts.SETTAG_NOTONLINE:
				text = "还未登陆成功";
				break;

			case PushConsts.SETTAG_IN_BLACKLIST:
				text = "该应用已经在黑名单中,请联系售后支持!";
				break;

			case PushConsts.SETTAG_NUM_EXCEED:
				text = "已存 tag 超过限制";
				break;

			default:
				break;
			}

			Log.d("GetuiSdkDemo", "settag result sn = " + sn + ", code = "
					+ code);
			Log.d("GetuiSdkDemo", "settag result sn = " + text);
			break;
		case PushConsts.THIRDPART_FEEDBACK:
			/*
			 * String appid = bundle.getString("appid"); String taskid =
			 * bundle.getString("taskid"); String actionid =
			 * bundle.getString("actionid"); String result =
			 * bundle.getString("result"); long timestamp =
			 * bundle.getLong("timestamp");
			 * 
			 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo",
			 * "taskid = " + taskid); Log.d("GetuiSdkDemo", "actionid = " +
			 * actionid); Log.d("GetuiSdkDemo", "result = " + result);
			 * Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
			 */
			break;

		default:
			break;
		}
	}

	public void alarm(final Context context, final String dev,
			final AlarmMessage alarmMessage) {
		notifyId++;
		if (nm == null) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		notifyMessage(context, alarmMessage.aps.alert.title,
				alarmMessage.aps.alert.body, alarmMessage.AlarmTime);
		// notifyMessageByPic(context, alarmMessage);
		// "http://www2.autoimg.cn/newsdfs/g17/M04/0C/CC/autohomecar__wKgH2Fe-ZFaAYNOXAA0oo0xc1Yw979.jpg"

	}

	public void notifyMessage(Context context, String title, String message,
			String time) {
		Intent intent = new Intent(context, AcLogo.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.notification);
		views.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
		views.setTextViewText(R.id.title, title);
		views.setTextViewText(R.id.content, message);
		views.setTextViewText(R.id.time, time);
		Notification status = new Notification();
		//配置震动和声音
//		AlertOption alertOption = Utils
//				.readAlertOption(AlarmUtils.SETTING_PATH);
//		if (alertOption.ckwurao == 0) {
			status.defaults = Notification.DEFAULT_VIBRATE
					| Notification.DEFAULT_LIGHTS;
			//if (alertOption.ckVioce == 1) {
				status.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.alarm);
		//}
	//	}
		status.flags |= Notification.FLAG_AUTO_CANCEL;
		status.tickerText = message;
		status.contentView = views;
		status.icon = R.drawable.ic_launcher;
		status.contentIntent = pendingIntent;
		LogOut.d(context, "notifyId :" + notifyId);
		nm.notify(notifyId, status);
	}
//
//	public String toTitle(Context context, AlarmMessage alarmMessage) {
//
//		String titleStr = String.format(
//				context.getString(R.string.message_title),
//				alarmMessage.CameraName);
//
//		return titleStr;
//
//	}

	public String toMessage(Context con, AlarmMessage alarmMessage) {

		return alarmMessage.aps.alert.body;

	}

	private void Vibrate(Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Context.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
}
