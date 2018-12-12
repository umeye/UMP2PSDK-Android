package com.example.umeyesdk.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevIpInfo;

public class GetIpConfigThread extends Thread {

	public static final int GET_FAILED = 2;
	public static final int GET_SUCCEES = 3;

	String ssid = "";
	String umid = "";
	String userName = "";
	String password = "";
	PlayerClient playerclient;
	Handler handler;
	private TDevIpInfo devIpInfo;

	public GetIpConfigThread(String umid, String userName, String password,
			PlayerClient playerclient, Handler handler) {
		this.umid = umid;
		this.userName = userName;
		this.password = password;
		this.playerclient = playerclient;
		this.handler = handler;
	}

	@Override
	public void run() {
		devIpInfo = playerclient.CameraGetIpConfig(umid, userName, password);
		if (devIpInfo != null) {

			Log.d("devWifiInfo", devIpInfo.toString());

			handler.sendMessage(Message.obtain(handler, GET_SUCCEES, devIpInfo));
		} else {
			handler.sendEmptyMessage(GET_FAILED);
		}
	}

}
