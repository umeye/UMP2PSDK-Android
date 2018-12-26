package com.example.umeyeNewSdk;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.Player.Core.PlayerClient;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.MuiltChInfo;
import com.example.umeyesdk.entity.MuiltChInfoRet;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcZeroChSettings extends Activity implements OnClickListener {
	public final int OK = 0;
	public final int ERROR = 1;
	public final int GET_OK = 2;
	public final int GET_ERROR = 3;
	public final int SET_OK = 4;
	public final int SET_ERROR = 5;
	private AppMain appMain;
	TextView tvGetInfo;
	MuiltChInfo muiltChInfo;
	int tvId[] = { R.id.ch1, R.id.ch2, R.id.ch3, R.id.ch4, R.id.ch5, R.id.ch6,
			R.id.ch7, R.id.ch8, R.id.ch9, R.id.ch10, R.id.ch11, R.id.ch12,
			R.id.ch13, R.id.ch14, R.id.ch15, R.id.ch16, R.id.ch17, R.id.ch18,
			R.id.ch19, R.id.ch20, R.id.ch21, R.id.ch22, R.id.ch23, R.id.ch24 };
	TextView tvCh[] = new TextView[24];
	Activity con;
	int index = 0;// 当前选中的通道下标 选择一个 加1
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch (msg.what) {

				case OK:
					finish();
					break;
				case ERROR:
					break;
				case GET_OK:
					muiltChInfo = (MuiltChInfo) msg.obj;
					initeData();
					break;
				case GET_ERROR:
					Show.toast(con, R.string.get_failed);
					finish();
					break;
				case SET_OK:
					Show.toast(con, "设置多通道成功");
					finish();
					break;
				case SET_ERROR:
					Show.toast(con, "获取多通道信息成功");
					break;
				default:
					break;
			}
		}
	};
	private ShowProgress progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_zero_ch_setting);
		con = this;
		appMain = (AppMain) this.getApplicationContext();
		tvGetInfo = (TextView) findViewById(R.id.getinfo);
		for (int i = 0; i < tvCh.length; i++) {
			tvCh[i] = (TextView) findViewById(tvId[i]);
			tvCh[i].setText(String.valueOf(1 + i));
			tvCh[i].setOnClickListener(this);
			tvCh[i].setBackgroundColor(Color.WHITE);
		}

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		findViewById(R.id.btnSave).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendData();
			}
		});
		findViewById(R.id.clear).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				index = 0;
				muiltChInfo.ShowChannel = new int[muiltChInfo.SyntheticNum];
				for (int i = 0; i < tvId.length; i++) {
					tvCh[i].setBackgroundColor(Color.WHITE);

				}
			}
		});
		progressDialog = new ShowProgress(this);
		getData();
	}

	public void getData() {
		progressDialog.show();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				PlayerClient pc = appMain.getPlayerclient();
				int funcID = 0x0e;
				int type = 0;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Operation", funcID);
				jsonObject.put("Request_Type", type);

				String inputJson = jsonObject.toString();
				Log.d("CallCustomFunc", "inputJson:" + inputJson);
				byte[] ret = pc.CallCustomFunc(Constants.UMID, Constants.user,
						Constants.password, 0x010203, inputJson.getBytes());
				if (ret != null) {
					String jsonString = new String(ret).trim();
					Log.d("CallCustomFunc", "CallCustomFunc:" + jsonString);
					MuiltChInfoRet muiltChInfoRet = JSON.parseObject(
							jsonString, MuiltChInfoRet.class);
					if (muiltChInfoRet != null && muiltChInfoRet.Result == 1) {
						Log.d("DevCodeInfo", "" + muiltChInfoRet.toString());
						handler.sendMessage(Message.obtain(handler, GET_OK,
								muiltChInfoRet.Value));
					} else {
						handler.sendEmptyMessage(GET_ERROR);
					}

				} else {
					handler.sendEmptyMessage(GET_ERROR);
				}
				super.run();
			}
		}.start();

	}

	public void sendData() {
		progressDialog.show();
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				PlayerClient pc = appMain.getPlayerclient();
				int funcID = 0x0e;
				int type = 1;
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("Operation", funcID);
				jsonObject.put("Request_Type", type);
				JSONObject jsonObject1 = new JSONObject();
				jsonObject1.put("ShowChannel", muiltChInfo.ShowChannel);
				jsonObject.put("Value", jsonObject1);
				String inputJson = jsonObject.toString();
				Log.d("CallCustomFunc", "inputJson:" + inputJson);
				byte[] ret = pc.CallCustomFunc(Constants.UMID, Constants.user,
						Constants.password, 0x010203, inputJson.getBytes());
				if (ret != null) {
					String jsonString = new String(ret).trim();
					Log.d("CallCustomFunc", "CallCustomFunc:" + jsonString);
					MuiltChInfoRet muiltChInfoRet = JSON.parseObject(
							jsonString, MuiltChInfoRet.class);
					if (muiltChInfoRet != null && muiltChInfoRet.Result == 1) {
						Log.d("DevCodeInfo", "" + muiltChInfoRet.toString());
						handler.sendMessage(Message.obtain(handler, SET_OK,
								muiltChInfoRet.Value));
					} else {
						handler.sendEmptyMessage(SET_ERROR);
					}

				} else {
					handler.sendEmptyMessage(SET_ERROR);
				}
				super.run();
			}
		}.start();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (index == muiltChInfo.SyntheticNum) {
			Show.toast(this, "超过最大值");
			return;
		}
		for (int i = 0; i < tvId.length; i++) {
			if (v.getId() == tvId[i]) {
				tvCh[i].setBackgroundColor(Color.GREEN);
				muiltChInfo.ShowChannel[index] = i;
				index++;
				return;
			}

		}
	}

	void initeData() {
		if (muiltChInfo != null) {
			tvGetInfo.setText(muiltChInfo.toString() + "");

		}

	}
}
