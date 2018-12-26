package com.example.umeyesdk.thread;

import android.os.Handler;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevIpInfo;

public class SetIpConfigThread extends Thread {
	public static final int SET_OK = 0;
	public static final int SET_FALL = 1;
	PlayerClient pc;
	String umid = "";
	String userName = "";
	String password = "";
	TDevIpInfo devIpInfo;
	Handler handler;

	// public WifiSetThread(PlayerClient pc, String deviceId,
	// TDevWifiInfor devWifiInfo, Handler handler) {
	// this.pc = pc;
	// //this.deviceId = deviceId;
	// this.devWifiInfo = devWifiInfo;
	// this.handler = handler;
	// }

	public SetIpConfigThread(PlayerClient pc, String umid, String userName,
			String password, TDevIpInfo devIpInfo, Handler handler) {
		this.pc = pc;
		this.umid = umid;
		this.userName = userName;
		this.password = password;
		this.devIpInfo = devIpInfo;
		this.handler = handler;
	}

	@Override
	public void run() {
		Log.d("setWifiInfo", devIpInfo.toString());
		int ret = 0;
		ret = pc.CameraSetIpConfig(umid, userName, password, devIpInfo);
		if (ret > 0) {
			handler.sendEmptyMessage(SET_OK);
		} else
			handler.sendEmptyMessage(SET_FALL);

	}
}
