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
import com.Player.Source.TAlarmSetInfor;
import com.Player.web.response.DevItemInfo;
import com.Player.web.response.ResponseDevList;
import com.Player.web.websocket.ClientCore;
import com.example.umeyesdk.adpter.DeviceManagerAdapter;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.EditDevDialog;
import com.example.umeyesdk.utils.MoreFuncDialog;
import com.example.umeyesdk.utils.Show;
import com.getui.demo.AlarmUtils;

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
	AppMain ua;
	ResponseDevList responseDevList;
	MoreFuncDialog moreFuncDialog;
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
				WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,this);
				Show.toast(MainActivity.this, "修改设备通道数成功");
				break;
			case Constants.MODIFY_DEV_S:
				WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,this);
				Show.toast(MainActivity.this, "修改设备成功");
				break;
			case Constants.MODIFY_DEV_F:
				Show.toast(MainActivity.this, "修改设备失败");
				break;
			case Constants.DELETE_DEV_F:
				Show.toast(MainActivity.this, "删除设备失败");
				break;
			case Constants.DELETE_DEV_S:
				WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,	this);
				Show.toast(MainActivity.this, "删除设备成功");
				break;

			case Constants.ADD_DEV_S:
				WebSdkApi.getNodeList(MainActivity.this, clientCore, "0", 0, 0,this);
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
				ua.setNodeList(reGetList);
				nodeList = ua.getDvrAndCamera();
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
		ua = (AppMain) getApplication();

		initeView();
		clientCore = ClientCore.getInstance();

	}

	void initeView() {
		listView = (ListView) findViewById(R.id.lvLive);
		nodeList = ua.getDvrAndCamera();// 列表重新赋值
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
		ua.setRun(false);

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
			// node.isCamera()
			// 如果是通道节点，就进入播放页面
			Intent intent = new Intent(context, PlayActivity.class);
			intent.putExtra("id", node.connecParams);
			startActivity(intent);
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
		nodeList = GetDevChildrenList(nodeId, ua.getNodeList());
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
			if (moreFuncDialog == null) {
				moreFuncDialog = new MoreFuncDialog(this, clientCore,handler);
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

		PlayNode node = nodeList.get(arg2);
		EditDevDialog editDevDialog = new EditDevDialog(this, clientCore, node,	handler);
		editDevDialog.show();
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
			if (!TextUtils.isEmpty(currentDVR)) {
				showChannleList("0");
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
