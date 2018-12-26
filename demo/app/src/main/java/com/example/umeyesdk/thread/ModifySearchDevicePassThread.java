package com.example.umeyesdk.thread;

import android.os.Handler;

import com.Player.Core.PlayerClient;
import com.example.umeyesdk.entity.SearchDeviceInfo;

public class ModifySearchDevicePassThread extends Thread {

	SearchDeviceInfo node;
	Handler handler;
	String oldPass;
	String newPass;
	PlayerClient playClient;

	public ModifySearchDevicePassThread(PlayerClient playClient,
										SearchDeviceInfo node, Handler handler, String oldPass,
										String newPass) {
		this.node = node;
		this.handler = handler;
		this.oldPass = oldPass;
		this.newPass = newPass;
		this.playClient = playClient;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			boolean ret = ModifySearchDevicePass(playClient, node, oldPass,
					newPass);
			if (ret) {
				handler.sendEmptyMessage(ModifySearchDeviceIpThread.MODIFY_SUCCESS);
			} else {
				handler.sendEmptyMessage(ModifySearchDeviceIpThread.MODIFY_FAILED);
			}
		} catch (Exception e) {
			handler.sendEmptyMessage(ModifySearchDeviceIpThread.MODIFY_FAILED);
		}

	}

	/**
	 * 修改搜索设备的密码
	 */
	public static boolean ModifySearchDevicePass(PlayerClient playerclient,
												 SearchDeviceInfo node, String oldPass, String newPass) {
		int ret = -1;

		ret = playerclient.ModifyDevPwd(node.dwVendorId, node.sDevId,
				node.sAdapterMac_1, oldPass, newPass);

		return ret > 0;

	}
}
