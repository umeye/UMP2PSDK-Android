package com.example.umeyeNewSdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.Player.Core.PlayerClient;
import com.Player.Source.TDevIpInfo;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.thread.GetIpConfigThread;
import com.example.umeyesdk.thread.SetIpConfigThread;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcDeviceIpSettings extends Activity implements OnClickListener {
	public ShowProgress pd;

	String title;
	private AppMain appMain;
	private EditText etSsid, etWifiPass;
	private Button btnAsyncSure, btnAsyncCancel;
	private CheckBox ckShowPass;
	// private PlayNode node;
	private Dialog asyncDialog;
	private TDevIpInfo devIpInfo;
	private TextView tvMac;
	private Button btnSwitchDHCP;
	boolean isSwitch;
	private TDevIpInfo tempdevIpInfo;

	private EditText etIp, etNetMask, etGateway;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case SetIpConfigThread.SET_OK:
				Show.toast(AcDeviceIpSettings.this, R.string.set_ok);
				if (asyncDialog != null)
					asyncDialog.dismiss();
				if (!isSwitch) {
					setResult(RESULT_OK);
					finish();
				} else {

					// String state = devIpInfo.bEnable == 1 ?
					// getString(R.string.on)
					// : getString(R.string.off);
					// btnSwitch.setText(state);

					String stateDHCP = devIpInfo.bDhcpEnable == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitchDHCP.setText(stateDHCP);

					isSwitch = false;
				}

				break;
			case SetIpConfigThread.SET_FALL:
				isSwitch = false;
				devIpInfo = tempdevIpInfo;
				Show.toast(AcDeviceIpSettings.this, R.string.set_fail);
				break;
			case GetIpConfigThread.GET_SUCCEES:
				// Show.toast(AcWifiList.this, R.string.ok);
				devIpInfo = (TDevIpInfo) msg.obj;
				if (devIpInfo != null) {

					// String stateEnable = devIpInfo.bEnable == 1 ?
					// getString(R.string.on)
					// : getString(R.string.off);
					// btnSwitch.setText(stateEnable);

					String stateDHCP = devIpInfo.bDhcpEnable == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitchDHCP.setText(stateDHCP);
					etIp.setText(devIpInfo.sIpaddr);
					etNetMask.setText(devIpInfo.sNetmask);
					etGateway.setText(devIpInfo.sGateway);
					// tvSSID.setText(wifiinfo);
				}
				break;
			case GetIpConfigThread.GET_FAILED:
				Show.toast(AcDeviceIpSettings.this, R.string.get_failed);
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
		new GetIpConfigThread(Constants.UMID, Constants.user,
				Constants.password, appMain.getPlayerclient(), handler).start();
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
				devIpInfo.bDhcpEnable = devIpInfo.bDhcpEnable == 1 ? 0 : 1;
				new SetIpConfigThread(appMain.getPlayerclient(),
						Constants.UMID, Constants.user, Constants.password,
						devIpInfo, handler).start();

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
				devIpInfo.sIpaddr = etIp.getText().toString().trim();
				devIpInfo.sGateway = etGateway.getText().toString().trim();
				devIpInfo.sNetmask = etNetMask.getText().toString().trim();
				new SetIpConfigThread(appMain.getPlayerclient(),
						Constants.UMID, Constants.user, Constants.password,
						devIpInfo, handler).start();
			}
			break;
		case R.id.btn_async_cancel:
			if (asyncDialog != null)
				asyncDialog.dismiss();
			break;

		// devIpInfo.sWifiPwd = pass;
		// devIpInfo.sWifiSSID = ssid;
		// devIpInfo.bFieldEnable_AuthType = 0;
		// showProgressDialog("");
		// new WifiSetThread(appMain.getPlayerclient(), Constants.UMID,
		// Constants.user, Constants.password, devIpInfo,
		// handler).start();
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

	public class ThreadSearchDeviceWifi extends
			AsyncTask<Void, Void, TDevIpInfo> {

		@Override
		protected TDevIpInfo doInBackground(Void... params) {
			// TODO Auto-generated method stub

			PlayerClient playerclient = appMain.getPlayerclient();
			TDevIpInfo searchRet = playerclient.CameraGetIpConfig(
					Constants.UMID, Constants.user, Constants.password);

			return searchRet;
		}

		TDevIpInfo copyInfo(TDevIpInfo info) {
			TDevIpInfo i = new TDevIpInfo();
			i.bDhcpEnable = info.bDhcpEnable;
			i.sGateway = info.sGateway;
			i.sIpaddr = info.sIpaddr;
			i.sMac = info.sMac;
			i.sNetcardName = info.sNetcardName;
			i.sNetmask = info.sNetmask;
			return i;

		}

		@Override
		protected void onPostExecute(TDevIpInfo info) {
			// TODO Auto-generated method stub
			pd.dismiss();
			if (info != null) {

			} else
				Show.toast(AcDeviceIpSettings.this, R.string.nodataerro);

			super.onPostExecute(info);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			showProgressDialog(AcDeviceIpSettings.this.getResources()
					.getString(R.string.wifi_searching));

			super.onPreExecute();
		}
	}
}
