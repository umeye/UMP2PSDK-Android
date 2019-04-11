package com.example.umeyesdk.api;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.Source.TAlarmSetInfor;
import com.Player.Source.TDevNodeInfor;
import com.Player.web.request.P2pConnectInfo;
import com.Player.web.response.DevState;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseDevList;
import com.Player.web.response.ResponseDevState;
import com.Player.web.response.ResponseGetServerList;
import com.Player.web.response.ResponseQueryAlarm;
import com.Player.web.response.ResponseQueryAlarmSettings;
import com.Player.web.response.ServerListInfo;
import com.Player.web.websocket.ClientCore;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;

public class WebSdkApi {
	public static final String WebSdkApi = "WebSdkApi";

	/**
	 * 错误码定义： 具体请求错误码定义参考HttpErrorCode
	 */

	/**
	 * 登陆
	 *
	 * @param pc
	 * @param userName
	 *            用户ID 必填 24位 , 限定字母，数字，下划线 必填
	 * @param password
	 *            必填 密码 必填 20位 , 限定字母，数字，下划线
	 **/
	public static void loginServerAtUserId(final Context context,
										   final ClientCore clientCore, String userName, String password,
										   final Handler handler) {
		clientCore.loginServerAtUserId(context, userName, password,
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						ResponseCommon responseCommon = (ResponseCommon) msg.obj;
						if (responseCommon != null && responseCommon.h != null) {
							if (responseCommon.h.e == 200) {
								handler.sendEmptyMessage(Constants.LOGIN_OK);
							} else if (responseCommon.h.e == 406) {
								handler.sendEmptyMessage(Constants.LOGIN_USER_OR_PWD_ERROR);
							} else {
								Log.e(WebSdkApi, "登录失败!code="
										+ responseCommon.h.e);
								handler.sendEmptyMessage(Constants.LOGIN_FAILED);
							}
						} else {
							Log.e(WebSdkApi, "登录失败! error=" + msg.what);
							handler.sendEmptyMessage(Constants.LOGIN_FAILED);
						}
						super.handleMessage(msg);
					}

				});
	}

	/**
	 * 注册账户
	 *
	 * @param pc
	 * @param aUserId
	 *            用户ID 必填 24位 , 限定字母，数字，下划线
	 * @param aPassword
	 *            密码 必填 20位 , 限定字母，数字，下划线
	 * @param user_email
	 *            邮箱 必填 32位 , 合法邮箱
	 * @param nickName
	 *            昵称 32位 , 限定英文，数字，下划线
	 * @param user_phone
	 *            手机号码 32位 , 限定数字
	 * @param user_id_card
	 *            用户身份证id 24位
	 */
	public static void registeredUser(final Context context,
									  final ClientCore clientCore, String aUserId, String aPassword,
									  String user_email, String nickName, String user_phone,
									  String user_id_card) {
		clientCore.registeredUser(aUserId, aPassword, user_email, nickName,
				user_phone, user_id_card, new Handler() {

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						ResponseCommon responseCommon = (ResponseCommon) msg.obj;
						if (responseCommon != null && responseCommon.h != null) {
							if (responseCommon.h.e == 200) {
								Show.toast(context, "注册成功");
							} else {
								Log.e(WebSdkApi, "注册失败!code="
										+ responseCommon.h.e);
								Show.toast(context, "注册失败");
							}

						} else {
							Log.e(WebSdkApi, "注册失败! error=" + msg.what);
							Show.toast(context, "注册失败");
						}
						super.handleMessage(msg);
					}

				});
	}

	/**
	 * 注销
	 *
	 * @param pc
	 * @param disableAlarm
	 *            是否取消报警推送
	 * @param handler
	 */
	public static void logoutServer(final ClientCore clientCore,
									int disableAlarm) {
		clientCore.logoutServer(disableAlarm, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Log.e(WebSdkApi, "注销成功!code=" + responseCommon.h.e);
					} else {
						Log.e(WebSdkApi, "注销失败!code=" + responseCommon.h.e);
					}

				} else {
					Log.e(WebSdkApi, "注销失败! error=" + msg.what);
				}
				super.handleMessage(msg);
			}
		});
	}

	/**
	 * 修改密码
	 *
	 * @param pc
	 * @param oldPassword
	 *            20位 , 限定字母，数字，下划线 旧密码
	 * @param newPassword
	 *            20位 , 限定字母，数字，下划线 新密码
	 */
	public static void modifyUserPassword(final Context context,
										  final ClientCore clientCore, String oldPassword, String newPassword) {
		clientCore.modifyUserPassword(oldPassword, newPassword, new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Show.toast(context, "修改密码成功");
					} else {
						Log.e(WebSdkApi, "修改密码失败!code=" + responseCommon.h.e);
						Show.toast(context, "修改密码失败");
					}
				} else {
					Log.e(WebSdkApi, "修改密码失败! error=" + msg.what);
					Show.toast(context, "修改密码失败");
				}
				super.handleMessage(msg);
			}

		});

	}

	/**
	 * 发送重置密码邮件
	 *
	 * @param pc
	 * @param user_id
	 *            需要重置密码的用户名
	 * @param language
	 *            语言：1表示英文(English)，2表示简体中文(SimpChinese)， 3表示繁体中文(TradChinese)
	 *
	 */
	public static void resetUserPassword(final Context context,
										 final ClientCore clientCore, String user_id, int language) {
		clientCore.resetUserPassword(user_id, language, new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Show.toast(context, "发送重置密码成功，稍后请查收！");
					} else {
						Log.e(WebSdkApi, "发送重置密码失败!code=" + responseCommon.h.e);
						Show.toast(context, "发送重置密码失败");
					}
				} else {
					Log.e(WebSdkApi, "发送重置密码失败! error=" + msg.what);
					Show.toast(context, "发送重置密码失败");
				}
				super.handleMessage(msg);
			}

		});

	}

	/**
	 * 获取设备列表
	 *
	 * @param pc
	 * @param parent_node_id
	 *            父节点ID
	 * @param page_index
	 *            分页功能，指定从第几页开始，是可选的，默认不分页；
	 * @param page_size
	 *            分页功能，每页返回的记录数，是可选的，默认不分页；
	 * @param handler
	 */
	public static void getNodeList(final Context context,
								   final ClientCore clientCore, String parent_node_id, int page_index,
								   int page_size, final Handler handler) {
		clientCore.getNodeList(parent_node_id, page_index, page_size,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						ResponseDevList responseDevList = (ResponseDevList) msg.obj;
						if (responseDevList != null
								&& responseDevList.h != null) {
							if (responseDevList.h.e == 200) {
								handler.sendMessage(Message.obtain(handler,
										Constants.GET_DEVLIST_S,
										responseDevList));
							} else {
								Log.e(WebSdkApi, "获取设备列表失败!code="
										+ responseDevList.h.e);
								handler.sendEmptyMessage(Constants.GET_DEVLIST_F);
							}
						} else {
							Log.e(WebSdkApi, "获取设备列表失败! error=" + msg.what);
							handler.sendEmptyMessage(Constants.GET_DEVLIST_F);
						}
						super.handleMessage(msg);
					}
				});
	}

	/**
	 * 添加设备节点
	 *
	 * @param pc
	 * @param node_name
	 *            长度 28 限定中文，字母，数字，下划线 名称
	 * @param parent_node_id
	 *            父节点ID
	 * @param node_type
	 *            节点类型 : 0表示目录节点、1表示设备节点、2表示摄像机节点；当为添加子节点时，通道号为0
	 * @param conn_mode
	 *            当node_type不为0的时候是必须的 连接模式: 0表示直连模式、1表示流媒体服务器模式、
	 *            2表示P2P云模式、3表示云流媒体模式；
	 *
	 * @param vendor_id
	 *
	 *            厂商ID，当node_type不为0的时候是必须的，取值范围：
	 *            1001表示华科流媒体协议（bit）、1002表示华科流媒体协议（XML）、
	 *            1003表示华科流媒体协议（JSON）、1004表示华科流媒体协议（OWSP）、
	 *            1005表示原华科流媒体服务器协议（HKSP）、1006表示文件摄像机协议（FCAM）、
	 *            1007表示华科监控通讯协议（HMCP）、1008表示华科设备上连协议（HDTP）、
	 *            1009表示UMSP、1010表示EPMY、1011表示RTSP、
	 *            1012表示HTTP、1013表示SIP、1014表示RTMP、
	 *            2010表示杭州海康、2011表示杭州海康推模式、2020表示杭州大华、
	 *            2030表示深圳锐明、2040表示深圳黄河、2050表示深圳汉邦、
	 *            2060表示杭州雄迈、2070表示中山立堡、2080表示成都索贝、
	 *            2090表示上海皓维、2100表示金三立、2110表示上海通立、
	 *            2120表示深圳郎驰、2130表示网视通、2140表示广州奇幻、
	 *            2150表示安联锐视、2160表示广州佳可、2170表示深圳旗翰、 2180表示瀚晖威视。
	 * @param dev_umid
	 *            长度28 限定字母，数字，下划线 设备umid
	 * @param dev_addr
	 *            设备IP或者域名 长度127
	 * @param dev_port
	 *            设备端口 长度10
	 * @param dev_user
	 *            设备用户名 长度60 限制字母，数字，下划线
	 * @param dev_passwd
	 *            设备密码 长度60 限制字母，数字，下划线
	 * @param dev_ch_num
	 *            设备通道数 dvr/nvr通道数
	 * @param dev_ch_no
	 *            设备通道号 node_type为2时有效，添加dvr/nvr指定的通道号
	 * @param dev_stream_no
	 *            设备请求码流 0:主码流，1，子码流
	 * @param handler
	 */
	public static void addNodeInfo(final Context context,
								   final ClientCore clientCore, String node_name, String parent_node_id,
								   int node_type, int conn_mode, int vendor_id, String dev_umid,
								   String dev_addr, int dev_port, String dev_user, String dev_passwd,
								   int dev_ch_num, int dev_ch_no, int dev_stream_no, int only_umid,int limit_appid,
								   final Handler handler) {
		clientCore.addNodeInfo(node_name, parent_node_id, node_type, conn_mode,
				vendor_id, dev_umid, dev_addr, dev_port, dev_user, dev_passwd,
				dev_ch_num, dev_ch_no, dev_stream_no, only_umid, limit_appid, new Handler() {

					@Override
					public void handleMessage(Message msg) {
						ResponseCommon responseCommon = (ResponseCommon) msg.obj;
						if (responseCommon != null && responseCommon.h != null) {
							if (responseCommon.h.e == 200) {
								handler.sendEmptyMessage(Constants.ADD_DEV_S);
							} else {
								Log.e(WebSdkApi, "添加设备失败!code="	+ responseCommon.h.e);
								handler.sendEmptyMessage(Constants.ADD_DEV_F);
							}
						} else {
							Log.e(WebSdkApi, "添加设备失败! error=" + msg.what);
							handler.sendEmptyMessage(Constants.ADD_DEV_F);
						}
						super.handleMessage(msg);
					}

				});

	}

	/**
	 * 删除设备
	 *
	 * @param pc
	 * @param node_id
	 *            节点ID
	 * @parm node_type 节点类型
	 * @param id_type
	 *            ID类型，0：用户id,1:设备组id,2:授权设备，默认填写0
	 * @param handler
	 */
	public static void deleteNodeInfo(final Context context,
									  final ClientCore clientCore, String node_id, int node_type,
									  int id_type, final Handler handler) {
		clientCore.deleteNodeInfo(node_id, node_type, id_type, new Handler() {

			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						handler.sendEmptyMessage(Constants.DELETE_DEV_S);
					} else {
						Log.e(WebSdkApi, " 删除设备设备失败!code=" + responseCommon.h.e);
						handler.sendEmptyMessage(Constants.DELETE_DEV_F);
					}
				} else {
					Log.e(WebSdkApi, " 删除设备设备失败! error=" + msg.what);
					handler.sendEmptyMessage(Constants.DELETE_DEV_F);
				}
				super.handleMessage(msg);
			}

		});

	}

	/**
	 * 修改设备
	 *
	 * @param pc
	 * @param node_id
	 *            节点ID
	 * @param node_name
	 *            28位 限定中文，字母，数字，下划线 名称
	 * @param dev_umid
	 *            设备umid 28位 限定字母，数字，下划线
	 * @param dev_addr
	 *            设备IP地址/域名
	 * @param dev_port
	 *            设备端口
	 * @param dev_user
	 *            设备用户名 60位 限定字母，数字，下划线
	 * @param dev_passwd
	 *            设备密码
	 * @param dev_ch_no
	 *            摄像机节点才有效， nvr/dvr的通道号
	 * @param dev_stream_no
	 *            码流
	 * @param handler
	 */
	public static void modifyNodeInfo(final Context context,
									  final ClientCore clientCore, String node_id, String node_name,
									  int node_type, int vendor_id, String dev_umid, String dev_addr,
									  int dev_port, String dev_user, String dev_passwd, int dev_ch_no,
									  int dev_stream_no, String custom_param,int update_channelname, final Handler handler) {
		clientCore.modifyNodeInfo(node_id, node_name, node_type, 0, vendor_id,
				dev_umid, dev_addr, dev_port, dev_user, dev_passwd, dev_ch_no,
				dev_stream_no, custom_param, update_channelname, new Handler() {

					@Override
					public void handleMessage(Message msg) {
						ResponseCommon responseCommon = (ResponseCommon) msg.obj;
						if (responseCommon != null && responseCommon.h != null) {
							if (responseCommon.h.e == 200) {
								handler.sendEmptyMessage(Constants.MODIFY_DEV_S);
							} else {
								Log.e(WebSdkApi, " 修改设备设备失败!code="
										+ responseCommon.h.e);
								handler.sendEmptyMessage(Constants.MODIFY_DEV_F);
							}
						} else {
							Log.e(WebSdkApi, " 修改设备设备失败! error=" + msg.what);
							handler.sendEmptyMessage(Constants.MODIFY_DEV_F);
						}
						super.handleMessage(msg);
					}
				});

	}

	/**
	 * 根据设备id查询设备在线状态
	 *
	 * @param devs
	 *            设备umid列表
	 */
	public static void getDeviceStateByUmid(ClientCore clientCore,
											List<String> devs) {
		clientCore.getDevState(devs, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ResponseDevState responseDevState = (ResponseDevState) msg.obj;
				if (responseDevState != null && responseDevState.h != null
						&& responseDevState.h.e == 200
						&& responseDevState.b != null
						&& responseDevState.b.devs != null) {
					List<DevState> devStates = responseDevState.b.devs;
					for (int i = 0; i < devStates.size(); i++) {
						Log.i("state",
								devStates.get(i).dev_id
										+ devStates.get(i).state);
					}
				} else {
					Log.i("state", "get state fail");
				}
				super.handleMessage(msg);
			}
		});
	}

	/**
	 * 查询报警记录
	 */
	public static void queryAlarmList(ClientCore clientCore) {
		clientCore.queryAlarmList(new Handler() {

			@Override
			public void handleMessage(Message msg) {
				ResponseQueryAlarm responseQueryAlarm = (ResponseQueryAlarm) msg.obj;
				if (responseQueryAlarm != null && responseQueryAlarm.h != null) {
					if (responseQueryAlarm.h.e == 200) {
						if (responseQueryAlarm.b != null
								&& responseQueryAlarm.b.alarms != null) {
							for (int i = 0; i < responseQueryAlarm.b.alarms.length; i++) {
								Log.i("alarms", responseQueryAlarm.b.alarms[i]
										.toString());
							}
							Log.e(WebSdkApi, "查询报警成果");
						} else {
							Log.e(WebSdkApi, "其它错误");
						}

					} else {
						Log.e(WebSdkApi, " 查询报警失败!code="
								+ responseQueryAlarm.h.e);
					}
				} else {
					Log.e(WebSdkApi, " 查询报警失败! error=" + msg.what);
				}
				super.handleMessage(msg);
			}

		});
	}

	/**
	 * 删除所有报警记录
	 */

	public static void deleteAllAlarm(ClientCore clientCore) {
		clientCore.deleteAllAlarm(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Log.i(WebSdkApi, "删除报警信息成功！");
					} else {
						Log.e(WebSdkApi, "删除所有报警信息失败!code="
								+ responseCommon.h.e);
					}
				} else {
					Log.e(WebSdkApi, "删除所有报警信息失败! error=" + msg.what);
				}
				super.handleMessage(msg);
			}
		});
	}

	/**
	 * 删除指定id报警记录
	 *
	 * @param alarms_ids
	 *            alarmid 数组
	 */

	public static void deleteAlarmByIds(ClientCore clientCore,
										String[] alarm_ids) {
		clientCore.deleteAlarm(alarm_ids, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Log.i(WebSdkApi, "删除报警信息成功！");
					} else {
						Log.e(WebSdkApi, "删除所有报警信息失败!code="
								+ responseCommon.h.e);
					}
				} else {
					Log.e(WebSdkApi, "删除所有报警信息失败! error=" + msg.what);
				}
				super.handleMessage(msg);
			}
		});
	}

	/**
	 * 根据客户端定制标识获取服务器列表
	 *
	 * @param pc
	 */
	public static void getServerList(final Context context,
									 final ClientCore clientCore) {
		clientCore.getServerList(new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				final ResponseGetServerList responseGetServerList = (ResponseGetServerList) msg.obj;
				if (responseGetServerList != null
						&& responseGetServerList.h != null) {
					if (responseGetServerList.h.e == 200
							&& responseGetServerList.b.srvs != null) {
						ServerListInfo serverListInfos[] = responseGetServerList.b.srvs;
						for (int i = 0; i < serverListInfos.length; i++) {
							Log.d("ServerListInfo",
									"" + serverListInfos[i].toString());
						}
					} else {
						Log.e(WebSdkApi, "获取服务器列表失败! code="
								+ responseGetServerList.h.e);
					}
				} else {
					Log.e(WebSdkApi, "获取服务器列表失败! error=" + msg.what);
				}

				super.handleMessage(msg);
			}

		});
	}

	/**
	 * 修改设备通道号
	 *
	 * @param node_id
	 *            节点id
	 * @param dev_id
	 *            设备id，playNode.node.sDevId 或者 DevItemInfo.dev_id
	 * @param dev_ch_num
	 *            设备通道数
	 */

	public static void modifyDevNum(ClientCore clientCore, String node_id,
									String dev_id, int dev_ch_num, final Handler handler) {
		clientCore.modifyDevNum(node_id, dev_id, dev_ch_num, new Handler() {

			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						handler.sendEmptyMessage(Constants.MODIFY_DEV_NUM_S);
					} else {
						Log.e(WebSdkApi, " 修改设备通道数失败!code="
								+ responseCommon.h.e);
						handler.sendEmptyMessage(Constants.MODIFY_DEV_NUM_F);
					}
				} else {
					Log.e(WebSdkApi, " 修改设备通道数! error=" + msg.what);
					handler.sendEmptyMessage(Constants.MODIFY_DEV_NUM_F);
				}
				super.handleMessage(msg);
			}
		});
	}

	/**
	 * 设置用户推送
	 *
	 * @param enable_push
	 *            ，启用或禁用推送，1 表示启用，0 表示禁用，是必须的，
	 * @param client_lang
	 *            客户端语言，参见语言宏定义
	 * @param client_token
	 *            个推给出的clientId
	 * @param disable_push_other_users
	 *            是否禁止向相同客户端ID 或客户端Token的其它用户进行推送，1表示禁止，0表示允许；
	 *
	 * @param unread_count
	 *            把推送的未读记录数设置为指定的值。默认置0
	 */
	public static void setUserPush(ClientCore clientCore, int enable_push,
								   int client_lang, String client_token, int disable_push_other_users,
								   int unread_count) {
		clientCore.setUserPush(enable_push, client_lang, client_token,
				disable_push_other_users, unread_count, "", "", "getui", new Handler() {
					@Override
					public void handleMessage(Message msg) {
						ResponseCommon responseCommon = (ResponseCommon) msg.obj;
						if (responseCommon != null && responseCommon.h != null
								&& responseCommon.h.e == 200) {
							Log.i("setUserPush", "设置用户推送成功");
						} else {
							Log.i("setUserPush", "设置用户推送失败");
						}
					}
				});
	}

	/**
	 * 设备端服务器进行布防 （主要用于免登陆模式下服务器布防）
	 *
	 * @param opCode
	 *            操作码，1 表示布防/布防通知，2 表示撤防，3 表示取消布防通知
	 * @param client_token
	 *            个推clientId
	 * @param alarm_events
	 *            布防事件类型数组，参考
	 * @param devName
	 *            设备名称
	 * @param devUmid
	 *            设备umid
	 * @param devUser
	 *            设备用户名
	 * @param devPassword
	 *            设备密码
	 * @param iChNo
	 *            设备通道号
	 */

	public static void setDeviceAlarm(ClientCore clientCore, final int opCode,
									  String client_token, int[] alarm_events, String devName,
									  String devUmid, String devUser, String devPassword, int iChNo) {
		P2pConnectInfo p2pConnectInfo = createConnectInfo1(clientCore, devName,
				devUmid, devUser, devPassword, iChNo);
		P2pConnectInfo[] p2pConnectInfos = { p2pConnectInfo };
		clientCore.alarmSettings(p2pConnectInfos);
		clientCore.alarmSettings(opCode, client_token, alarm_events,
				new Handler() {
					@Override
					public void handleMessage(Message msg) {
						ResponseCommon commonSocketText = (ResponseCommon) msg.obj;
						if (commonSocketText != null
								&& commonSocketText.h.e == 200) {
							if (opCode == 1) {
								Log.i(WebSdkApi, "布防成功");
							} else if (opCode == 2) {
								Log.i(WebSdkApi, "撤防成功");
							}
						} else {
							if (opCode == 1) {
								Log.i(WebSdkApi, "布防失败");
							} else if (opCode == 2) {
								Log.i(WebSdkApi, "撤防失败");
							}
						}
					}
				}, "");

	}

	/**
	 * 设备端服务器进行布防(主用于登录模式下进行服务器布放)
	 *
	 * @param playNode
	 *            播放节点数据
	 * @param opCode
	 *            为1时布防 为2时撤防 为4撤销所有设备布防
	 * @param client_token
	 *            个推的client_id
	 * @param alarm_events
	 *            报警事件类型，参见报警类型宏定义
	 */
	public static void setDeviceAlarm(ClientCore clientCore, PlayNode node,
									  final int opCode, String client_token, int[] alarm_events) {
		P2pConnectInfo p2pConnectInfo = createConnectInfo(clientCore, node);
		P2pConnectInfo[] p2pConnectInfos = { p2pConnectInfo };
		clientCore.alarmSettings(p2pConnectInfos);// 免登陆需提供设备参数
		clientCore.alarmSettings(opCode, client_token, alarm_events,
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						ResponseCommon commonSocketText = (ResponseCommon) msg.obj;
						if (commonSocketText != null
								&& commonSocketText.h.e == 200) {
							if (opCode == 1) {
								Log.i(WebSdkApi, "布防成功");
							} else if (opCode == 2) {
								Log.i(WebSdkApi, "撤防成功");
							}
						} else {
							if (opCode == 1) {
								Log.i(WebSdkApi, "布防失败");
							} else if (opCode == 2) {
								Log.i(WebSdkApi, "撤防失败");
							}
						}
					}
				}, node.node.sDevId);
	}

	/**
	 * 查询设备布放状态
	 *
	 * @param sDevId
	 *            设备id playNode.node.sdevId 或者 devItemInfo.dev_id
	 * @param client_token
	 *            个推clientId
	 */
	public static void getDeviceAlarm(ClientCore clientCore, String sDevId,
									  final String client_token) {
		clientCore.queryAlarmSettings(sDevId, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				ResponseQueryAlarmSettings responseQueryAlarmSettings = (ResponseQueryAlarmSettings) msg.obj;
				if (responseQueryAlarmSettings != null
						&& responseQueryAlarmSettings.h != null) {
					if (responseQueryAlarmSettings.h.e == 200
							&& responseQueryAlarmSettings.b != null
							&& responseQueryAlarmSettings.b.devs != null
							&& responseQueryAlarmSettings.b.devs.length > 0) {
						TAlarmSetInfor alarmInfo = TAlarmSetInfor
								.toTAlarmSetInfor(responseQueryAlarmSettings.b.devs[0]);
						boolean isNotifyToPhone = false;
						if (alarmInfo.bIfSetAlarm == 1) {
							if (alarmInfo != null) {
								if (alarmInfo != null
										&& alarmInfo.notifies != null) {
									for (int i = 0; i < alarmInfo.notifies.length; i++) {
										Log.w("alarmInfo",
												alarmInfo.notifies[i].notify_type
														+ ","
														+ alarmInfo.notifies[i].notify_param);
										if (alarmInfo.notifies[i].notify_type == 1
												&& client_token
												.equals(alarmInfo.notifies[i].notify_param)) {
											isNotifyToPhone = true;
											break;
										}
									}
								}
							}
						} else {
							isNotifyToPhone = false;
						}
						if (isNotifyToPhone) {
							Log.e(WebSdkApi, "设备已设置设置推送");
						} else {
							Log.e(WebSdkApi, "设备没有设置推送");
						}
					} else {
						Log.e("queryAlarmSettings", "查询报警布防失败!code="
								+ responseQueryAlarmSettings.h.e);
					}
				} else {
					Log.e("queryAlarmSettings", "查询报警布防失败! error=" + msg.what);
				}
				super.handleMessage(msg);
			}
		});
	}

	/**
	 * umid直连 设备通道参数
	 *
	 * @param clientCore
	 * @param node
	 * @return
	 */
	public static P2pConnectInfo createConnectInfo1(ClientCore clientCore,
													String devName, String devUmid, String devUser, String devPassword,
													int iChNo) {
		P2pConnectInfo p2pConnectInfo = new P2pConnectInfo();

		p2pConnectInfo = new P2pConnectInfo();
		p2pConnectInfo.umid = devUmid;
		p2pConnectInfo.user = devUser;
		p2pConnectInfo.passwd = devPassword;
		// 推送设备名称由umid 和通道号组成，可自定义
		p2pConnectInfo.dev_name = devName;
		String sDevId = clientCore.encryptDevId("", devUmid, iChNo);
		p2pConnectInfo.dev_id = sDevId;
		p2pConnectInfo.channel = iChNo;
		return p2pConnectInfo;
	}

	/**
	 * 免登录报警 设备通道参数
	 *
	 * @param clientCore
	 * @param node
	 * @return
	 */
	public static P2pConnectInfo createConnectInfo(ClientCore clientCore,
												   PlayNode node) {
		P2pConnectInfo p2pConnectInfo = new P2pConnectInfo();
		if (node != null) {
			// 解析连接参数
			TDevNodeInfor info = TDevNodeInfor.changeToTDevNodeInfor(
					node.getDeviceId(), node.node.iConnMode);
			if (info != null) {
				p2pConnectInfo = new P2pConnectInfo();
				p2pConnectInfo.umid = info.pDevId;
				p2pConnectInfo.user = info.pDevUser;
				p2pConnectInfo.passwd = info.pDevPwd;
				p2pConnectInfo.dev_name = node.getName();

				/**
				 * 免登陆模式下 node.node.sDevId是 String sDevId
				 * =clientCore.encryptDevId
				 * (String.valueOf(node.node.dwNodeId),info.pDevId, info.iChNo);
				 */
				String sDevId = node.node.sDevId;
				p2pConnectInfo.dev_id = sDevId;
				p2pConnectInfo.channel = info.iChNo;
			}
		}
		return p2pConnectInfo;

	}
}