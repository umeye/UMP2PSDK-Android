package com.example.umeyeNewSdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.SearchDeviceInfo;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcModifyDevice extends Activity {
	public final int OK = 0;
	public final int ERROR = 1;
	AppMain appMain;
	public static SearchDeviceInfo info;
	int position;
	TextView title;
	private ShowProgress progressDialog;
	public Handler hander = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case OK:
				Show.toast(AcModifyDevice.this, R.string.set_ok);
				finish();
				break;
			case ERROR:
				Show.toast(AcModifyDevice.this, R.string.set_fail);
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
		setContentView(R.layout.ac_modify_search_device);
		appMain = (AppMain) this.getApplication();

		progressDialog = new ShowProgress(this);
		title = (TextView) findViewById(R.id.title_name);
		info = (SearchDeviceInfo) getIntent().getSerializableExtra("node");
		position = getIntent().getIntExtra("position", 0);

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.layout_m_ip).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(AcModifyDevice.this,
								AcModifySearchDeviceParameters.class)
								.putExtra("node", info).putExtra("type", 1)
								.putExtra("position", position));
					}
				});
		findViewById(R.id.layout_m_name).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(AcModifyDevice.this,
								AcModifySearchDeviceParameters.class)
								.putExtra("node", info).putExtra("type", 2)
								.putExtra("position", position));
					}
				});
		findViewById(R.id.layout_m_pass).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(AcModifyDevice.this,
								AcModifySearchDeviceParameters.class)
								.putExtra("node", info).putExtra("type", 3)
								.putExtra("position", position));
					}
				});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		title.setText("" + info.getsDevName());
		super.onResume();
	}

}
