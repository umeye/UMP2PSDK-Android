package com.example.umeyeNewSdk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.Player.Core.PlayerClient;
import com.Player.Source.LogOut;
import com.Player.Source.TDevCodeInfo;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcDevCodeInfo extends Activity implements OnClickListener {
	private static final int SET_OK = 0;
	private static final int SET_FALL = 1;
	private static final int GET_FAILED = 2;
	private static final int GET_SUCCEES = 3;
	private static final int SEND_EMPTY = 4;
	private AppMain appMain;
	public ShowProgress pd;

	private TDevCodeInfo tDevCodeInfo;
	LayoutInflater lInflater;

	// TextView remainTextView1, totalTextView1, remainTextView2,
	// totalTextView2;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			pd.dismiss();

			super.handleMessage(msg);
			switch (msg.what) {

			case SET_OK:

				break;
			case SET_FALL:

				break;
			case GET_SUCCEES:
				setText();
				break;
			case GET_FAILED:
				Show.toast(AcDevCodeInfo.this, R.string.get_failed);
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
		setContentView(R.layout.ac_dev_code);

		appMain = (AppMain) this.getApplicationContext();
		// remainTextView1 = (TextView) findViewById(R.id.tv_remain1);
		// totalTextView1 = (TextView) findViewById(R.id.tv_totoal1);
		// remainTextView2 = (TextView) findViewById(R.id.tv_remain2);
		// totalTextView2 = (TextView) findViewById(R.id.tv_totoal2);

		lInflater = LayoutInflater.from(this);

		findViewById(R.id.back_btn).setOnClickListener(this);
		
		showProgressDialog(getString(R.string.get_dev_storage));
		getCodeInfoThread();

	}

	public void showProgressDialog(String message) {
		if (pd == null) {
			pd = new ShowProgress(this);
			pd.setCanceledOnTouchOutside(false);
		}
		pd.setMessage(message);
		pd.show();
	}

	void getCodeInfoThread() {
		new Thread() {

			@Override
			public void run() {
				PlayerClient playerClient = appMain.getPlayerclient();
				tDevCodeInfo = playerClient.CameraGetDevCode(Constants.UMID,
						Constants.user, Constants.password, 0);
				if (tDevCodeInfo != null) {
					LogOut.d("CameraGetDevCode", "CameraGetDevCode:"
							+ tDevCodeInfo.toString());
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
//				
//				PlayerClient playerClient = appMain.getPlayerclient();
//				if (tDevStorageInfo.tDevSdardInfo == null)
//					return;
//
//				if (tDevStorageInfo.tDevSdardInfo.length == 0)
//					return;
//
//				int[] iserial = new int[tDevStorageInfo.tDevSdardInfo.length];
//				for (int i = 0; i < tDevStorageInfo.tDevSdardInfo.length; i++) {
//					iserial[i] = tDevStorageInfo.tDevSdardInfo[i].iSerialNo;
//				}
//				int ret;
//				try {
//					ret = playerClient.CameraFormateStorage(Constants.UMID,
//							Constants.user, Constants.password,
//							new MyCallBack(), iserial);
//					handler.sendEmptyMessage(SEND_EMPTY);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

			}
		}.start();
	}

	/**
	 * »Øµ÷£¬
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

		

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.btn_formate:
			break;
		default:
			break;
		}

	}


}
