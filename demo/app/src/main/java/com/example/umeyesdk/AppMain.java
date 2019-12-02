package com.example.umeyesdk;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.Player.Core.PlayerClient;
import com.Player.web.websocket.ClientCore;
import com.Player.web.websocket.IoTTokenInvalidListener;
import com.example.umeyesdk.entity.PlayNode;

public class AppMain extends Application {
	public static final String FILTER = "com.example.umeyesdk.RefreshData";
	private ClientCore pc;
	private PlayerClient playerclient;
	private List<PlayNode> nodeList;
	public boolean isRun = false;
	private int currentNodeId = 0; // 当前列表父节点的ID；

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		nodeList = new ArrayList<PlayNode>();
		pc = ClientCore.getInstance();
		// 设置免登陆支持报警，如果服务器不支持，必须 设置ClientCore.isSuportLocalAlarmPush=false；
		ClientCore.isSuportLocalAlarmPush = false; // 默认是不支持
		playerclient = new PlayerClient();
		if (!isRun) {
			isRun = true;
			new Thread() {

				@Override
				public void run() {

					while (isRun) {
						String log = pc.CLTLogData(100);
						if (!TextUtils.isEmpty(log)) {
							Log.d("WriteLogThread", log);
						}
					}

				}

			}.start();
		}

		//设置登录账号已在其他地方登录或者未登录的错误回调函数，可在baseActivity使用
		ClientCore.getInstance().setIotTokenInvalidListener(new IoTTokenInvalidListener() {
			@Override
			public void onIoTTokenInvalid() {//线程回调，更新UI请用handler

			}
		});

		super.onCreate();
	}

	public synchronized PlayerClient getPlayerclient() {
		return playerclient;
	}

	public int getCurrentNodeId() {
		return currentNodeId;
	}

	public void setCurrentNodeId(int currentNodeId) {
		this.currentNodeId = currentNodeId;
	}

	public ClientCore getPc() {
		return pc;
	}

	public void setPc(ClientCore pc) {
		this.pc = pc;
	}

	public List<PlayNode> getNodeList() {
		return nodeList;
	}

	public synchronized List<PlayNode> getDvrAndCamera() {
		List<PlayNode> list = new ArrayList<PlayNode>();
		for (int i = 0; i < nodeList.size(); i++) {
			PlayNode dvrNode = nodeList.get(i);
			if (dvrNode.IsDvr()) {
				list.add(dvrNode);

			} else if (dvrNode.isCamera()
					&& TextUtils.isEmpty(dvrNode.getParentId())) {
				list.add(dvrNode);
			}
		}
		return list;
	}

	public void setNodeList(List<PlayNode> nodeList) {
		this.nodeList = nodeList;
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
