package com.example.umeyesdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;

import com.Player.Source.TFileListNode;
import com.Player.web.websocket.ClientCore;
import com.getui.demo.AlarmUtils;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.entity.PlayNode;

/**
 * 更多功能
 *
 * @author Administrator
 *
 */
public class EditDevDialog extends AlertDialog.Builder {
	ClientCore clientCore;
	Activity activity;
	Handler handler;
	public String[] funcArray = { "修改设备", "删除设备", "查询布防", "设置布防", "撤销布防",
			"修改通道数" };
	PlayNode node;

	public EditDevDialog(Activity arg0, ClientCore clientCore, PlayNode node,
						 Handler handler) {
		super(arg0);
		this.node = node;
		this.activity = arg0;
		this.clientCore = clientCore;
		this.handler = handler;
		setItems();
	}

	public void setItems() {
		setItems(funcArray, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				TFileListNode tFileListNode = node.node;
				switch (which) {

					case 0:
						// 修改设备连接参数，通道名称、设备用户名密码、码流、摄像机的通道号
						WebSdkApi.modifyNodeInfo(activity, clientCore,
								node.node.dwNodeId, "test_modify", 2,
								tFileListNode.usVendorId, node.umid, "", 0,
								"admin", "", 0, 1, "", 1, handler);
						break;
					case 1:
						WebSdkApi.deleteNodeInfo(activity, clientCore,
								String.valueOf(node.node.dwNodeId),
								node.node.ucNodeKind, node.node.id_type, handler);
						break;
					case 2:
						/**
						 * 只有通过umid添加的摄像机镜头，独立camrea 或者dvr 下的镜头 可以进行查询布放记录
						 */
						if (node.IsDvr() || node.IsDirectory()
								|| tFileListNode.iConnMode != 2) {
							Show.toast(activity, "只有添加umid的ipc，设备的通道才可以撤防");
							break;
						}
						WebSdkApi.getDeviceAlarm(clientCore, tFileListNode.sDevId,
								AlarmUtils.GETUI_CID);
						break;
					case 3:
						/**
						 * 只有通过umid添加的摄像机镜头，独立camrea 或者dvr 下的镜头 可以进行布防
						 */
						if (node.IsDvr() || node.IsDirectory()
								|| tFileListNode.iConnMode != 2) {
							Show.toast(activity, "只有添加umid的ipc，设备的通道才可以布防");
							break;
						}
						WebSdkApi.setDeviceAlarm(clientCore, node, 1,
								AlarmUtils.GETUI_CID, new int[] { 1, 2, 3, 4, 5 });
						break;
					case 4:
						/**
						 * 只有通过umid添加的摄像机镜头，独立camrea 或者dvr 下的镜头 可以进行布撤防
						 */
						if (node.IsDvr() || node.IsDirectory()
								|| tFileListNode.iConnMode != 2) {
							Show.toast(activity, "只有添加umid的ipc，设备的通道才可以撤防");
							break;
						}
						WebSdkApi.setDeviceAlarm(clientCore, node, 2,
								AlarmUtils.GETUI_CID, new int[] { 1, 2, 3, 4, 5 });
						break;
					case 5:
						/**
						 * 修改设备通道号
						 */
						if (node.IsDvr()) {
							WebSdkApi.modifyDevNum(clientCore,
									String.valueOf(node.node.dwNodeId),
									node.node.sDevId, 16, handler);
						}
						break;

					default:
						break;

				}
				dialog.cancel();
			}
		});
	}

}
