package com.example.umeyeNewSdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.Player.Core.PlayerCore;
import com.Player.Source.TAlarmMotionDetect;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcAlertSettings extends FragmentActivity {
	public static final int NOTIFY_SAVE_OK = 0;
	public static final int NOTIFY_SAVE_ERROE = 1;
	public static final int ALARM_SAVE_OK = 2;
	public static final int ALARM_SAVE_ERROE = 3;
	protected static final int GET_OK = 4;
	protected static final int GET_FAILED = 5;
	protected static final int GET_AlARM_OK = 6;
	protected static final int GET_AlARM_FAILED = 7;
	TextView titleName;
	String deviceName;

	private ShowProgress pd;
	TAlarmMotionDetect alarmMotion;
	// PtzPickerDialog ppd;
	ToggleButton toggleSwitch, toggleNotify;
	boolean isNotifyToPhone = false;
	boolean isGetAlarmMotion = false;
	TextView tv_sensor;
	PlayerCore pc;

	// public ArrayList<Fragment> fragments = new ArrayList<Fragment>();;
	public Handler hander = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {

			case GET_AlARM_OK:

				toggleSwitch = (ToggleButton) findViewById(R.id.toggle_action_switch);
				tv_sensor.setText(String.valueOf(alarmMotion.iLevel));

				if (alarmMotion.iLevel == 0) {

					findViewById(R.id.rl_sensor_level).setVisibility(View.GONE);

				}
				if (alarmMotion.bIfEnable == 1) {
					toggleSwitch.setChecked(true);
				} else {
					toggleSwitch.setChecked(false);
				}
				break;
			case GET_AlARM_FAILED:
				findViewById(R.id.rl_sensor_level).setVisibility(View.GONE);
				findViewById(R.id.rl_action_toggle).setVisibility(View.GONE);
				Show.toast(AcAlertSettings.this,
						getString(R.string.get_parameters_failed));

				finish();
				break;

			case ALARM_SAVE_OK:
				Show.toast(AcAlertSettings.this, R.string.set_ok);
				finish();
				break;
			case ALARM_SAVE_ERROE:
				toggleSwitch.setChecked(!toggleSwitch.isChecked());
				alarmMotion.bIfEnable = alarmMotion.bIfEnable == 1 ? 0 : 1;
				Show.toast(AcAlertSettings.this, R.string.set_fail);
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
		setContentView(R.layout.ac_alert_settings);

		deviceName = getIntent().getStringExtra("deviceName");
		tv_sensor = (TextView) findViewById(R.id.tv_sensor);

		findViewById(R.id.rl_sensor_level).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// if (ppd == null) {
						// ppd = new PtzPickerDialog(AcAlertSettings.this,
						// R.style.MMTheme_DataSheet,
						// getString(R.string.sensor_level),
						// alarmMotion.iLevel,
						// new PtzPickerDialog.OnPTZSetListener() {
						//
						// @Override
						// public void onDateTimeSet(int ptz) {
						// // TODO Auto-generated method stub
						// tv_sensor.setText("" + (ptz + 1));
						// // alarmMotion.iLevel = (ptz + 1);
						//
						// }
						// }, 6);
						// }
						// ppd.show();
					}
				});
		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		findViewById(R.id.save_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				boolean isModify;

				int sensor = Integer.parseInt(tv_sensor.getText().toString()
						.trim());
				if (alarmMotion != null) {
					boolean ifDevAlarmChanged = false;
					if (sensor != alarmMotion.iLevel) {
						ifDevAlarmChanged = true;

						alarmMotion.iLevel = sensor;

					}
					if (toggleSwitch.isChecked() != (alarmMotion.bIfEnable == 1)) {

						ifDevAlarmChanged = true;
						if (toggleSwitch.isChecked()) {
							alarmMotion.bIfEnable = 1;
						} else {
							alarmMotion.bIfEnable = 0;
						}

					}

					if (ifDevAlarmChanged) {
						pd.show();
						new CameraSetAlarmMotionThread().start();
					}

				}

			}
		});
		pd = new ShowProgress(this);
		pd.show();
		pc = new PlayerCore(AcAlertSettings.this);
		new Thread() {

			@Override
			public void run() {

				alarmMotion = new TAlarmMotionDetect();

				// if (pc.CameraGetAlarmMotion(deviceId, alarmMotion) == 0) {
				if (pc.CameraGetAlarmMotionEx(Constants.UMID, Constants.user,
						Constants.password, 0, alarmMotion) == 0) {
					Log.i("TAlarmMotionDetect", "TAlarmMotionDetect sensor:"
							+ alarmMotion.iLevel + ",switch £º"
							+ alarmMotion.bIfEnable);
					if (alarmMotion != null) {
						hander.sendEmptyMessage(GET_AlARM_OK);
					} else
						hander.sendEmptyMessage(GET_AlARM_FAILED);

				} else {
					alarmMotion = null;
					hander.sendEmptyMessage(GET_AlARM_FAILED);
				}

				super.run();
			}
		}.start();
		// fragmentTransaction();
	}

	class CameraSetAlarmMotionThread extends Thread {

		@Override
		public void run() {
			alarmMotion.bFuncEnable_Region = 0;
			if (pc.CameraSetAlarmMotion(Constants.UMID, Constants.user,
					Constants.password, 0, alarmMotion) == 0) {

				hander.sendEmptyMessage(AcAlertSettings.ALARM_SAVE_OK);
			} else {
				hander.sendEmptyMessage(AcAlertSettings.ALARM_SAVE_ERROE);
			}

		}

	}

}
