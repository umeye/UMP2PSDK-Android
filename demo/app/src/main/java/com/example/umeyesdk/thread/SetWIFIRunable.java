package com.example.umeyesdk.thread;

import android.os.Handler;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevWifiInfor;

class SetWIFIRunable extends Thread {
	public static final int SET_WIFI_SUCCESS = 2;
	public static final int SET_WIFI_FAILED = 3;
	String apSsid;
	String apPwd;
	String devAddress;
	int devPort;
	String devPWD;
	PlayerClient playerclient;
	Handler handler;

	public SetWIFIRunable(String apSsid, String apPwd, String devAddress,
			String devPWD, int devPort, PlayerClient playerclient,
			Handler handler) {
		this.devAddress = devAddress;
		this.devPort = devPort;
		this.apSsid = apSsid;
		this.apPwd = apPwd;
		this.devPWD = devPWD;
		this.playerclient = playerclient;
		this.handler = handler;
	}

	@Override
	public void run() {
		TDevWifiInfor wifiInfo = new TDevWifiInfor();
		wifiInfo.bEnable = 1;
		wifiInfo.bDhcpEnable = 1;
		wifiInfo.bFieldEnable_AuthType = 0;
		wifiInfo.sWifiSSID = apSsid;
		wifiInfo.sWifiPwd = apPwd;
		if (playerclient.CameraSetWIFIConfig(devAddress, 34567, 2060, "admin",
				devPWD, wifiInfo) > 0) {
			handler.sendEmptyMessage(SET_WIFI_SUCCESS);
		} else {
			handler.sendEmptyMessage(SET_WIFI_FAILED);
		}

	}

}
