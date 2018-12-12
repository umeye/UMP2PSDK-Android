package com.example.umeyeNewSdk;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.Player.Source.TDevWifiInfor;
import com.Player.Source.TVideoFile;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.adpter.WifiAdapter;
import com.example.umeyesdk.thread.WifiGetThread;
import com.example.umeyesdk.thread.WifiSetThread;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;
import com.example.umeyesdk.utils.WifiAdmin;

public class AcWifiList extends Activity implements OnClickListener,
		OnItemClickListener {
	public ShowProgress pd;
	WifiAdapter adapter;
	ListView listView;
	String title;
	private AppMain appMain;
	public static TVideoFile videoFile;
	public int deviceId;
	private EditText etSsid, etWifiPass;
	private Button btnAsyncSure, btnAsyncCancel;
	private CheckBox ckShowPass;
	// private PlayNode node;
	private Dialog asyncDialog;
	private TDevWifiInfor devWifiInfo, tempdevWifiInfo;
	private List<ScanResult> list;
	private TextView tvSSID;
	private Button btnSwitch;
	private Button btnSwitchDHCP;
	boolean isSwitch;
	private WifiAdmin wifiAdmin;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case WifiSetThread.SET_OK:
				Show.toast(AcWifiList.this, R.string.set_ok);
				if (asyncDialog != null)
					asyncDialog.dismiss();
				if (!isSwitch) {
					// setResult(RESULT_OK);
					// finish();
				} else {

					String state = devWifiInfo.bEnable == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitch.setText(state);

					String stateDHCP = devWifiInfo.bDhcpEnable == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitchDHCP.setText(stateDHCP);

					isSwitch = false;
				}

				break;
			case WifiSetThread.SET_FALL:
				isSwitch = false;
				devWifiInfo = tempdevWifiInfo;
				Show.toast(AcWifiList.this, R.string.set_fail);
				break;
			case WifiGetThread.GET_SUCCEES:
				// Show.toast(AcWifiList.this, R.string.ok);
				devWifiInfo = (TDevWifiInfor) msg.obj;
				if (devWifiInfo != null) {

					String stateEnable = devWifiInfo.bEnable == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitch.setText(stateEnable);

					String stateDHCP = devWifiInfo.bDhcpEnable == 1 ? getString(R.string.on)
							: getString(R.string.off);
					btnSwitchDHCP.setText(stateDHCP);

					String wifiinfo = String.format(
							getString(R.string.wifi_info),
							devWifiInfo.sWifiSSID, devWifiInfo.sIpaddr,
							devWifiInfo.sNetmask, devWifiInfo.sGateway);
					tvSSID.setText(wifiinfo);
				}
				new ThreadSearchAPDevice().execute();
				break;
			case WifiGetThread.GET_FAILED:
				Show.toast(AcWifiList.this, R.string.get_wifi_failed);
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
		setContentView(R.layout.ac_wifi_list);
		appMain = (AppMain) this.getApplicationContext();
		tvSSID = (TextView) this.findViewById(R.id.tv_ssid);

		deviceId = getIntent().getIntExtra("deviceId", 0);
		// node = CommonData.getPlayNode(appMain.getNodeList(), deviceId);
		listView = (ListView) findViewById(R.id.lvLive);
		listView.setOnItemClickListener(this);
		adapter = new WifiAdapter(this);
		listView.setAdapter(adapter);
		findViewById(R.id.back_btn).setOnClickListener(this);
		findViewById(R.id.menu_btn1).setOnClickListener(this);

		btnSwitchDHCP = (Button) findViewById(R.id.btn_switch_dhcp);
		btnSwitchDHCP.setOnClickListener(this);

		btnSwitch = (Button) findViewById(R.id.btn_switch);
		btnSwitch.setOnClickListener(this);
		// new ThreadSearchDeviceWifi().execute();
		showProgressDialog(getString(R.string.get_wifi_info));
		new WifiGetThread(Constants.UMID, Constants.user, Constants.password,
				appMain.getPlayerclient(), handler).start();
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
		case R.id.btn_switch:
			if (devWifiInfo != null) {
				showProgressDialog("");
				isSwitch = true;
				tempdevWifiInfo = devWifiInfo;
				devWifiInfo.bEnable = devWifiInfo.bEnable == 1 ? 0 : 1;
				new WifiSetThread(appMain.getPlayerclient(), Constants.UMID,
						Constants.user, Constants.password, devWifiInfo,
						handler).start();

			}
			break;
		case R.id.btn_switch_dhcp:
			if (devWifiInfo != null) {
				showProgressDialog("");
				isSwitch = true;
				tempdevWifiInfo = devWifiInfo;
				devWifiInfo.bDhcpEnable = devWifiInfo.bDhcpEnable == 1 ? 0 : 1;
				new WifiSetThread(appMain.getPlayerclient(), Constants.UMID,
						Constants.user, Constants.password, devWifiInfo,
						handler).start();

			}
			break;
		case R.id.back_btn:
			// startActivity(new Intent(this, AcRegister.class));
			finish();
			break;
		case R.id.menu_btn1:
			new ThreadSearchAPDevice().execute();
			break;
		case R.id.btn_async_cancel:
			if (asyncDialog != null)
				asyncDialog.dismiss();
			break;
		case R.id.btn_async_sure:

			if (devWifiInfo != null) {
				tempdevWifiInfo = devWifiInfo;
				String ssid = etSsid.getText().toString();
				String pass = etWifiPass.getText().toString();
				if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pass)) {
					Show.toast(this,
							R.string.NPC_D_MPI_MON_ERROR_USER_PWD_ERROR);
					break;
				}

				devWifiInfo.sWifiPwd = pass;
				devWifiInfo.sWifiSSID = ssid;
				devWifiInfo.bFieldEnable_AuthType = 0;
				devWifiInfo.bDhcpEnable = 1;
				devWifiInfo.bEnable = 1;
				showProgressDialog("");
				new WifiSetThread(appMain.getPlayerclient(), Constants.UMID,
						Constants.user, Constants.password, devWifiInfo,
						handler).start();

			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ScanResult info = list.get(arg2);
		showDialog(info);
		// if (devWifiInfo != null) {
		// if (devWifiInfo.bEnable == 0) { //如果设备未开启wifi，则开启
		// devWifiInfo.bEnable = 1;
		// }
		// String ssid = etSsid.getText().toString();
		// String pass = etWifiPass.getText().toString();
		// if (TextUtils.isEmpty(ssid) && TextUtils.isEmpty(pass)) {
		// Show.toast(this, R.string.NPC_D_MPI_MON_ERROR_USER_PWD_ERROR);
		// return;
		// }
		// devWifiInfo.sWifiPwd = pass;
		// devWifiInfo.sWifiSSID = ssid;
		// showProgressDialog("");
		// new WifiSetThread(appMain.getPlayerclient(), Constants.UMID,
		// Constants.user, Constants.password, devWifiInfo, handler).start();
		//
		// }
	}

	/**
	 * 同步手机网络
	 */
	void showDialog(ScanResult info) {

		if (asyncDialog == null) {
			asyncDialog = new Dialog(this, R.style.MMTheme_DataSheet);
			View view = LayoutInflater.from(this).inflate(
					R.layout.layout_dialog_wifi_synac_to_device, null);
			etSsid = (EditText) view.findViewById(R.id.wifi_enter_ssid);
			etWifiPass = (EditText) view.findViewById(R.id.wifi_enter_pass);
			ckShowPass = (CheckBox) view.findViewById(R.id.ck_show_pass);
			btnAsyncSure = (Button) view.findViewById(R.id.btn_async_sure);
			btnAsyncSure.setOnClickListener(this);
			btnAsyncCancel = (Button) view.findViewById(R.id.btn_async_cancel);
			btnAsyncCancel.setOnClickListener(this);
			// 密码可见监听
			ckShowPass
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub

							if (isChecked) {
								etWifiPass
										.setTransformationMethod(HideReturnsTransformationMethod
												.getInstance());
							} else {
								etWifiPass
										.setTransformationMethod(PasswordTransformationMethod
												.getInstance());
							}
							CharSequence etusere = etWifiPass.getText();

							if (etusere instanceof Spannable) {// 讲光标移至最后面
								Spannable spanText = (Spannable) etusere;
								Selection.setSelection(spanText,
										etusere.length());

							}
						}
					});
			asyncDialog.setContentView(view);
			asyncDialog.setCanceledOnTouchOutside(true);
		}
		etSsid.setText(info.SSID);
		asyncDialog.show();
	}

	public void showProgressDialog(String message) {
		if (pd == null) {
			pd = new ShowProgress(this);
			pd.setCanceledOnTouchOutside(false);
		}
		pd.setMessage(message);
		pd.show();
	}

	public class ThreadSearchAPDevice extends
			AsyncTask<Void, Void, List<ScanResult>> {

		@Override
		protected List<ScanResult> doInBackground(Void... params) {
			list = wifiAdmin.getScanResultList();
			return list;

		}

		@Override
		protected void onPostExecute(List<ScanResult> flist) {
			// TODO Auto-generated method stub

			pd.dismiss();
			if (flist == null) {
				Show.toast(AcWifiList.this, "获取wifi列表失败");
			} else {
				if (flist.size() == 0) {
					Show.toast(AcWifiList.this, "获取wifi列表失败");
				} else {
					adapter.setNodeList(list);
					for (int i = 0; i < flist.size(); i++) {
						String SSID = flist.get(i).SSID;
					}
				}
			}
			super.onPostExecute(list);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			showProgressDialog(AcWifiList.this.getResources().getString(
					R.string.wifi_searching));
			if (list != null) {
				list.clear();
			}
			if (wifiAdmin == null) {
				wifiAdmin = new WifiAdmin(AcWifiList.this);
			}

			super.onPreExecute();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}
}
