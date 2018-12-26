package com.example.umeyeNewSdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.SearchDeviceInfo;
import com.example.umeyesdk.thread.ModifySearchDeviceIpThread;
import com.example.umeyesdk.thread.ModifySearchDeviceNameThread;
import com.example.umeyesdk.thread.ModifySearchDevicePassThread;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcModifySearchDeviceParameters extends Activity implements
		OnCheckedChangeListener {
	public final int OK = 0;
	public final int ERROR = 1;
	AppMain appMain;
	private ShowProgress progressDialog;
	TextView txtShow;
	SearchDeviceInfo info;
	EditText etContent;
	ToggleButton tbDHCP;
	EditText etGateWay;
	EditText etMask;
	int position;
	int type;
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

				case ModifySearchDeviceIpThread.MODIFY_SUCCESS:
					Show.toast(AcModifySearchDeviceParameters.this,
							R.string.set_ok);
					if (type == 1) {// 修改IP、
						info.sIpaddr_1 = etContent.getText().toString();
						info.sNetmask_1 = etMask.getText().toString();
						info.sGateway_1 = etGateWay.getText().toString();
						info.bIfEnableDhcp = tbDHCP.isChecked() ? 1 : 0;
						AcModifyDevice.info = info;
						if (AcSearchDevice.list != null) {
							if (AcSearchDevice.list.size() > position) {
								AcSearchDevice.list.set(position, info);
							}
						}

					} else if (type == 2) {// 修改名称

						info.sDevName = etContent.getText().toString();
						AcModifyDevice.info = info;
						if (AcSearchDevice.list != null) {
							if (AcSearchDevice.list.size() > position) {
								AcSearchDevice.list.set(position, info);
							}
						}

					}
					finish();
					break;
				case ModifySearchDeviceIpThread.MODIFY_FAILED:
					Show.toast(AcModifySearchDeviceParameters.this,
							R.string.set_fail);
					break;
				default:
					break;
			}
			progressDialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_modify_search_device_parameters);
		appMain = (AppMain) this.getApplication();
		Intent intent = getIntent();
		progressDialog = new ShowProgress(this);
		TextView title = (TextView) findViewById(R.id.title_name);
		txtShow = (TextView) findViewById(R.id.textShow);
		etContent = (EditText) findViewById(R.id.et);
		info = (SearchDeviceInfo) intent.getSerializableExtra("node");
		position = getIntent().getIntExtra("position", 0);
		type = intent.getIntExtra("type", 1);
		String titleName;
		String sContent;
		if (type == 1) {// 修改IP、
			titleName = getString(R.string.modify_ip);
			sContent = info.sIpaddr_1;
			txtShow.setText(getString(R.string.search_ip));
			findViewById(R.id.dhcp).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_mask).setVisibility(View.VISIBLE);
			findViewById(R.id.layout_gateway).setVisibility(View.VISIBLE);
			etMask = (EditText) findViewById(R.id.et2);
			etGateWay = (EditText) findViewById(R.id.et3);

			tbDHCP = (ToggleButton) findViewById(R.id.toggle_dhcp);
			tbDHCP.setOnCheckedChangeListener(this);
			tbDHCP.setChecked(info.bIfEnableDhcp == 1 ? true : false);
			etMask.setText(info.sNetmask_1 + "");
			etGateWay.setText(info.sGateway_1);


		} else if (type == 2) {// 修改名称
			titleName = getString(R.string.modify_user);
			sContent = info.sDevName;
			txtShow.setText(getString(R.string.modify_user));
		} else {// 密码
			titleName = getString(R.string.modify_pass);
			sContent = "";
			txtShow.setText(getString(R.string.new_password));
			findViewById(R.id.layout_old_pass).setVisibility(View.VISIBLE);
		}
		title.setText("" + titleName);

		etContent.setText("" + sContent);
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String sContent = etContent.getText().toString();

				if (type == 1) {

					String sMask = etMask.getText().toString();
					String sGateWay = etGateWay.getText().toString();

					if (!tbDHCP.isChecked()) {
						if (TextUtils.isEmpty(sContent)
								|| TextUtils.isEmpty(sMask)
								|| TextUtils.isEmpty(sGateWay)) {
							Show.toast(AcModifySearchDeviceParameters.this,
									R.string.input_not_empty);
							return;
						}
					}

					progressDialog.show();

					new ModifySearchDeviceIpThread(appMain.getPlayerclient(),
							info, handler, sContent, sMask, sGateWay, tbDHCP
							.isChecked()).start();

				} else if (type == 2) {
					if (TextUtils.isEmpty(sContent)) {
						Show.toast(AcModifySearchDeviceParameters.this,
								R.string.input_not_empty);
						return;
					}
					progressDialog.show();
					new ModifySearchDeviceNameThread(appMain.getPlayerclient(),
							info, handler, sContent).start();
				} else {
					EditText etOld = (EditText) findViewById(R.id.et1);
					String oldPass = etOld.getText().toString();

					progressDialog.show();
					new ModifySearchDevicePassThread(appMain.getPlayerclient(),
							info, handler, oldPass, sContent).start();
				}
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		EditText et2 = (EditText) findViewById(R.id.et2);
		EditText et3 = (EditText) findViewById(R.id.et3);

		if (isChecked) {

			etContent.setEnabled(false);

			et2.setEnabled(false);

			et3.setEnabled(false);
		} else {

			etContent.setEnabled(true);

			et2.setEnabled(true);

			et3.setEnabled(true);
		}

	}
}
