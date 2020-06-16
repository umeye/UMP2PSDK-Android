package com.example.umeyesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.Player.Source.LogOut;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseServer;
import com.Player.web.response.UserInfo;
import com.Player.web.websocket.ClientCore;
import com.Player.web.websocket.IoTTokenInvalidListener;
import com.Player.web.websocket.PermissionUtils;
import com.audio2.aacEncode;
import com.example.umeyeNewSdk.PlayActivity2;
import com.example.umeyesdk.utils.Errors;
import com.getui.demo.AlarmUtils;
import com.example.umeyeNewSdk.AcSelectMode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.Utility;
import com.video.h264.DefualtRecoredThread;

public class AcLogo extends Activity {
	public static final String WebSdkApi_Error = "WebSdkApi_Error";
	AppMain appMain;
	private Handler handler;
	private boolean localLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_logo);
		appMain = (AppMain) this.getApplicationContext();

		handler = new Handler();
		SharedPreferences sp = getSharedPreferences("server",
				Context.MODE_PRIVATE);
		String server = sp.getString("server", "");
		if (!TextUtils.isEmpty(server)) {
			Constants.server = server;
		}

		UserInfo userInfo = UserInfo.getUserInfo(this);
		localLogin = userInfo != null && userInfo.isLocalMode();

		ClientCore clientCore = ClientCore.getInstance();

		if (clientCore != null && clientCore.IsLogin()) {// 判断是否处于登录状态，直接进入主界面
			if(!localLogin) {
				startActivity(new Intent(AcLogo.this, MainActivity.class));
				finish();

			} else {
				startActivity(new Intent(AcLogo.this,PlayActivity2.class));
				finish();

			}
		} else {
			ClientCore.isKeDaDev=false;
			if (!PermissionUtils.checkStoragePermission(this)) {
				PermissionUtils.verifyStoragePermissions(this, 1);
			} else {
				// 启动推送服务
				AlarmUtils.openPush(AcLogo.this);
				// 启动sdk

				authUstServerAtUserId(clientCore);

				if(BuildConfig.DEBUG) {
					log();
				}
			}
			// test();
			// clientCore.CLTStartClient("182.92.170.98", 8300);
			// actionToLogin();
		}


	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// TODO request success
					// 启动推送服务
					AlarmUtils.openPush(AcLogo.this);
					// 启动sdk
					authUstServerAtUserId(ClientCore.getInstance());

					if(BuildConfig.DEBUG) {
						log();
					}
				}
		}
	}


	private void log() {

		//打印手机系统信息 需申请READ_PHONE_STATE权限
//		appMain.logSystemInfo(this);


		/**
		 * 若不继承UmeyeApplication，以下2段代码则改为自行调用WriteAllLogThread,WriteDeepLogThread
		 */

		/**
		 * 打印并写底层日志信息
		 * tag logcat显示底层的tag名称，默认为WriteDeepLogThread
		 * dir 日志写到手机的目录位置，默认为sdcard目录的deepLog文件夹
		 * logLevel logcat显示底层的log级别
		 */
		appMain.startLogDeepInfo("","",Log.DEBUG);
		/**
		 * 写所在进程的所有logcat信息
		 * printAllLogs true: 写所有进程信息 false: 写当前进程信息
		 * dir 日志写到手机的目录位置，默认为sdcard目录的allLog文件夹
		 */
		appMain.startLogAllInfos(true, "");
	}



	public void test() {
		ClientCore clientCore = ClientCore.getInstance();
		clientCore
				.setupHost("v0.api.umeye.com", 0, "", 0, "", "", "", "");
		clientCore.authUstServerAtUserId("", "", new Handler() {

			@Override
			public void handleMessage(Message msg) {
				ResponseServer responseServer = (ResponseServer) msg.obj;
				if (responseServer != null && responseServer.b != null) {
					LogOut.i("getCurrentBestServer", "获穿透服务器："
							+ responseServer.b.ust_ip + ",端口："
							+ responseServer.b.ust_port);

				}
			}
		});
	}

	/**
	 * 初始化服务器
	 *
	 */
	public void authUstServerAtUserId(final ClientCore clientCore) {
		int language = Utility.isZh(this) ? 2 : 1;
//		ClientCore.isAPLanMode = true;//ap mode方式

		clientCore.setupHost(Constants.server, 0, Utility.getImsi(this),
				language, Constants.custom_flag,
				String.valueOf(Utility.GetVersionCode(this)), "", "");// 添加备用服务器参数,默认为空




		// 获取最优服务器，然后启动sdk
		clientCore.getCurrentBestServer(new Handler() {
			@Override
			public void handleMessage(Message msg) { // TODO
				ResponseServer responseServer = (ResponseServer) msg.obj;
				if (responseServer != null && responseServer.h != null) {
					if (responseServer.h.e == Errors.UM_WEB_API_SUCCESS) {
						Show.toast(AcLogo.this,
								"获取服务器:" + responseServer.b.toJsonString());
						String[] server = clientCore.getCurrentServer();

					} else {
						Log.e(WebSdkApi_Error, "获取服务器失败! code="
								+ responseServer.h.e);
						Show.toast(AcLogo.this, "获取服务器失败! code="
								+ responseServer.h.e);
					}
				} else {
					Log.e(WebSdkApi_Error, "获取服务器失败! error=" + msg.what);
					Show.toast(AcLogo.this, "获取服务器失败! error=" + msg.what);
				}

				actionToLogin();
				super.handleMessage(msg);





			}
		});
	}

	private void actionToLogin() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				if(ClientCore.getInstance().IsLoginEx()) {//如果需要自动登录，增加这个判断，判断上次是否有登录
					if(!localLogin) {
						startActivity(new Intent(AcLogo.this, MainActivity.class));
						finish();

					} else {
						startActivity(new Intent(AcLogo.this,PlayActivity2.class));
						finish();

					}

				} else {
					startActivity(new Intent(AcLogo.this, AcSelectMode.class));
					finish();
				}

			}
		}, 2000);

	}

}
