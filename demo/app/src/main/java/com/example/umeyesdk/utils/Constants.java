package com.example.umeyesdk.utils;

import com.Player.Core.Utils.CommenUtil;
import com.Player.web.websocket.ClientCore;

public class Constants {
	/**
	 * 服务器
	 */
	public static String server = "v0.api.umeye.com";//v4iot.umeye.com

	public static final int port = 8300;// 端口
	/**
	 * 客户端标识 ，由服务器生成，如需推送功能， 需后台绑定第三方推送的PUSH_APPID，PUSH_APPKEY，PUSH_APPSECRET。
	 */
	public static final String custom_flag = "1000000053";
	/**
	 * 登录模式 默认用户名
	 */
	public static String Login_user = "";

	/**
	 * 登录模式 默认密码
	 */

	public static String Login_password = "";

	/**
	 * umid直连模式 默认设备UMID
	 */
	public static String UMID = "02et3p3adveh";
	/**
	 * umid直连模式 默认设备名
	 */
	public static String user = "admin";
	/**
	 * umid直连模式 默认设备密码
	 */
	public static String password = "";// 设备密码
	//zd1234

	/**
	 * umid直连模式当前选择设备通道号
	 */
	public static int iChNo = 0;//
	/**
	 * 默认截图路径
	 */
	public static String UserImageDir = CommenUtil.getExternalStorageFile(ClientCore.getInstance().getContext()) + "/snapshot";
	/**
	 * 默认录像路径
	 */
	public static String UserVideoDir = CommenUtil.getExternalStorageFile(ClientCore.getInstance().getContext()) + "/videorecord";


	public static int Vendorid = 1009;

	public final static int LOGIN = 1;// 登陆
	public final static int LOGIN_OK = 2;// 登陆成功
	public final static int LUNCH_FAILED = -1;// 启动客户端失败
	public final static int LOGIN_USER_ERROR = -2;// 用户名错误登陆失败
	public final static int LOGIN_PWD_ERROR = -24;// 密码错误登陆失败
	public final static int LOGIN_FAILED = -3;// 其他登陆失败
	public final static int MODIFY_PASSWORD_S = 3;// 修改密码成功
	public final static int MODIFY_PASSWORD_F = -4;// 修改密码失败
	public final static int REGIST_S = 5;// 注册成功
	public final static int REGIST_F = -5;// 注册失败
	public final static int LOGOUT_S = 6;// 注销成功
	public final static int LOGOUT_F = -6;// 注销失败
	public final static int GET_DEVLIST_F = 0;// 获取列表失败
	public final static int GET_DEVLIST_S = 7;// 获取列表成功
	public final static int GET_DEVLIST_OK_NO_DATA = 8;// 获取列表成功,没设备数据
	public final static int RESET_PASSWORD_S = 9;// 发送重置密码成功
	public final static int RESET_PASSWORD_F = -9;// 发送重置密码失败
	public final static int ADD_DEV_S = 10;// 添加设备成功
	public final static int ADD_DEV_F = -10;// 添加设备失败
	public final static int DELETE_DEV_S = 11;// 删除设备成功
	public final static int DELETE_DEV_F = -11;// 删除设备失败
	public final static int MODIFY_DEV_S = 12;// 修改设备成功
	public final static int MODIFY_DEV_F = -12;// 修改设备失败
	public final static int QUERY_ALARM_S = 13;// 查询报警成功
	public final static int QUERY_ALARM_F = -13;// 查询报警失败
	public final static int MODIFY_DEV_NUM_S = 14;// 修改设备成功
	public final static int MODIFY_DEV_NUM_F = -14;// 修改设备失败
	public final static int SEND_SMS_S = 15;//发送验证码成功
	public final static int SEND_SMS_F = -15;//发送验证码失败
    public final static int GET_SHARE_INFO_S = 16;// 获取分享信息失败
    public final static int GET_SHARE_INFO_F = -16;// 获取分享信息成功
	public final static int REFRESH_SESSION_S = 17;// 刷新session失败
	public final static int REFRESH_SESSION_F = -17;// 刷新session成功
	public final static int SEND_EMAIL_CODE_S = 18;//发送验证码成功
	public final static int SEND_EMAIL_CODE_F = -18;//发送验证码失败
	public final static int ADD_SHARE_S = 19;//添加分享成功
	public final static int ADD_SHARE_F = -19;//添加分享失败
	public final static int DELETE_SHARE_S = 20;//取消分享成功
	public final static int DELETE_SHARE_F = -20;//取消分享失败
	public final static int GET_PUBLIC_USER_ACCOUNT_S = 21;//获取公众号信息成功
	public final static int GET_PUBLIC_USER_ACCOUNT_F = -21;//获取公众号信息失败
	public final static int QUERY_USER_PUSH_S = 22;//获取用户推送信息成功
	public final static int QUERY_USER_PUSH_F = -22;//获取用户推送信息失败
    public final static int CLEAR_USER_BY_DEVICE_ID_S = 23;
    public final static int CLEAR_USER_BY_DEVICE_ID_F = -23;
	// public static final byte NEW_DATA = 0x11;
	public static final byte DELETE_FAILED = 0x12;
	public static final byte DELETE_SUCCEED = 0x13;
	public static final byte ADD_FAILED = 0x14;
	public static final byte ADD_SUCCEED = 0x15;
	public static final byte SET_ALARM_S = 0x16;
	public static final byte SET_ALARM_F = 0x17;
	public static final byte CANCEL_ALARM_S = 0x18;
	public static final byte CANCEL_ALARM_F = 0x19;
}
