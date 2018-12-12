package com.example.umeyesdk.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevWifiInfor;

public class WifiGetThread extends Thread {

	public static final int GET_FAILED = 2;
	public static final int GET_SUCCEES = 3;

	String ssid = "";
	String umid = "";
	String userName = "";
	String password = "";
	PlayerClient playerclient;
	Handler handler;
	private TDevWifiInfor devWifiInfo;

	public WifiGetThread(String umid, String userName, String password,
			PlayerClient playerclient, Handler handler) {
		this.umid = umid;
		this.userName = userName;
		this.password = password;
		this.playerclient = playerclient;
		this.handler = handler;
	}

	@Override
	public void run() {
		devWifiInfo = playerclient.CameraGetWIFIConfigEx(umid, userName,
				password);

		if (devWifiInfo != null) {
			Log.d("devWifiInfo", "devWifiInfo.sWifiSSID:"
					+ devWifiInfo.sWifiSSID + ",devWifiInfo.bDhcpEnable:"
					+ devWifiInfo.bDhcpEnable + ",devWifiInfo.sWifiPwd:"
					+ devWifiInfo.sWifiPwd + ",devWifiInfo.sWifiSSID:"
					+ devWifiInfo.sWifiSSID + ",devWifiInfo.sGateway:"
					+ devWifiInfo.sGateway + "bIfSetNetParam"
					+ devWifiInfo.bIfSetNetParam);
			handler.sendMessage(Message.obtain(handler, GET_SUCCEES,
					devWifiInfo));
		} else {
			handler.sendEmptyMessage(GET_FAILED);
		}
	}

}