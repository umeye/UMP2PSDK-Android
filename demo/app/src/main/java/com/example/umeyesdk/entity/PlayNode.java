package com.example.umeyesdk.entity;

import java.io.Serializable;

import com.Player.Core.PlayerClient;
import com.Player.Source.SDKError;
import com.Player.Source.TFileListNode;
import com.Player.web.response.DevItemInfo;
import com.example.umeyesdk.utils.NodeType;

public class PlayNode implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2443499198699802287L;
	public TFileListNode node = null;
	private int level = -1;
	private int childrenCount = 0;
	private String route;
	public boolean hasLat = false, hasLon = false;
	public boolean childrenHasLatLon = false;// 子节点中是否有经纬信息,只有当是目录时此属性才有效
	public boolean isDenfence = false;
	public boolean isSelectToPlay = false;
	public boolean isFavorite = false;
	public boolean isExanble = false;
	public boolean isPlayAll = false;
	public boolean parentIsDvr;
	public String connecParams = null;
	public String umid = "";
	public String ip;
	public String dev_user;
	public String dev_passaword;
	public int dev_ch_no;
	public int dev_ch_num;
	public int dev_stream_no;

	public PlayNode() {

	}

	public TFileListNode getNode() {
		return node;
	}

	public boolean IsAlarm() {
		return node.ucIfArming == 1;
	}

	// public void setAlarm(boolean isAlarm) {
	// this.isAlarm = isAlarm;
	// }
	public static PlayNode ChangeData(DevItemInfo info) {
		PlayNode node = new PlayNode();
		TFileListNode temp = new TFileListNode();

		temp.dwNodeId = info.node_id;
		temp.dwParentNodeId = info.parent_node_id;
		temp.iNodeType = info.node_type;
		temp.sDevId = info.dev_id;
		temp.sNodeName = info.node_name;
		// temp.ucIfLongLat = info.;
		temp.iConnMode = info.conn_mode;
		temp.dwLatitude = (int) info.latitude;
		temp.dwLongitude = (int) info.longitude;
		temp.ucDevState = 1;
		temp.ucIfPtz = info.is_ptz;
		// temp.iDevPopNum = info.iDevPopNum;
		temp.ucIfArming = info.is_arming;
		temp.usVendorId = info.vendor_id;
		node.node = temp;
		node.connecParams = info.conn_params;
		temp.id_type = info.id_type;
		node.umid = info.umid;
		node. ip=info.ip;
		node. dev_user=info.dev_user;
		node. dev_passaword=info.dev_passaword;
		node. dev_ch_no=info.dev_ch_no;
		node. dev_ch_num=info.dev_ch_num;
		node.dev_stream_no=info.dev_stream_no;
		// node.connectInfo = changetoConnectInfo(info.conn_params);
		return node;
	}

	public void setNode(TFileListNode node) {
		this.node = DeepCopy(node);
	}

	public boolean isSelectToPlay() {
		return isSelectToPlay;
	}

	public void setSelectToPlay(boolean isSelectToPlay) {
		this.isSelectToPlay = isSelectToPlay;
	}

	public void setChildrenHasLatLon(boolean childrenHasLatLon) {
		this.childrenHasLatLon = childrenHasLatLon;
	}

	public boolean getChilderHasLatLon() {
		return childrenHasLatLon;
	}

	public long getLon() {
		return node.dwLongitude;
	}

	public long getLat() {
		return node.dwLatitude;
	}

	/**
	 * 播放点是否在线
	 *
	 * @return
	 */
	public boolean isOnline() {
		return node.ucDevState == 1;
	}

	/**
	 * 是否有经纬度信息
	 *
	 * @return
	 */
	public boolean HasLatLon() {
		return node.ucIfLongLat == 1;
	}

	/**
	 * 是否有云台权限
	 *
	 * @return
	 */
	public boolean IsSupportPtz(PlayerClient playerclient) {
		// 查询用户的全局权限是否存在 返回1表示存在全局权限 返回0表示没有全局权限也没有iPopId权限 返回2表示有iPopId权限
		return playerclient.CheckPoporNot(node,
				SDKError.NPC_D_MPI_MON_USER_POP_PTZ) > 0;
	}

	/**
	 * 是否有布放权限
	 *
	 * @return
	 */
	public boolean IsSupportDefence(PlayerClient playerclient) {
		// 查询用户的全局权限是否存在 返回1表示存在全局权限 返回0表示没有全局权限也没有iPopId权限 返回2表示有iPopId权限
		return playerclient.CheckPoporNot(node,
				SDKError.NPC_D_MPI_MON_USER_POP_RECV_ALARM) > 0;
	}

	public boolean isPtz() {
		return node.ucIfPtz == 1;

	}

	// 是否是布防状态
	public boolean isDefence() {
		return node.ucIfArming == 1;
	}

	public boolean IsDirectory() {
		return node.iNodeType == NodeType.DIRECTORY;
	}

	public boolean IsDvr() {
		return node.iNodeType == NodeType.DVR;
	}

	/**
	 * 仅针对于Camera和device
	 *
	 * @return true为镜头，false为设备
	 */
	public boolean isCamera() {
		return node.iNodeType == NodeType.CAMERA;
	}

	public PlayNode(TFileListNode node) {
		this.node = DeepCopy(node);
	}

	public int getLevel() {
		return level;
	}

	public int getChildrenCount() {
		return childrenCount;
	}

	public void setChildrenCount(int childrenCount) {
		this.childrenCount = childrenCount;
	}

	public String getName() {
		return node.sNodeName;
	}

	public String getDeviceId() {
		return connecParams;
	}

	public String getParentId() {
		return node.dwParentNodeId;
	}

	public TFileListNode DeepCopy(TFileListNode node) {
		TFileListNode temp = new TFileListNode();
		temp.dwNodeId = node.dwNodeId;
		temp.dwParentNodeId = node.dwParentNodeId;
		temp.iNodeType = node.iNodeType;
		temp.sDevId = node.sDevId;
		temp.sNodeName = node.sNodeName;
		temp.ucIfLongLat = node.ucIfLongLat;
		temp.dwLatitude = node.dwLatitude;
		temp.dwLongitude = node.dwLongitude;
		temp.ucDevState = node.ucDevState;
		temp.ucIfPtz = node.ucIfPtz;
		temp.iDevPopNum = node.iDevPopNum;
		temp.ucIfArming = node.ucIfArming;
		temp.usVendorId = node.usVendorId;
		temp.iConnMode = node.iConnMode;
		for (int i = 0; i < node.iDevPopNum; i++) {
			temp.ucDevPopTable[i] = node.ucDevPopTable[i];
		}
		return temp;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 创建一个简单的播放节点，只有播放所需要的信息，及route,其他都没有，此函数仅仅在收藏中有用
	 *
	 * @return
	 */
	public static PlayNode CreateSimplePlayNode(String Name) {
		PlayNode playNode = new PlayNode();
		TFileListNode node = new TFileListNode();
		node.sNodeName = Name;
		playNode.isPlayAll = true;
		playNode.node = node;
		node.iNodeType = NodeType.CAMERA;
		return playNode;
	}
}
