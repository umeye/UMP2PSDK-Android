package com.example.umeyeNewSdk;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.Player.Core.PlayerClient;
import com.Player.Source.LogOut;
import com.Player.Source.TDateTime;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcDevTime extends Activity implements OnClickListener {

	private static final int SET_OK = 0;
	private static final int SET_FALL = 1;
	private static final int GET_FAILED = 2;
	private static final int GET_SUCCEES = 3;
	private AppMain appMain;
	private TDateTime tDateTime;

	public ShowProgress pd;

	private TextView yearEditText, monthEditText, dayEditText, hourEditText,
			minuteEditText, secondEditText;

	private Button saveButton, dateButton, timeButton;

	private TimePickerDialog timePickerDialog;
	private DatePickerDialog datePickerDialog;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			pd.dismiss();
			super.handleMessage(msg);
			switch (msg.what) {

			case SET_OK:
				Show.toast(AcDevTime.this, R.string.set_ok);
				break;
			case SET_FALL:
				Show.toast(AcDevTime.this, R.string.set_fail);
				break;
			case GET_SUCCEES:
				setText();
				break;
			case GET_FAILED:
				Show.toast(AcDevTime.this, R.string.get_failed);
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
		setContentView(R.layout.ac_dev_time);
		appMain = (AppMain) this.getApplicationContext();

		findViewById(R.id.back_btn).setOnClickListener(this);
		yearEditText = (TextView) findViewById(R.id.etYear);
		monthEditText = (TextView) findViewById(R.id.etMonth);
		dayEditText = (TextView) findViewById(R.id.etDay);
		hourEditText = (TextView) findViewById(R.id.etHour);
		minuteEditText = (TextView) findViewById(R.id.etMinute);
		secondEditText = (TextView) findViewById(R.id.etSecond);
		saveButton = (Button) findViewById(R.id.menu_btn1);
		saveButton.setOnClickListener(this);

		dateButton = (Button) findViewById(R.id.set_date);
		dateButton.setOnClickListener(this);

		timeButton = (Button) findViewById(R.id.set_time);
		timeButton.setOnClickListener(this);
		showProgressDialog(getString(R.string.get_dev_time));
		getTimeThread();
	}

	public void showProgressDialog(String message) {
		if (pd == null) {
			pd = new ShowProgress(this);
			pd.setCanceledOnTouchOutside(false);
		}
		pd.setMessage(message);
		pd.show();
	}

	void setText() {
		yearEditText.setText(String.valueOf(tDateTime.iYear));
		monthEditText.setText(String.valueOf(tDateTime.iMonth));
		dayEditText.setText(String.valueOf(tDateTime.iDay));
		hourEditText.setText(String.valueOf(tDateTime.iHour));
		minuteEditText.setText(String.valueOf(tDateTime.iMinute));
		secondEditText.setText(String.valueOf(tDateTime.iSecond));
	}

	void getTimeThread() {
		new Thread() {

			@Override
			public void run() {
				PlayerClient playerClient = appMain.getPlayerclient();
				tDateTime = playerClient.CameraGetDevTime(Constants.UMID,
						Constants.user, Constants.password);
				if (tDateTime != null) {
					LogOut.d("CameraGetDevTime", "CameraGetDevTime:"
							+ tDateTime.toString());
					handler.sendEmptyMessage(GET_SUCCEES);
				} else {
					handler.sendEmptyMessage(GET_FAILED);
				}

			}
		}.start();
	}

	void setTimeThread() {
		new Thread() {

			@Override
			public void run() {
				PlayerClient playerClient = appMain.getPlayerclient();
				int ret = playerClient.CameraSetDevTime(Constants.UMID,
						Constants.user, Constants.password, tDateTime);
				if (ret>0) {
					LogOut.d("CameraGetDevTime", "CameraGetDevTime:"
							+ tDateTime.toString());
					handler.sendEmptyMessage(SET_OK);
				} else {
					handler.sendEmptyMessage(SET_FALL);
				}

			}
		}.start();
	}

	class DateListener implements OnDateSetListener {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			tDateTime.iYear = year;
			tDateTime.iMonth = monthOfYear + 1;
			tDateTime.iDay = dayOfMonth;
			setText();
		}

	}

	class TimeListener implements OnTimeSetListener {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			tDateTime.iHour = hourOfDay;
			tDateTime.iMinute = minute;
			setText();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.set_date:
			datePickerDialog = new DatePickerDialog(this, new DateListener(),
					tDateTime.iYear, tDateTime.iMonth - 1, tDateTime.iDay);
			datePickerDialog.show();
			break;
		case R.id.set_time:
			timePickerDialog = new TimePickerDialog(this, new TimeListener(),
					tDateTime.iHour, tDateTime.iMinute, true);
			timePickerDialog.show();
			break;
		case R.id.menu_btn1:
			showProgressDialog("");
			setTimeThread();
			break;
		default:
			break;
		}

	}
}
