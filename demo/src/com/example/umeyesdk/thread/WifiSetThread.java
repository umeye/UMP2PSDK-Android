package com.example.umeyesdk.thread;

import android.os.Handler;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevWifiInfor;

/**
 * 设置前，先获取一下wifi配置
 * 
 * @author Administrator
 * 
 */
public class WifiSetThread extends Thread {
	public static final int SET_OK = 0;
	public static final int SET_FALL = 1;
	PlayerClient pc;
	String umid = "";
	String userName = "";
	String password = "";
	TDevWifiInfor devWifiInfo;
	Handler handler;

	public WifiSetThread(PlayerClient pc, String umid, String userName,
			String password, TDevWifiInfor devWifiInfo, Handler handler) {
		this.pc = pc;
		this.umid = umid;
		this.userName = userName;
		this.password = password;
		this.devWifiInfo = devWifiInfo;
		this.handler = handler;
	}

	@Override
	public void run() {

		TDevWifiInfor getdevWifiInfo = pc.CameraGetWIFIConfigEx(umid, userName,
				password);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (getdevWifiInfo != null) {
			// 设置wifi是否开启
			getdevWifiInfo.bEnable = devWifiInfo.bEnable;
			// 设置DHCP功能
			getdevWifiInfo.bDhcpEnable = devWifiInfo.bDhcpEnable;
			// // 字段使能：安全类型
			getdevWifiInfo.bFieldEnable_AuthType = 0;
			// 字段使能：加密类型
			getdevWifiInfo.bFieldEnable_EncrypType = 0;
			getdevWifiInfo.bFieldEnable_Channel = 0;
			// 是否设置网络参数（必填），TRUE：设置，以下参数有效，FALSE：不设置，保持原网络参数
			// 要使DHCP，IP地址生效，必须设置bIfSetNetParam=1.
			getdevWifiInfo.bIfSetNetParam = 1;
			// 连接路由ssid
			getdevWifiInfo.sWifiPwd = devWifiInfo.sWifiPwd;
			// 连接路由密码
			getdevWifiInfo.sWifiSSID = devWifiInfo.sWifiSSID;
			Log.d("setWifiInfo",
					"设备ID：" + umid + ",wifi SSID:" + getdevWifiInfo.sWifiSSID
							+ ", " + getdevWifiInfo.toString());
			int ret = pc.CameraSetWIFIConfigEx(umid, userName, password,
					getdevWifiInfo);

			if (ret > 0) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(SET_OK);
			} else
				handler.sendEmptyMessage(SET_FALL);

		} else {
			handler.sendEmptyMessage(SET_FALL);
		}
		pc.RtsCameraDisconnect();
	}
}
