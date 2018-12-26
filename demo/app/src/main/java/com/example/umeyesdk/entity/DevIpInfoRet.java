package com.example.umeyesdk.entity;

import com.example.umeyesdk.entity.DevIpInfo;

public class DevIpInfoRet {
	/**
	 * NPC_D_DPS_JSON_FUNCID_DEV_IP=0x03 设备有线网络配置
	 */
	public int Operation;
	/**
	 * 请求类型；0:APP向设备获取信息，1：APP将配置信息交给设备做处理 (数据类型：int)
	 */
	public int Request_Type;
	/**
	 * 执行结果，-1：不支持改功能；0：失败；1：成功 (数据类型：int)
	 */
	public int Result;
	/**
	 * 各操作的JSON数据
	 */
	public DevIpInfo Value;

	public DevIpInfoRet() {
		super();
	}

	@Override
	public String toString() {
		return "DevCodeInfoRet [Operation=" + Operation + ", Request_Type="
				+ Request_Type + ", Result=" + Result + ", Value=" + Value
				+ "]";
	}

}
