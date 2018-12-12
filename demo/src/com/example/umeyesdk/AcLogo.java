package com.example.umeyesdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.Player.Core.PlayerCore;
import com.Player.Source.LogLisenter;
import com.Player.Source.LogOut;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseServer;
import com.Player.web.websocket.ClientCore;
import com.example.umeyeNewSdk.AcSelectMode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.Utility;
import com.getui.demo.AlarmUtils;
import com.igexin.sdk.PushManager;

public class AcLogo extends Activity {
	public static final String WebSdkApi_Error = "WebSdkApi_Error";
	AppMain appMain;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_logo);
		appMain = (AppMain) this.getApplicationContext();

		handler = new Handler();
		SharedPreferences sp = getSharedPreferences("server",Context.MODE_PRIVATE);
		String server = sp.getString("server", "");
		if (!TextUtils.isEmpty(server)) {
			Constants.server = server;
		}

		ClientCore clientCore = ClientCore.getInstance();
		if (clientCore != null && clientCore.IsLogin()) {//已经登录过，直接进入主界面
			startActivity(new Intent(AcLogo.this, AcSelectMode.class));
			finish();
		} else {
			// 启动推送服务
			AlarmUtils.openPush(AcLogo.this);
			// 启动sdk
			authUstServerAtUserId(clientCore);
		}
	}

	/**
	 * 初始化服务器
	 * 
	 * @param pc
	 * @param handler
	 */
	public void authUstServerAtUserId(ClientCore clientCore) {

		int language = Utility.isZh(this) ? 2 : 1;
		clientCore.setupHost(this, Constants.server, 0, Utility.getImsi(this),language, Constants.custom_flag,String.valueOf(Utility.GetVersionCode(this)), "","");//添加备用服务器参数,默认为空

		// 获取最优服务器，然后启动sdk

		clientCore.getCurrentBestServer(this, new Handler() {
			@Override
			public void handleMessage(Message msg) { // TODO

				ResponseServer responseServer = (ResponseServer) msg.obj;
				if (responseServer != null && responseServer.h != null) {
					if (responseServer.h.e == 200) {
						Show.toast(AcLogo.this,"获取服务器:" + responseServer.b.toJsonString());
					} else {
						Log.e(WebSdkApi_Error, "获取服务器失败! code="+ responseServer.h.e);
						Show.toast(AcLogo.this, "获取服务器失败! code="+ responseServer.h.e);
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

				startActivity(new Intent(AcLogo.this, AcSelectMode.class));
				finish();
			}
		}, 2000);

	}

}
