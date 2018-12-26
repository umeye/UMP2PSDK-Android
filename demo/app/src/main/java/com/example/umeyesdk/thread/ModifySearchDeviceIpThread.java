package com.example.umeyesdk.thread;

import android.os.Handler;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.example.umeyesdk.entity.SearchDeviceInfo;

public class ModifySearchDeviceIpThread extends Thread {
	public static final int MODIFY_SUCCESS = 10;
	public static final int MODIFY_FAILED = 11;
	SearchDeviceInfo node;
	Handler handler;
	String newIP;
	String newMask;
	String newGateWay;
	boolean isDHCP;
	PlayerClient playClient;

	public ModifySearchDeviceIpThread(PlayerClient playClient,
									  SearchDeviceInfo node, Handler handler, String newIP,
									  String newMask, String newGateWay, boolean isDHCP) {
		this.node = node;
		this.handler = handler;
		this.newIP = newIP;
		this.newMask = newMask;
		this.newGateWay = newGateWay;
		this.isDHCP = isDHCP;
		this.playClient = playClient;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean ret = ModifySearchDeviceIP(playClient, node, newIP,
				newMask, newGateWay, isDHCP);
		if (ret) {

			handler.sendEmptyMessage(MODIFY_SUCCESS);

		} else {
			handler.sendEmptyMessage(MODIFY_FAILED); // 后加
		}

	}

	public boolean ModifySearchDeviceIP(PlayerClient playerclient,
										SearchDeviceInfo node, String newIP, String newMask,
										String newGetWay, boolean isDhcp) {
		int ret = -1;
		if (isDhcp) {
			Log.w("dhcp", "自动获取Ip");
			return EnableDHCP(playerclient, node, isDhcp);
		} else {
			Log.w("dhcp", "重新设置Ip");

			if (EnableDHCP(playerclient, node, isDhcp)) {

				ret = playerclient.ModifyDevIpaddr(node.dwVendorId,
						node.sDevId, node.sAdapterMac_1, node.sIpaddr_1, newIP,
						newMask, newGetWay);

				return ret > 0;
			} else {
				return false;
			}
		}
	}

	/**
	 * 使用DHCP自动获取
	 */
	public  boolean EnableDHCP(PlayerClient playerclient,
							   SearchDeviceInfo node, boolean isEnable) {
		int ret = -1;

		ret = playerclient.EnableDevDhcp(node.dwVendorId, node.sDevId,
				node.sAdapterMac_1, node.sAdapterName_1, isEnable ? 1 : 0);

		return ret >= 0;

	}
}

