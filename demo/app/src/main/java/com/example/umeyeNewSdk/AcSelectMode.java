package com.example.umeyeNewSdk;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Player.Source.LogLisenter;
import com.Player.Source.LogOut;
import com.Player.web.response.DevItemInfo;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseDevList;
import com.Player.web.response.ResponseQueryUserInfo;
import com.Player.web.response.UserInfo;
import com.Player.web.websocket.ClientCore;
import com.Player.web.websocket.IoTTokenInvalidListener;
import com.example.umeyesdk.utils.Errors;
import com.getui.demo.AlarmUtils;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.MainActivity;
import com.example.umeyesdk.R;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.Utility;

public class AcSelectMode extends Activity {
	public ClientCore clientCore;
	AppMain appMain;
	private Context context;
	public List<PlayNode> nodeList = new ArrayList<PlayNode>();
	private EditText editServer;
	private EditText editUser;
	private EditText editPassword;
	/**
	 * 用Handler来更新UI
	 */
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case Constants.LOGIN:

					break;

				case Constants.LOGIN_OK:
					Show.toast(AcSelectMode.this, R.string.login_success);
					String[] server = clientCore.getCurrentServer();

					Log.d("getCurrentServer", "认证服务器:" + server[1]);




					// 启动用户报警推送
					if (!TextUtils.isEmpty(AlarmUtils.GETUI_CID)) {
						WebSdkApi.setUserPush(clientCore, 1,
								Utility.isZh(context) ? 2 : 1,
								AlarmUtils.GETUI_CID, 1, 0,0);
					}
//					WebSdkApi.getNodeList(AcSelectMode.this, clientCore, "", 0, 0,
//							this);

//					ClientCore.getInstance().queryUserInfo(AcSelectMode.this,
//							"yin", new Handler() {
//
//								@Override
//								public void handleMessage(Message msg) {
//									// TODO Auto-generated method stub
//									ResponseQueryUserInfo responseQueryUserInfo = (ResponseQueryUserInfo) msg.obj;
//									if (responseQueryUserInfo != null
//											&& responseQueryUserInfo.b != null) {
//										Log.i("ResponseQueryUserInfo",
//												responseQueryUserInfo.b
//														.toJsonString());
//
//									}
//									super.handleMessage(msg);
//								}
//							});

					startActivity(new Intent(AcSelectMode.this, MainActivity.class));
					finish();



					break;
				case Constants.LOGIN_USER_ERROR:
					Show.toast(AcSelectMode.this, "用户名错误");
					break;
				case Constants.LOGIN_PWD_ERROR:
					Show.toast(AcSelectMode.this, "密码错误");
					break;
				case Constants.GET_DEVLIST_F:
					Show.toast(AcSelectMode.this, "获取设备失败");
					break;
				case Constants.GET_DEVLIST_S:
					nodeList.clear();
					ResponseDevList responseDevList = (ResponseDevList) msg.obj;
					List<DevItemInfo> items = responseDevList.b.nodes;
					for (int i = 0; i < items.size(); i++) {
						DevItemInfo devItemInfo = items.get(i);
						if (devItemInfo != null) {
							PlayNode node = PlayNode.ChangeData(devItemInfo);
							nodeList.add(node);
						}
					}
					appMain.setNodeList(nodeList);
					Show.toast(AcSelectMode.this, "获取设备成功");
					startActivity(new Intent(AcSelectMode.this, MainActivity.class));
					finish();
					break;
				case Constants.LOGIN_FAILED:

					Show.toast(AcSelectMode.this, "登陆失败");
					break;
				case Constants.LUNCH_FAILED:

					break;
				case Constants.GET_DEVLIST_OK_NO_DATA:
					Show.toast(context, R.string.login_succeed_no_data);
					break;

				default:
					break;
			}

		}
	};
	private TextView textLog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.ac_select_mod);
		context = this;
		clientCore = ClientCore.getInstance();
		appMain = (AppMain) getApplication();
		editUser = (EditText) findViewById(R.id.et_user);
		editServer = (EditText) findViewById(R.id.et_server);
		editPassword = (EditText) findViewById(R.id.et_password);
		UserInfo userInfo = UserInfo.getUserInfo(this);
		if(userInfo != null && userInfo.getLoginType() == 0) {//如果上次不是微信登录才显示
			Constants.Login_user = userInfo.getFullName();
		} else {

		}
		editUser.setText(Constants.Login_user);
//		editPassword.setText(Constants.Login_password);
		editServer.setText(Constants.server);
		textLog = (TextView) findViewById(R.id.log_text);
		LogOut.logLisenter = new LogLisenter() {

			@Override
			public void OnLogLisenter(final int logColor, final String tag,
									  final String message) {
				// TODO Auto-generated method stub
				textLog.post(new Runnable() {

					@Override
					public void run() {
						SpannableStringBuilder spantag = new SpannableStringBuilder(
								"<--" + tag + "-->" + "\n" + message + "\n");
						// TODO Auto-generated method stub
						spantag.setSpan(new ForegroundColorSpan(logColor), 0,
								spantag.length(),
								Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						textLog.append(spantag);

					}
				});
			}
		};
		super.onCreate(savedInstanceState);
	}

	/**
	 *
	 * 清除日志
	 *
	 * @param v
	 */
	public void clearLog(View v) {
		textLog.setText("");
	}

	/**
	 * 修改服务器
	 *
	 * @param v
	 */
	public void modifyServer(View v) {
		String server = editServer.getText().toString();
		if (TextUtils.isEmpty(server)) {
			return;
		}
		if (server.equals(Constants.server)) {
			return;
		}
		Constants.server = server;
		ClientCore clientCore = ClientCore.getInstance();
		clientCore.RelaseClient();
		clientCore.setCurrentBestServer("",0,"", 0,"",0,"",0,false);
		int language = Utility.isZh(this) ? 2 : 1;
		clientCore.setupHost(Constants.server, 0, Utility.getImsi(this),
				language, Constants.custom_flag,
				String.valueOf(Utility.GetVersionCode(this)), "", "");
		clientCore.getCurrentBestServer(new Handler());
		SharedPreferences sp = getSharedPreferences("server",
				Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putString("server", server);
		ed.commit();
	}

	/**
	 * 登录模式
	 *
	 * @param v
	 */
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String userName = editUser.getText().toString();
		String password = editPassword.getText().toString();
		if (TextUtils.isEmpty(userName)) {
			Show.toast(this, R.string.enter_user_name);
			return;
		}

		if (TextUtils.isEmpty(password)) {

			Show.toast(this, R.string.enter_password);
			return;
		}
		Constants.Login_user = userName;
		Constants.Login_password = password;

		// 设置登录模式
		clientCore.setLocalList(false);

		WebSdkApi.loginServerAtUserId(clientCore, "",
				Constants.Login_user, Constants.Login_password, handler);

	}

	/**
	 * 免登陆模式
	 *
	 * @param v
	 */
	public void onClick1(View v) {
		// 设置免登陆模式
		clientCore.setLocalList(true);
		WebSdkApi.loginServerAtUserId(clientCore, "", "", "",
				handler);
	}

	/**
	 * umid直连模式/免登陆报警测试
	 *
	 * @param v
	 */
	public void onClick2(View v) {
		//startActivity(new Intent(AcSelectMode.this, AcDevNetPort.class));
		// umid直连模式 ，不需要下载设备列表
		clientCore.setLocalList(true);
		ClientCore.isSuportLocalAlarmPush = true;// 设置支持免登陆报警
		WebSdkApi.loginServerAtUserId(clientCore, "", "", "",
				new Handler() {

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub

						clientCore.setUserPush(1,
								Utility.isZh(context) ? 2 : 1,
								AlarmUtils.GETUI_CID, 1, 0, 0,new Handler() {

									@Override
									public void handleMessage(Message msg) {
										ResponseCommon responseCommon = (ResponseCommon) msg.obj;
										if (responseCommon != null
												&& responseCommon.h != null
												&& responseCommon.h.e == Errors.UM_WEB_API_SUCCESS) {
											Log.i("setUserPush", "设置用户推送成功");
										} else {
											Log.i("setUserPush", "设置用户推送失败");
										}
									}
								});

						startActivity(new Intent(AcSelectMode.this,
								PlayActivity2.class));
						finish();
						super.handleMessage(msg);
					}
				});
		// TODO Auto-generated method stub
	}




	public void onClick3(View v) {
		clientCore.setLocalList(false);
		WebSdkApi.loginServerByThirdParty(clientCore, "wx31d03edf8e532e3f", "", 1,"oQoOsw2tMWSALpjO-xnkRBKPW4M2",
				handler);

	}



}
