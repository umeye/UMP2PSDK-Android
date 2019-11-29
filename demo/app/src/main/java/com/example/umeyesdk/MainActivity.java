package com.example.umeyesdk;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.Player.Core.PlayerClient;
import com.Player.Source.DevInfo;
import com.Player.Source.TAlarmSetInfor;
import com.Player.web.response.DevItemInfo;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseDevList;
import com.Player.web.websocket.ClientCore;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.adpter.DeviceManagerAdapter;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.CameraFuncDialog;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.MoreFuncDialog;
import com.example.umeyesdk.utils.Show;

public class MainActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener {
	public ClientCore clientCore;
	String imsi;
	public Context context;
	ListView listView;
	Button btnMoreFunc;
	DeviceManagerAdapter adapter;
	public List<PlayNode> nodeList = new ArrayList<PlayNode>();
	RefreshData refresh;
	AppMain appMain;
	ResponseDevList responseDevList;
	MoreFuncDialog moreFuncDialog;
	CameraFuncDialog cameraFuncDialog;
	/**
	 * 用Handler来更新UI
	 */
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case Constants.MODIFY_DEV_NUM_F:
					Show.toast(MainActivity.this, "修改设备通道数失败");
					break;
				case Constants.MODIFY_DEV_NUM_S:
					WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,
							this);
					Show.toast(MainActivity.this, "修改设备通道数成功");
					break;
				case Constants.MODIFY_DEV_S:
					WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,
							this);
					Show.toast(MainActivity.this, "修改设备成功");
					break;
				case Constants.MODIFY_DEV_F:
					Show.toast(MainActivity.this, "修改设备失败");
					break;
				case Constants.DELETE_DEV_F:
					Show.toast(MainActivity.this, "删除设备失败");
					break;
				case Constants.DELETE_DEV_S:
					WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,
							this);
					Show.toast(MainActivity.this, "删除设备成功");
					break;

				case Constants.ADD_DEV_S:
					WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,
							this);
					Show.toast(MainActivity.this, "添加设备成功");
					break;
				case Constants.ADD_DEV_F:
					Show.toast(MainActivity.this, "添加设备失败");
					break;

				case Constants.GET_DEVLIST_F:
					Show.toast(MainActivity.this, "获取设备失败");
					break;
				case Constants.GET_DEVLIST_S:
					nodeList.clear();
					List<PlayNode> reGetList = new ArrayList<PlayNode>();
					responseDevList = (ResponseDevList) msg.obj;
					List<DevItemInfo> items = responseDevList.b.nodes;
					for (int i = 0; i < items.size(); i++) {
						DevItemInfo devItemInfo = items.get(i);
						if (devItemInfo != null) {
							PlayNode node = PlayNode.ChangeData(devItemInfo);
							Log.d("PlayNode", "PlayNode.dwParentNodeId:"
									+ node.node.dwParentNodeId
									+ ",PlayNode.dwNodeId" + node.node.dwNodeId);
							reGetList.add(node);
						}
					}
					appMain.setNodeList(reGetList);
					nodeList = appMain.getDvrAndCamera();
					adapter.setNodeList(nodeList);
					adapter.notifyDataSetChanged();

					Show.toast(MainActivity.this, "获取设备成功");
					break;

				case Constants.DELETE_SUCCEED:
					adapter.notifyDataSetChanged();
					Show.toast(context, R.string.delete_succeed);

					break;
				case Constants.DELETE_FAILED:
					Show.toast(context, R.string.delete_failed);
					break;
				case Constants.ADD_FAILED:
					Show.toast(context, R.string.add_failed);
					break;
				case Constants.ADD_SUCCEED:
					adapter.notifyDataSetChanged();
					Show.toast(context, R.string.add_succeed);
					break;
				case Constants.SET_ALARM_F:
					Show.toast(context, "布放失败");
					break;
				case Constants.SET_ALARM_S:
					Show.toast(context, "布放成功");
					break;
				case Constants.CANCEL_ALARM_F:
					Show.toast(context, "撤防失败");
					break;
				case Constants.CANCEL_ALARM_S:
					Show.toast(context, "撤防成功");

				default:
					break;
			}

		}
	};
	private String currentDVR = "";// 当前选中的dvr设备

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_live_list);
		context = this;
		appMain = (AppMain) getApplication();

		initeView();
		clientCore = ClientCore.getInstance();

	}

	void initeView() {
		listView = (ListView) findViewById(R.id.lvLive);
		nodeList = appMain.getDvrAndCamera();// 列表重新赋值
		adapter = new DeviceManagerAdapter(context, nodeList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(MainActivity.this);
		listView.setOnItemLongClickListener(MainActivity.this);
		adapter.setNodeList(nodeList);
		btnMoreFunc = (Button) findViewById(R.id.btnMore);
		btnMoreFunc.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		appMain.setRun(false);

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		PlayNode node = nodeList.get(arg2);
		// TODO Auto-generated method stub
		if (node.IsDirectory()) {
			Show.toast(context, "点击了目录");
			return;
		} else if (node.IsDvr()) {
			showChannleList(node.getNode().dwNodeId);
			// Show.toast(context, "点击了DVR");

			return;
		} else {

			if (cameraFuncDialog == null) {
				cameraFuncDialog = new CameraFuncDialog(this, clientCore, node, handler);
			}
			cameraFuncDialog.setTitle(R.string.function);
			cameraFuncDialog.show();

			// node.isCamera()

		}
	}

	/**
	 * 显示通道列表
	 *
	 * @param nodeId
	 */
	void showChannleList(String nodeId) {
		currentDVR = nodeId;
		nodeList.clear();
		nodeList = GetDevChildrenList(nodeId, appMain.getNodeList());
		adapter.setNodeList(nodeList);
		adapter.notifyDataSetChanged();

	}

	/**
	 * 获取设备下的通道节点
	 *
	 *
	 */
	public List<PlayNode> GetDevChildrenList(String nodeId,
											 List<PlayNode> NodeList) {
		List<PlayNode> list = new ArrayList<PlayNode>();

		for (int i = 0; i < NodeList.size(); i++)// 查找node下的直接子节点
		{
			PlayNode temp = NodeList.get(i);
			if (temp.node.dwParentNodeId.equals(nodeId)) { // 设备节点的.node.dwNodeId是该设备下通道节点的.node.dwParentNodeId
				list.add(temp);
			}

		}
		return list;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnMore:

				// ClientCore.getInstance().editUserInfo(new Handler() {
				//
				// @Override
				// public void handleMessage(Message msg) {
				// ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				// if (responseCommon != null && responseCommon.h.e == 200) {
				// Log.i("responseCommon", "设置个人信息成功");
				// } else
				// Log.e("responseCommon", "设置个人信息失败");
				// super.handleMessage(msg);
				// }
				// }, "547373990@qq.com", "yin", "",
				// "http://app.umeye.con/4243636.jpg", "18665572599", "1",
				// "广州", "1991-07-09", "1");
				// new UploadFileUtil("/sdcard/aa.jpg", new UploadFileListener() {
				//
				// @Override
				// public void uploadStart() {
				// Log.i("UploadFileUtil", "uploadStart");
				// }
				//
				// @Override
				// public void uploadProgress(int arg0) {
				// Log.i("UploadFileUtil", "uploadProgress" + arg0);
				// }
				//
				// @Override
				// public void uploadFinish(String arg0) {
				// Log.i("UploadFileUtil", "uploadFinish" + arg0);
				// }
				//
				// @Override
				// public void uploadError(String arg0) {
				// Log.i("UploadFileUtil", "uploadError" + arg0);
				// }
				// }).uploadDone();

				if(moreFuncDialog == null) {
					moreFuncDialog = new MoreFuncDialog(this, clientCore, handler);
				}
				moreFuncDialog.setTitle(R.string.more);
				moreFuncDialog.show();
				break;

			default:
				break;
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
								   final int arg2, long arg3) {

		final PlayNode node = nodeList.get(arg2);
		// EditDevDialog editDevDialog = new EditDevDialog(this, clientCore,
		// node,
		// handler);
		// editDevDialog.show();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				PlayerClient playerClient = appMain.getPlayerclient();
				DevInfo devInfo = new DevInfo();
				int ret = playerClient.CameraGetDevInfo(node.connecParams,
						devInfo);
				if (ret == 0) {
					Log.d("CameraGetDevInfo", "获取成功：" + devInfo.toString());
				} else {
					Log.e("CameraGetDevInfo", "获取失败！=" + ret);
				}
				playerClient.CameraDisconnect();
				super.run();
			}
		}.start();
		return true;
	}

	public class RefreshData extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("new Data", "Updata~~~~~~~~~~~~~~~~~~~~");

			// handler.sendEmptyMessage(Constants.NEW_DATA);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (!TextUtils.isEmpty(currentDVR) && !"0".equals(currentDVR)) {
//				showChannleList("0");

				currentDVR = "0";
				nodeList.clear();
				nodeList = appMain.getDvrAndCamera();
				adapter.setNodeList(nodeList);
				adapter.notifyDataSetChanged();

				return true;
			} else {
				// 注销登录
				WebSdkApi.logoutServer(clientCore, 1);
				this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
