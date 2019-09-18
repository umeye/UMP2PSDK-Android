package com.getui.demo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.Player.Source.TDevNodeInfor;
import com.Player.web.request.P2pConnectInfo;
import com.Player.web.response.ResponseCommon;
import com.Player.web.websocket.ClientCore;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.Show;
import com.igexin.sdk.PushManager;

/**
 * 报警工具类
 *
 * @author Administrator
 *
 */
public class AlarmUtils {
	public static final String SETTING_PATH = "/sdcard/UMEyeDome/setting.dat";// 报警设置目录

	public static boolean isRecived = true;
	/**
	 * 个推ClientID PushDemoReceiver 里面赋值
	 */
	public static String GETUI_CID = "";

	public static void openPush(final Context context) {
		// XGPushConfig.enableDebug(this, true);

		PushManager.getInstance().initialize(context, DemoPushService.class);
		PushManager.getInstance().registerPushIntentService(context.getApplicationContext(),DemoIntentService.class);

	}

	/**
	 * 布防，撤防设置
	 *
	 * @param opCode
	 *            为1时布防 为2时撤防 为4撤销所有设备布防
	 */
	public static void setAlarmPush(final Context context,
									ClientCore clientCore, PlayNode node, final int opCode) {
		// 报警类型
		int[] alarm_event = { 1, 2, 3, 4, 5 };
		// 免登陆模式、umid直连模式下 ，需设置设备通道参数 ， 支持多个通道设置，可选
		P2pConnectInfo p2pConnectInfo = createConnectInfo(clientCore, node);
		P2pConnectInfo[] p2pConnectInfos = { p2pConnectInfo };
		clientCore.alarmSettings(p2pConnectInfos);//免登陆需提供设备参数
		// AlarmUtils.GETUI_CID为推送token
		clientCore.alarmSettings(opCode, AlarmUtils.GETUI_CID, alarm_event,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						ResponseCommon commonSocketText = (ResponseCommon) msg.obj;
						if (commonSocketText != null
								&& commonSocketText.h.e == 200) {
							if (opCode == 1) {
								Show.toast(context, "布防成功");
							} else if (opCode == 2) {
								Show.toast(context, "撤防成功");
							}

						} else {
							if (opCode == 1) {
								Show.toast(context, "布防失败");
							} else if (opCode == 2) {
								Show.toast(context, "撤防失败");
							}

						}
					}
				}, node.node.sDevId);
	}

	/**
	 * 免登录报警 设备通道参数
	 *
	 * @param clientCore
	 * @param node
	 * @return
	 */
	public static P2pConnectInfo createConnectInfo(ClientCore clientCore,
												   PlayNode node) {
		P2pConnectInfo p2pConnectInfo = new P2pConnectInfo();
		if (node != null) {
			// 解析连接参数
			TDevNodeInfor info = TDevNodeInfor.changeToTDevNodeInfor(
					node.getDeviceId(), node.node.iConnMode);
			if (info != null) {
				p2pConnectInfo = new P2pConnectInfo();
				p2pConnectInfo.umid = info.pDevId;
				p2pConnectInfo.user = info.pDevUser;
				p2pConnectInfo.passwd = info.pDevPwd;
				p2pConnectInfo.dev_name = node.getName();

				/**
				 * 免登陆模式下 node.node.sDevId是 String sDevId
				 * =clientCore.encryptDevId
				 * (String.valueOf(node.node.dwNodeId),info.pDevId, info.iChNo);
				 */
				String sDevId = node.node.sDevId;
				p2pConnectInfo.dev_id = sDevId;
				p2pConnectInfo.channel = info.iChNo;
			}
		}
		return p2pConnectInfo;

	}

	/**
	 * umid直连模式 布防，撤防设置
	 *
	 *
	 * @param activity
	 * @param opCode
	 *            为1时布防 为2时撤防 为4撤销所有设备布防
	 * @param devName
	 *            推送通知显示 设备名称
	 * @param devUmid
	 *            设备umid
	 * @param devUser
	 *            设备用户名
	 * @param devPassword
	 *            设备密码
	 * @param iChNo
	 *            设备通道号
	 */
	public static void setAlarmPush(final Context activity, final int opCode,
									String devName, String devUmid, String devUser, String devPassword,
									int iChNo) {
		ClientCore clientCore = ClientCore.getInstance();
		// 报警类型
		int[] alarm_event = { 1, 2, 3, 4, 5 };
		// 免登陆模式下、 umid直连模式，需设置设备通道参数 ， 支持多个通道设置，可选
		P2pConnectInfo p2pConnectInfo = createConnectInfo1(clientCore, devName,
				devUmid, devUser, devPassword, iChNo);
		P2pConnectInfo[] p2pConnectInfos = { p2pConnectInfo };
		clientCore.alarmSettings(p2pConnectInfos);
		// AlarmUtils.GETUI_CID为推送token
		clientCore.alarmSettings(opCode, AlarmUtils.GETUI_CID, alarm_event,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						ResponseCommon commonSocketText = (ResponseCommon) msg.obj;
						if (commonSocketText != null
								&& commonSocketText.h.e == 200) {
							if (opCode == 1) {
								Show.toast(activity, "布防成功");
							} else if (opCode == 2) {
								Show.toast(activity, "撤防成功");
							}

						} else {
							if (opCode == 1) {
								Show.toast(activity, "布防失败");
							} else if (opCode == 2) {
								Show.toast(activity, "撤防失败");
							}

						}
					}
				}, "");
	}

	/**
	 * umid直连 设备通道参数
	 *
	 * @param clientCore
	 * @return
	 */
	public static P2pConnectInfo createConnectInfo1(ClientCore clientCore,
													String devName, String devUmid, String devUser, String devPassword,
													int iChNo) {
		P2pConnectInfo p2pConnectInfo = new P2pConnectInfo();

		p2pConnectInfo = new P2pConnectInfo();
		p2pConnectInfo.umid = devUmid;
		p2pConnectInfo.user = devUser;
		p2pConnectInfo.passwd = devPassword;
		// 推送设备名称由umid 和通道号组成，可自定义
		p2pConnectInfo.dev_name = devName;
		String sDevId = clientCore.encryptDevId("", devUmid, iChNo);
		p2pConnectInfo.dev_id = sDevId;
		p2pConnectInfo.channel = iChNo;
		return p2pConnectInfo;

	}
}
