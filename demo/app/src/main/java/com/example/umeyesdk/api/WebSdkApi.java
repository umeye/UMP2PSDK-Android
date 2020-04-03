package com.example.umeyesdk.api;

import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.Source.TAlarmSetInfor;
import com.Player.Source.TDevNodeInfor;
import com.Player.web.request.P2pConnectInfo;
import com.Player.web.response.DevState;
import com.Player.web.response.ResponseAddNode;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseDevList;
import com.Player.web.response.ResponseDevShareInfo;
import com.Player.web.response.ResponseDevState;
import com.Player.web.response.ResponseGetServerList;
import com.Player.web.response.ResponseQueryAlarm;
import com.Player.web.response.ResponseQueryAlarmSettings;
import com.Player.web.response.ResponseRefreshSession;
import com.Player.web.response.ResponseResetPwd;
import com.Player.web.response.ServerListInfo;
import com.Player.web.websocket.ClientCore;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;

public class WebSdkApi {
	public static final String WebSdkApi = "WebSdkApi";


	/**
	 * 无证书
	 */
	public static void setHttps() {
		ClientCore.setHttps();
	}

	/**
	 *
	 * @param certInputStream
	 *         证书文件的输入流
	 *         如：
	 *         InputStream certInputStream = new BufferedInputStream(getAssets().open("test.crt"));
	 */
	public static void setHttps(InputStream certInputStream) {
		ClientCore.setHttps(certInputStream);
	}

   /**
   * 错误码定义： 具体请求错误码定义参考HttpErrorCode
   */

	/**
	 * 登陆
	 * @param areaCode
	 *            区号(当userName为手机号时，areaCode必填)
	 * @param userName
	 *            用户ID 必填 24位 , 限定字母，数字，下划线 必填 若是手机号，为区号（如86）+号码）
	 * @param password
	 *            必填 密码 必填 20位 , 限定字母，数字，下划线
	 **/
	public static void loginServerAtUserId(final Context context,
										   final ClientCore clientCore, String areaCode, String userName, String password,
										   final Handler handler) {
		clientCore.loginServerAtUserId(context, areaCode, userName, password,
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
	 *@brief 发送短信 （SendSMS）
	 *
	 * @param type 类型，1：找回密码，2：注册， 3：激活通知，4：服务器运维 ，5：报警信息
	 * @param phone_num 接收推送短信的手机号码
	 * @param phone_areacode 国家手机编码，如86
	 */
	public static void sendSMS(final ClientCore clientCore, int type, String phone_num, String phone_areacode, final Handler handler) {
		clientCore.sendSMS(type,phone_num,phone_areacode,new Handler(){

			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						handler.sendEmptyMessage(Constants.SEND_SMS_S);
					} else {
						Log.e(WebSdkApi, "发送验证码失败!code="
								+ responseCommon.h.e);
						handler.sendEmptyMessage(Constants.SEND_SMS_F);
					}
				} else {
					Log.e(WebSdkApi, "发送验证码失败! error=" + msg.what);
					handler.sendEmptyMessage(Constants.SEND_SMS_F);
				}
				super.handleMessage(msg);
			}
		});
	}


	/**
	 *
	 * @param email  接收验证码的邮箱
	 * @param send_lang 英文不传或者传1 简体中文传2
	 * @param type 邮箱验证码类型，1：邮箱注册用户验证码、2：邮箱修改密码验证码
	 */
	public static void sendEmailCode(final ClientCore clientCore, String email, int send_lang, int type, final Handler handler) {
		clientCore.sendEmailCode(email,send_lang,type,new Handler(){

			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						handler.sendEmptyMessage(Constants.SEND_EMAIL_CODE_S);
					} else {
						Log.e(WebSdkApi, "发送验证码失败!code="
								+ responseCommon.h.e);
						handler.sendEmptyMessage(Constants.SEND_EMAIL_CODE_F);
					}
				} else {
					Log.e(WebSdkApi, "发送验证码失败! error=" + msg.what);
					handler.sendEmptyMessage(Constants.SEND_EMAIL_CODE_F);
				}
				super.handleMessage(msg);
			}
		});
	}






	/**
	 * 注册账户
	 *
	 * @param aUserId
	 *            用户ID 必填 24位 , 限定字母，数字，下划线 若是手机号，为区号（如86）+号码）
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
	 * 给邮箱激活注册或发送注册成功通知邮件用
	 *
	 *
	 * @brief 注册 （Register）
	 *
	 * @param aUserId
	 *            用户id，必填（Necessary）
	 * @param aPassword
	 *            用户密码，必填（Necessary）
	 * @param user_email
	 *            邮箱，必填（Necessary）
	 * @param nickName
	 *            昵称，选填（Unnecessary）
	 * @param user_phone
	 *            电话，选填（Unnecessary）
	 * @param user_id_card
	 *            身份证，选填（Unnecessary）
	 * @param email_active
	 *            是否需要邮箱激活，默认为0:不需要邮箱激活，1:需要邮箱激活,2:需要发送注册成功通知邮件，3:需要手机验证码验证
	 *  @param client_lang
	 * 	        邮箱链接语言类型
	 * @return
	 */
	public static void registeredUser(final Context context,
							   final ClientCore clientCore, String aUserId, String aPassword,
							   String user_email, String nickName, String user_phone,
							   String user_id_card, int email_active, int client_lang) {
		clientCore.registeredUser(aUserId, aPassword, user_email, nickName,
				user_phone, user_id_card, email_active, client_lang, new Handler() {

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
	 * 手机号注册
	 *
	 *
	 *  @brief 注册 （Register）
	 *
	 * @param phone_areacode
	 *        区号，必填
	 * @param phone
	 *        手机号，必填
	 * @param aPassword
	 *            用户密码，必填（Necessary）
	 * @param user_email
	 *            邮箱，必填（Necessary）
	 * @param nickName
	 *            昵称，选填（Unnecessary）
	 * @param user_id_card
	 *            身份证，选填（Unnecessary）
	 * @param code
	 *            手机号验证码
	 */
	public static void registeredPhone(final Context context, ClientCore clientCore, String phone_areacode, String phone, String aPassword,
									  String user_email, String nickName,
									  String user_id_card, String code) {
		clientCore.registeredPhone(phone_areacode, phone, aPassword, user_email, nickName,
				user_id_card, code, new Handler() {

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
	 * 邮箱注册
	 *
	 * @param aUserId
	 *            用户ID 必填 24位 , 邮箱
	 * @param aPassword
	 *            密码 必填 20位 , 限定字母，数字，下划线
	 * @param nickName
	 *            昵称 32位 , 限定英文，数字，下划线
	 * @param user_phone
	 *            手机号码 32位 , 限定数字
	 * @param user_id_card
	 *            用户身份证id 24位
	 * @param code
	 *            邮箱验证码
	 */
	public static void registeredEmail(final Context context, ClientCore clientCore, String aUserId, String user_phone, String aPassword,
									  String nickName, String user_id_card, String code) {
		clientCore.registeredEmail(aUserId, aPassword, nickName,
				user_phone, user_id_card, code, new Handler() {

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
	 * 登出
	 *
	 * @param disableAlarm
	 *            是否取消报警推送
	 */
	public static void logoutServer(final ClientCore clientCore,
									int disableAlarm) {
		clientCore.logoutServer(disableAlarm, new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Log.e(WebSdkApi, "登出成功!code=" + responseCommon.h.e);
					} else {
						Log.e(WebSdkApi, "登出失败!code=" + responseCommon.h.e);
					}

				} else {
					Log.e(WebSdkApi, "登出失败! error=" + msg.what);
				}
				super.handleMessage(msg);
			}
		});
	}





	/**
	 * 注销账号
	 *
	 */
	public static void logoutUserAccount(final ClientCore clientCore) {
		clientCore.logoutUserAccount(new Handler() {
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
	 * 刷新session
	 *
	 * @param handler
	 */
	public static void refreshSession(final ClientCore clientCore, final Handler handler) {
		clientCore.refreshSession(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ResponseRefreshSession responseRefreshSession = (ResponseRefreshSession) msg.obj;
				if (responseRefreshSession != null && responseRefreshSession.h != null) {
					if (responseRefreshSession.h.e == 200) {
						handler.sendMessage(Message.obtain(handler,
								Constants.REFRESH_SESSION_S,
								responseRefreshSession));
					} else {
						Log.e(WebSdkApi, "刷新session失败!code=" + responseRefreshSession.h.e);
						handler.sendEmptyMessage(Constants.REFRESH_SESSION_F);
					}

				} else {
					Log.e(WebSdkApi, "刷新session失败! error=" + msg.what);
					handler.sendEmptyMessage(Constants.REFRESH_SESSION_F);
				}
				super.handleMessage(msg);
			}
		});
	}






	/**
	 *账号修改密码
	 *
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
	 * 邮箱/手机号发送短信修改密码
	 *
	 * @param userName 用户名
	 * @param oldPassword 旧密码，20位 , 限定字母，数字，下划线
	 * @param ver_code 验证码
	 * @param newPassword 新密码，20位 , 限定字母，数字，下划线
	 * @param ver_type
	 *            验证码方式类型 ：0：原密码验证；1 短信修改密码验证码验证 2邮箱修改密码验证码验证
	 */
	public static void modifyUserPassword(final Context context,
								   final ClientCore clientCore, String userName, String oldPassword,String ver_code,
								   String newPassword, int ver_type) {
		clientCore.modifyUserPassword(userName, oldPassword, ver_code, newPassword, ver_type, new Handler() {

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
				ResponseResetPwd responseResetPwd = (ResponseResetPwd) msg.obj;
				if (responseResetPwd != null && responseResetPwd.h != null) {
					if (responseResetPwd.h.e == 200) {
						Show.toast(context, "发送重置密码成功，稍后请查收！");
					} else {
						Log.e(WebSdkApi, "发送重置密码失败!code=" + responseResetPwd.h.e);
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
	 * 获取设备列表
	 *
	 *
	 * @param isLocalList
	 * 	 *        是不是本地设备列表
	 * @param parent_node_id
	 *            父节点ID
	 * @param page_index
	 *            分页功能，指定从第几页开始，是可选的，默认不分页；
	 * @param page_size
	 *            分页功能，每页返回的记录数，是可选的，默认不分页；
	 * @param handler
	 */
	public static void getNodeList(final Context context,
								   final ClientCore clientCore, boolean isLocalList, final String parent_node_id,
								   int page_index, int page_size, final Handler handler) {
		clientCore.getNodeList(isLocalList, parent_node_id, page_index, page_size,
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
	 *  查询设备分享信息
	 *
	 * @param dev_id
	 *             设备id
	 * @param handler
	 */
	public static void getDevShareInfo(final ClientCore clientCore, String dev_id, final Handler handler) {
		clientCore.getDevShareInfo(dev_id, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				ResponseDevShareInfo responseDevShareInfo = (ResponseDevShareInfo) msg.obj;
				if (responseDevShareInfo != null
						&& responseDevShareInfo.h != null) {
					if (responseDevShareInfo.h.e == 200) {
						handler.sendMessage(Message.obtain(handler,
								Constants.GET_SHARE_INFO_S,
								responseDevShareInfo));
					} else {
						Log.e(WebSdkApi, "获取分享信息失败!code="
								+ responseDevShareInfo.h.e);
						handler.sendEmptyMessage(Constants.GET_SHARE_INFO_F);
					}
				} else {
					Log.e(WebSdkApi, "获取分享信息失败! error=" + msg.what);
					handler.sendEmptyMessage(Constants.GET_SHARE_INFO_F);
				}
				super.handleMessage(msg);
			}
		});
	}


	/**
	 *
	 * @param opcode 	 			操作码，1：新增，2：删除
	 * @param share_type 	 			分享类型，0表示由系统分享，1表示用户分享
	 * @param node_ids 	 			分享的设备节点ID数组
	 *  @param to_userid 	 			分享的目标用户id，opcode是1时是必须的
	 * @param dev_rights 	 			设备的权限数组，opcode是1时是必须的
	 */
	public static void addShareDevice(final ClientCore clientCore, final Handler handler, final int opcode, int share_type, List<String> node_ids, String to_userid, List<Integer> dev_rights) {
		clientCore.addShareDevice(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null
						&& responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						if(opcode == 1) {
							handler.sendMessage(Message.obtain(handler,
									Constants.ADD_SHARE_S,
									responseCommon));
						}else {
							handler.sendMessage(Message.obtain(handler,
									Constants.DELETE_SHARE_S,
									responseCommon));
						}

					} else {
						if(opcode == 1) {
							handler.sendEmptyMessage(Constants.ADD_SHARE_F);
						} else {
							handler.sendEmptyMessage(Constants.DELETE_SHARE_F);
						}
					}
				} else {
					if(opcode == 1) {
						handler.sendEmptyMessage(Constants.ADD_SHARE_F);
					} else {
						handler.sendEmptyMessage(Constants.DELETE_SHARE_F);
					}
				}
				super.handleMessage(msg);
			}
		},opcode, share_type, node_ids, to_userid, dev_rights);
	}






	/**
	 *  修改设备节点顺序号
	 *
	 * @param node_id
	 *              结点ID
	 * @param node_order
	 *              结点顺序号
	 */
	public static void modifyDevNodeOrder(final Context context, final ClientCore clientCore, String node_id, int node_order) {
		clientCore.modifyDevNodeOrder(node_id, node_order, new Handler(){
			@Override
			public void handleMessage(Message msg) {
				ResponseCommon responseCommon = (ResponseCommon) msg.obj;
				if (responseCommon != null && responseCommon.h != null) {
					if (responseCommon.h.e == 200) {
						Show.toast(context, "修改设备节点顺序成功！");
					} else {
						Log.e(WebSdkApi, "修改设备节点顺序失败!code=" + responseCommon.h.e);
						Show.toast(context, "修改设备节点顺序失败");
					}
				} else {
					Log.e(WebSdkApi, "修改设备节点顺序失败! error=" + msg.what);
					Show.toast(context, "修改设备节点顺序失败");
				}
				super.handleMessage(msg);
			}
		});
	}







	/**
	 * 添加设备节点
	 *
	 * @param node_name
	 *            长度 28 限定中文，字母，数字，下划线 名称
	 * @param parent_node_id
	 *            父节点ID, 当没有父节点可以传空字符串
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
	 * @param only_umid
	 *            设备id是不是唯一用户添加
	 * @param limit_appid
	 *            是否只允许本APP厂家的设备才能添加
	 * @param custom_param
	 *            自定义参数
	 * @param handler
	 */
	public static void addNodeInfo(final Context context,
								   final ClientCore clientCore, String node_name, String parent_node_id,
								   int node_type, int conn_mode, int vendor_id, String dev_umid,
								   String dev_addr, int dev_port, String dev_user, String dev_passwd,
								   int dev_ch_num, int dev_ch_no, int dev_stream_no, int only_umid,int limit_appid,String custom_param,
								   final Handler handler) {
		clientCore.addNodeInfo(node_name, parent_node_id, node_type, conn_mode,
				vendor_id, dev_umid, dev_addr, dev_port, dev_user, dev_passwd,
				dev_ch_num, dev_ch_no, dev_stream_no, only_umid, limit_appid, custom_param, new Handler() {

					@Override
					public void handleMessage(Message msg) {
						ResponseAddNode responseAddNode = (ResponseAddNode) msg.obj;
						if (responseAddNode != null && responseAddNode.h != null) {
							if (responseAddNode.h.e == 200) {
								handler.sendEmptyMessage(Constants.ADD_DEV_S);
							} else {
								Log.e(WebSdkApi, "添加设备失败!code="	+ responseAddNode.h.e);
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
	 * 删除多个设备
	 *
	 * @param node_ids
	 *            节点ID数组
	 * @parm node_type 节点类型
	 * @param id_type
	 *            ID类型，0：用户id,1:设备组id,2:授权设备，默认填写0
	 * @param handler
	 */
	public static void deleteNodeInfos(final ClientCore clientCore, String[] node_ids, int node_type, int id_type, final Handler handler) {
		clientCore.deleteNodeInfos(node_ids, node_type, id_type, new Handler() {

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
	 *  @param custom_param
	 *           自定义厂家参数
	 * @update_channelname
	 *          是否需要同步更新设备通道名称,默认1
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
	 * @param alarm_ids
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
	 *            第三方推送如个推给出的clientId
	 * @param disable_push_other_users
	 *            是否禁止向相同客户端ID 或客户端Token的其它用户进行推送，1表示禁止，0表示允许；
	 *
	 * @param unread_count
	 *            把推送的未读记录数设置为指定的值。默认置0
	 *
	 *  @param enable_wechat_push
	 *           是否开启微信推送
	 */
	public static void setUserPush(ClientCore clientCore, int enable_push,
								   int client_lang, String client_token, int disable_push_other_users, int enable_wechat_push,
								   int unread_count) {
		clientCore.setUserPush(enable_push, client_lang, client_token,
				disable_push_other_users, unread_count, enable_wechat_push, new Handler() {
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
	 * @param enable_push， 启用或禁用推送，1 表示启用，0 表示禁用，是必须的，
	 * @param client_lang   客户端语言，参见语言宏定义
	 * @param client_token  个推给出的clientId
	 * @param disable_push_other_users   是否禁止向相同客户端ID 或客户端Token的其它用户进行推送，1表示禁止，0表示允许；
	 * @param unread_count  把推送的未读记录数设置为指定的值。默认置0
	 * @param param_type
	 *            额外推送的参数类型，是必须的，传空表示默认使用透传，有url，intent 等（按个推文档增 加）；
	 * @param param_content
	 *            额外推送的参数内容，是必须的，跟据类型的变化，例如：当param_type传intent 类型参数时，固定
	 *            下面的参数“intent
	 *            :#Intent;launchFlags=0x10000000;package=com.pp.yl;component
	 *            ={包名
	 *            }/com.quvii.bell.activity.LoadingActivity;i.DevChNo={DevChNo
	 *            };s.DevUmid={DevUmid};i.AlarmEvent={AlarmEvent};end”，
	 *            有其他的参数按规定拼装；当param_type传的是url 类型参数时，参数内容为：http://www.xxx.com。
	 * @param platform_flag
	 *            推送平台(取值为以下其中之一)
	 *            getui xg fcm
	 * @param enable_wechat_push
	 *         是否开启微信推送
	 *
	 */
	public static void setUserPush(ClientCore clientCore,int enable_push, int client_lang,
								   String client_token, int disable_push_other_users,int unread_count,String param_type, String param_content,String platform_flag, int enable_wechat_push) {
		clientCore.setUserPush(enable_push, client_lang, client_token,
				disable_push_other_users, unread_count, param_type, param_content, platform_flag, enable_wechat_push,new Handler() {
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
     * 1 设备故障
     * 2 移动侦测
     * 3 视频遮挡
     * 4 视频丢失
     * 5 探头报警
     * 6 人体感应
     *  ...
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
	 * @param node
	 *            播放节点数据
	 * @param opCode
	 *            为1时布防 为2时撤防 为4撤销所有设备布防
	 * @param client_token
	 *            个推的client_id
	 * @param alarm_events
	 *            报警事件类型，参见报警类型宏定义
     * 1 设备故障
     * 2 移动侦测
     * 3 视频遮挡
     * 4 视频丢失
     * 5 探头报警
     * 6 人体感应
     *  ...
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
	 *            第三方推送如个推clientId
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
						if (alarmInfo.bIfSetAlarm == 1) {//1是已布防
							if (alarmInfo != null) {
								if (alarmInfo != null
										&& alarmInfo.notifies != null) {
									for (int i = 0; i < alarmInfo.notifies.length; i++) {
										Log.w("alarmInfo",
												alarmInfo.notifies[i].notify_type
														+ ","
														+ alarmInfo.notifies[i].notify_param);
										if (alarmInfo.notifies[i].notify_type == 1//通知类型，1：手机推送
												&& client_token
												.equals(alarmInfo.notifies[i].notify_param)) {//第三方推送id跟布防时候传的id是否相同
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
							Log.e(WebSdkApi, "设备已设置推送");
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
	 * @param devName
	 * @param devUmid
	 * @param devUser
	 * @param devPassword
	 * @param iChNo
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
				p2pConnectInfo.umid = info.pDevId;//umid
				p2pConnectInfo.user = info.pDevUser;//用户名
				p2pConnectInfo.passwd = info.pDevPwd;//密码
				p2pConnectInfo.dev_name = node.getName();//设备名

				/**
				 * 免登陆模式下 node.node.sDevId是 String sDevId
				 * =clientCore.encryptDevId
				 * (String.valueOf(node.node.dwNodeId),info.pDevId, info.iChNo);
				 */
				String sDevId = node.node.sDevId;
				p2pConnectInfo.dev_id = sDevId;//设备ID
				p2pConnectInfo.channel = info.iChNo;//通道号
			}
		}
		return p2pConnectInfo;

	}
}