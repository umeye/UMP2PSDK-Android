package com.example.umeyeNewSdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Player.Core.PlayerClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.DevIpInfo;
import com.example.umeyesdk.entity.DevIpInfoRet;
import com.example.umeyesdk.thread.GetIpConfigThread;
import com.example.umeyesdk.thread.SetIpConfigThread;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcDeviceIpSettingsJson extends Activity implements OnClickListener {

	public ShowProgress pd;

	String title;
	private AppMain appMain;
	private Dialog asyncDialog;
	private DevIpInfo devIpInfo;
	private TextView tvMac;
	private Button btnSwitchDHCP;
	boolean isSwitch;
	private DevIpInfo tempdevIpInfo;

	private EditText etIp, etNetMask, etGateway;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case SetIpConfigThread.SET_OK:
				Show.toast(AcDeviceIpSettingsJson.this, R.string.set_ok);
				if (asyncDialog != null)
					asyncDialog.dismiss();
				if (!isSwitch) {
					finish();
				} else {
					String stateDHCP = devIpInfo.Net_DHCP == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitchDHCP.setText(stateDHCP);
					isSwitch = false;
				}

				break;
			case SetIpConfigThread.SET_FALL:
				isSwitch = false;
				devIpInfo = tempdevIpInfo;
				Show.toast(AcDeviceIpSettingsJson.this, R.string.set_fail);
				break;
			case GetIpConfigThread.GET_SUCCEES:
				// Show.toast(AcWifiList.this, R.string.ok);
				devIpInfo = (DevIpInfo) msg.obj;
				if (devIpInfo != null) {
					String stateDHCP = devIpInfo.Net_DHCP == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitchDHCP.setText(stateDHCP);
					etIp.setText(devIpInfo.Net_IPAddr);
					etNetMask.setText(devIpInfo.Net_Netmask);
					etGateway.setText(devIpInfo.Net_Gateway);
					// tvSSID.setText(wifiinfo);
				}
				break;
			case GetIpConfigThread.GET_FAILED:
				Show.toast(AcDeviceIpSettingsJson.this, R.string.get_failed);
				finish();
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_device_ip_settings);
		appMain = (AppMain) this.getApplicationContext();
		tvMac = (TextView) this.findViewById(R.id.tvMac);
		findViewById(R.id.back_btn).setOnClickListener(this);
		findViewById(R.id.menu_btn1).setOnClickListener(this);

		btnSwitchDHCP = (Button) findViewById(R.id.btn_switch_dhcp);
		btnSwitchDHCP.setOnClickListener(this);

		etIp = (EditText) this.findViewById(R.id.et_dev_ip);
		etNetMask = (EditText) this.findViewById(R.id.et_dev_netmask);
		etGateway = (EditText) this.findViewById(R.id.et_dev_gateway);
		// new ThreadSearchDeviceWifi().execute();
		showProgressDialog(getString(R.string.get_dev_ip));
		getData(0);
	}

	public void getData(final int iChNo) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				PlayerClient pc = appMain.getPlayerclient();
				int funcID = 0x03;// NPC_D_DPS_JSON_FUNCID_DEV_IP=0x03 设备有线网络配置
				int type = 0;// 获取配置
				int channel = iChNo;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Operation", funcID);
				jsonObject.put("Request_Type", type);
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("Channel", channel);
				jsonObject.put("Value", jsonObject1);

				String inputJson = jsonObject.toString();
				Log.d("CallCustomFunc", "inputJson:" + inputJson);
				// byte[] ret = pc.CallCustomFunc(deviceId, 0x010203,
				// inputJson.getBytes());
				byte[] ret = pc.CallCustomFunc(Constants.UMID, Constants.user,
						Constants.password, 0x010203, inputJson.getBytes());
				if (ret != null) {
					String jsonString = new String(ret).trim();
					Log.d("CallCustomFunc", "CallCustomFunc:" + jsonString);
					DevIpInfoRet devIpInfoRet = JSON.parseObject(jsonString,
							DevIpInfoRet.class);
					if (devIpInfoRet != null && devIpInfoRet.Result == 1) {

						handler.sendMessage(Message.obtain(handler,
								GetIpConfigThread.GET_SUCCEES,
								devIpInfoRet.Value));
					} else {
						handler.sendEmptyMessage(GetIpConfigThread.GET_FAILED);
					}

				} else {
					handler.sendEmptyMessage(GetIpConfigThread.GET_FAILED);
				}
				super.run();
			}
		}.start();

	}

	/**
	 * 发送保存数据
	 * 
	 * @param channel
	 */
	public void sendData(final int iChNo) {
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (devIpInfo != null) {
					PlayerClient pc = appMain.getPlayerclient();
					int funcID = 0x03;// 设备有线网络配置
										// NPC_D_DPS_JSON_FUNCID_DEV_IP=0x03

					int type = 1;// 设置配置
					int channel = iChNo;
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("Operation", funcID);
					jsonObject.put("Request_Type", type);
					jsonObject.put("Value", devIpInfo);
					String inputJson = jsonObject.toString();
					Log.d("CallCustomFunc", "" + inputJson);
					byte[] ret = pc.CallCustomFunc(Constants.UMID,
							Constants.user, Constants.password, 0x010203,
							inputJson.getBytes());
					if (ret != null) {
						String jsonString = new String(ret).trim();
						Log.d("CallCustomFunc", "CallCustomFunc:" + jsonString);
						DevIpInfoRet devIpInfoRet = JSON.parseObject(
								jsonString, DevIpInfoRet.class);
						if (devIpInfoRet != null && devIpInfoRet.Result == 1) {
							handler.sendEmptyMessage(SetIpConfigThread.SET_OK);
						} else {
							handler.sendEmptyMessage(SetIpConfigThread.SET_FALL);
						}

					} else {
						handler.sendEmptyMessage(SetIpConfigThread.SET_FALL);
					}
				}
				super.run();
			}
		}.start();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_switch_dhcp:
			if (devIpInfo != null) {
				showProgressDialog("");
				isSwitch = true;
				tempdevIpInfo = devIpInfo;
				devIpInfo.Net_DHCP = devIpInfo.Net_DHCP == 1 ? 0 : 1;
				// new SetIpConfigThread(appMain.getPlayerclient(),
				// Constants.UMID, Constants.user, Constants.password,
				// devIpInfo, handler).start();
				sendData(0);
			}
			break;
		case R.id.back_btn:
			// startActivity(new Intent(this, AcRegister.class));
			finish();
			break;
		case R.id.menu_btn1:
			if (devIpInfo != null) {
				showProgressDialog("");
				tempdevIpInfo = devIpInfo;
				devIpInfo.Net_IPAddr = etIp.getText().toString().trim();
				devIpInfo.Net_Gateway = etGateway.getText().toString().trim();
				devIpInfo.Net_Netmask = etNetMask.getText().toString().trim();

				sendData(0);
			}
			break;
		case R.id.btn_async_cancel:
			if (asyncDialog != null)
				asyncDialog.dismiss();
			break;
		default:
			break;
		}

	}

	public void showProgressDialog(String message) {
		if (pd == null) {
			pd = new ShowProgress(this);
			pd.setCanceledOnTouchOutside(false);
		}
		pd.setMessage(message);
		pd.show();
	}

}
