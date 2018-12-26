package com.example.umeyesdk.thread;

import android.os.Handler;

import com.Player.Core.PlayerClient;
import com.example.umeyesdk.entity.SearchDeviceInfo;

public class ModifySearchDeviceNameThread extends Thread {

	SearchDeviceInfo node;
	Handler handler;
	String newName;
	PlayerClient playClient;

	public ModifySearchDeviceNameThread(PlayerClient playClient,
										SearchDeviceInfo node, Handler handler, String newName) {
		this.node = node;
		this.handler = handler;
		this.newName = newName;
		this.playClient = playClient;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			boolean ret = ModifySearchDeviceName(playClient, node, newName);
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
	 * 修改搜索设备的名字
	 *
	 * @param node
	 * @return
	 */
	public boolean ModifySearchDeviceName(PlayerClient playerclient,
										  SearchDeviceInfo node, String newName) {
		int ret = -1;

		ret = playerclient.ModifyDevName(node.dwVendorId, node.sDevId,
				node.sAdapterMac_1, newName);

		return ret > 0;
	}

}