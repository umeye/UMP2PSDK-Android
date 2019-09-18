package com.example.umeyeNewSdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.Player.Core.PlayerClient;
import com.Player.Source.LogOut;
import com.Player.Source.TDevStorageInfo;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcDevStorage extends Activity implements OnClickListener {
	private static final int GET_FAILED = 2;
	private static final int GET_SUCCEES = 3;
	private static final int SEND_EMPTY = 4;
	private AppMain appMain;
	public ShowProgress pd;

	private TDevStorageInfo tDevStorageInfo;
	LayoutInflater lInflater;

	ListView listView;

	private TextView logTextView;

	// TextView remainTextView1, totalTextView1, remainTextView2,
	// totalTextView2;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			pd.dismiss();

			super.handleMessage(msg);
			switch (msg.what) {
				case GET_SUCCEES:
					setText();
					break;
				case GET_FAILED:
					Show.toast(AcDevStorage.this, R.string.get_failed);
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
		setContentView(R.layout.ac_dev_storage);

		appMain = (AppMain) this.getApplicationContext();
		// remainTextView1 = (TextView) findViewById(R.id.tv_remain1);
		// totalTextView1 = (TextView) findViewById(R.id.tv_totoal1);
		// remainTextView2 = (TextView) findViewById(R.id.tv_remain2);
		// totalTextView2 = (TextView) findViewById(R.id.tv_totoal2);

		lInflater = LayoutInflater.from(this);
		listView = (ListView) findViewById(R.id.listView);
		logTextView = (TextView) findViewById(R.id.tv_log);
		findViewById(R.id.back_btn).setOnClickListener(this);
		findViewById(R.id.btn_formate).setOnClickListener(this);
		showProgressDialog(getString(R.string.get_dev_storage));
		getStorageThread();

	}

	public void showProgressDialog(String message) {
		if (pd == null) {
			pd = new ShowProgress(this);
			pd.setCanceledOnTouchOutside(false);
		}
		pd.setMessage(message);
		pd.show();
	}

	void getStorageThread() {
		new Thread() {

			@Override
			public void run() {
				PlayerClient playerClient = appMain.getPlayerclient();
				tDevStorageInfo = playerClient.CameraGetDevStorage(
						Constants.UMID, Constants.user, Constants.password);
				if (tDevStorageInfo != null) {
					LogOut.d("CameraGetDevStorage", "CameraGetDevStorage:"
							+ tDevStorageInfo.toString());
					handler.sendEmptyMessage(GET_SUCCEES);
				} else {
					handler.sendEmptyMessage(GET_FAILED);
				}

			}
		}.start();
	}

	void setStorageThread() {
		new Thread() {

			@Override
			public void run() {
				PlayerClient playerClient = appMain.getPlayerclient();
				if (tDevStorageInfo.tDevSdardInfo == null)
					return;

				if (tDevStorageInfo.tDevSdardInfo.length == 0)
					return;

				int[] iserial = new int[tDevStorageInfo.tDevSdardInfo.length];
				for (int i = 0; i < tDevStorageInfo.tDevSdardInfo.length; i++) {
					iserial[i] = tDevStorageInfo.tDevSdardInfo[i].iSerialNo;
				}
				long ret;
				try {
					ret = playerClient.CameraFormateStorage(Constants.UMID,
							Constants.user, Constants.password,
							new MyCallBack(), iserial);
					handler.sendEmptyMessage(SEND_EMPTY);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

	/**
	 * 回调，
	 *
	 * @author Simula
	 *
	 */
	class MyCallBack implements Callback {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub

			handler.sendMessage(msg);



			return false;
		}
	}

	void setText() {
		// remainTextView1.setText(String.valueOf(tDevStorageInfo.dwRemainSize1)+"M");
		// remainTextView2.setText(String.valueOf(tDevStorageInfo.dwRemainSize2)+"M");
		// totalTextView1.setText(String.valueOf(tDevStorageInfo.dwStorageSize1)+"M");
		// totalTextView2.setText(String.valueOf(tDevStorageInfo.dwStorageSize2)+"M");
		if (tDevStorageInfo.tDevSdardInfo != null) {
			listView.setAdapter(new listAdapter());
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.btn_formate:
				new AlertDialog.Builder(this)
						.setMessage(getString(R.string.dev_storage_format_c))
						.setTitle("警告")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										logTextView
												.append(getString(R.string.dev_storage_format));
										setStorageThread();
									}
								}).setNegativeButton("取消", null).show();

				break;
			default:
				break;
		}

	}

	@SuppressLint("ViewHolder")
	class listAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tDevStorageInfo.tDevSdardInfo.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = lInflater.inflate(R.layout.item_sdcard_info, null);
			TextView serialTextView = (TextView) convertView
					.findViewById(R.id.tv_serial);
			TextView remainTextView = (TextView) convertView
					.findViewById(R.id.tv_remain);
			TextView totoalTextView = (TextView) convertView
					.findViewById(R.id.tv_totoal);

			serialTextView
					.setText(String
							.valueOf(tDevStorageInfo.tDevSdardInfo[position].iSerialNo));
			remainTextView
					.setText(String
							.valueOf(tDevStorageInfo.tDevSdardInfo[position].dwRemainSize)
							+ "M");
			totoalTextView
					.setText(String
							.valueOf(tDevStorageInfo.tDevSdardInfo[position].dwStorageSize)
							+ "M");

			return convertView;
		}

	}

}
