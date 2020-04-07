package com.example.umeyesdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseGetInfomation;
import com.Player.web.websocket.ClientCore;
import com.example.umeyeNewSdk.AcSelectMode;
import com.example.umeyesdk.R;
import com.example.umeyesdk.api.WebSdkApi;

/**
 * 更多功能
 *
 * @author Administrator
 *
 */
public class MoreFuncDialog extends AlertDialog.Builder {
	ClientCore clientCore;
	Activity activity;
	Handler handler;
	public String[] funcArray = { "添加设备", "刷新列表", "修改密码", "发送邮件重置密码", "查询报警记录",	"删除所有报警", "注销" };

	public MoreFuncDialog(Activity arg0, ClientCore clientCore,Handler handler) {
		super(arg0);
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
				switch (which) {
					case 0:
						// 添加4路子码流的dvr
						WebSdkApi.addNodeInfo(activity, clientCore,
								String.valueOf(System.currentTimeMillis()), "", 1, 2, 1009,
								Constants.UMID, "", 0, Constants.user,
								Constants.password, 8, 0, 1, 0,0,"",handler);
						break;
					case 1:
						// 重新获取列表
						WebSdkApi.getNodeList(activity, clientCore, "", 0, 0,handler);
						break;
					case 2:
						// 修改登录用户密码
						WebSdkApi.modifyUserPassword(activity, clientCore, "111111","111111");
						break;
					case 3:
						// 发送重置密码到注册时所填的邮箱
						WebSdkApi.resetUserPassword(activity, clientCore, "yin", 2);
						break;
					case 4:
						// 查询报警记录
						WebSdkApi.queryAlarmList(clientCore);
						break;
					case 5:
						// 删除所有报警
						WebSdkApi.deleteAllAlarm(clientCore);
						break;
					case 6:
						// 注销
						WebSdkApi.logoutServer(clientCore, 1, new Handler(){
							@Override
							public void handleMessage(Message msg) {
								Toast.makeText(activity, R.string.logout_user, Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(activity, AcSelectMode.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
								activity.startActivity(intent);
								ClientCore.getInstance().setIotTokenInvalidListener(null);
							}
						});

						break;
					default:
						break;
				}
				dialog.dismiss();
			}
		});
	}
}
