package com.example.umeyeNewSdk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.Player.Core.PlayerSearchCore;
import com.Player.Source.Date_Time;
import com.Player.Source.TVideoFile;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;

/**
 * 搜索远程回放
 * */
public class AcSearchRecord extends Activity implements OnItemSelectedListener {
	private final int SEARCH_FINISH = 1;// 搜索完成的标志
	private PlayerSearchCore SearchCore = null;
	private String deviceId;

	private TextView tvTitle;
	private Button btnStartDate, btnEndDate, btnStartTime, btnEndTime;

	private Button btnSearch;
	private Spinner spType;

	private Date_Time startTime, endTime;
	private ProgressDialog pdLoading;
	public static List<TVideoFile> data;
	private MyHandler handler;
	private ArrayAdapter<String> adapter;
	private int recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_ALL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_search_record);

		deviceId = getIntent().getStringExtra("deviceId");

		data = new ArrayList<TVideoFile>();

		OnClick onClick = new OnClick();

		startTime = new Date_Time();
		startTime.hour = 0;
		startTime.minute = 0;
		startTime.second = 0;

		endTime = new Date_Time();
		endTime.hour = 0;
		endTime.minute = 0;
		endTime.second = 0;

		Date d = new Date();

		Calendar c = Calendar.getInstance();
		c.setTime(d);

		endTime.year = (short) c.get(Calendar.YEAR);
		endTime.month = (short) (c.get(Calendar.MONTH) + 1);
		endTime.day = (byte) c.get(Calendar.DAY_OF_MONTH);
		endTime.hour = (byte) c.get(Calendar.HOUR_OF_DAY);
		endTime.minute = (byte) c.get(Calendar.MINUTE);
		endTime.second = (byte) c.get(Calendar.SECOND);

		// c.add(Calendar.DAY_OF_MONTH, -1);// 得到前一天

		startTime.year = (short) c.get(Calendar.YEAR);
		startTime.month = (short) (c.get(Calendar.MONTH) + 1);
		startTime.day = (byte) (c.get(Calendar.DAY_OF_MONTH));

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(Constants.UMID + "");
		btnStartDate = (Button) findViewById(R.id.btnStartDate);
		btnStartDate.setOnClickListener(onClick);
		findViewById(R.id.btnBack).setOnClickListener(onClick);
		setDateTime(startTime, btnStartDate, false);

		btnEndDate = (Button) findViewById(R.id.btnEndDate);
		btnEndDate.setOnClickListener(onClick);
		setDateTime(endTime, btnEndDate, false);

		btnStartTime = (Button) findViewById(R.id.btnStartTime);
		btnStartTime.setOnClickListener(onClick);
		setDateTime(startTime, btnStartTime, true);

		btnEndTime = (Button) findViewById(R.id.btnEndTime);
		btnEndTime.setOnClickListener(onClick);
		setDateTime(endTime, btnEndTime, true);

		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(onClick);
		spType = (Spinner) findViewById(R.id.spEvent);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.record_event_type));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// for (int i = 0; i < infoList.size(); i++) {
		// adapter.add(infoList.get(i).getName());
		// adapter.
		// }
		spType.setAdapter(adapter);
		spType.setOnItemSelectedListener(this);
		spType.setSelection(0);
		if (getIntent().getBooleanExtra("isDownLoad", false)
				&& !getIntent().getBooleanExtra("isvideo", false)) {// 判断是否是下载，且是否是视频,如果不是视频的话
																	// ，搜索图片
			recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_PIC_ALL;
			findViewById(R.id.rlevent).setVisibility(View.GONE);
		}
	}

	public class OnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.btnStartDate:
				ShowDateDialog(R.id.btnStartDate);
				break;
			case R.id.btnEndDate:
				ShowDateDialog(R.id.btnEndDate);
				break;
			case R.id.btnStartTime:
				ShowTimeDialog(R.id.btnStartTime);
				break;
			case R.id.btnEndTime:
				ShowTimeDialog(R.id.btnEndTime);
				break;
			case R.id.btnSearch:
				StartSearch();
				break;
			case R.id.btnBack:
				finish();
			}
		}

	}

	public class DateCallBack implements DatePickerDialog.OnDateSetListener {
		private int which;

		public DateCallBack(int which) {
			this.which = which;
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			String s = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
			if (which == R.id.btnStartDate) {
				startTime.year = (short) year;
				startTime.month = (short) (monthOfYear + 1);
				startTime.day = (byte) dayOfMonth;

				btnStartDate.setText(s);
			} else {
				endTime.year = (short) year;
				endTime.month = (short) (monthOfYear + 1);
				endTime.day = (byte) dayOfMonth;

				btnEndDate.setText(s);
			}

		}
	}

	public class TimeCallBack implements TimePickerDialog.OnTimeSetListener {
		private int which;

		public TimeCallBack(int which) {
			this.which = which;
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (which == R.id.btnStartTime) {
				startTime.hour = (byte) hourOfDay;
				startTime.minute = (byte) minute;
				setDateTime(startTime, btnStartTime, true);
			} else {
				endTime.hour = (byte) hourOfDay;
				endTime.minute = (byte) minute;
				setDateTime(endTime, btnEndTime, true);
			}
		}
	}

	/**
	 * 显示时间选择对话在框
	 */
	public void ShowTimeDialog(int which) {

		TimePickerDialog dialog;
		if (which == R.id.btnStartTime) {
			dialog = new TimePickerDialog(this, new TimeCallBack(which),
					startTime.hour, startTime.minute, true);
		} else {
			dialog = new TimePickerDialog(this, new TimeCallBack(which),
					endTime.hour, endTime.minute, true);
		}
		dialog.show();
	}

	/**
	 * 显示日期选择对话框
	 */
	public void ShowDateDialog(int which) {
		DatePickerDialog dialog;
		if (which == R.id.btnStartDate) {
			dialog = new DatePickerDialog(this, new DateCallBack(which),
					startTime.year, startTime.month - 1, startTime.day);
		} else {
			dialog = new DatePickerDialog(this, new DateCallBack(which),
					endTime.year, endTime.month - 1, endTime.day);
		}
		dialog.show();
	}

	public Date_Time getDateTime(Date dt) {
		Date_Time result = new Date_Time();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		result.year = (short) c.get(Calendar.YEAR);
		result.month = (short) (c.get(Calendar.MONTH) + 1);
		result.day = (byte) c.get(Calendar.DAY_OF_MONTH);
		result.hour = (byte) c.get(Calendar.HOUR_OF_DAY);
		result.minute = (byte) c.get(Calendar.MINUTE);
		result.second = (byte) c.get(Calendar.SECOND);

		return result;
	}

	public void setDateTime(Date_Time dt, Button button, boolean isTime) {
		if (isTime) {
			String hour = "";
			String minute = "";
			if (dt.hour < 10)
				hour = "0" + dt.hour;
			else
				hour = String.valueOf(dt.hour);
			if (dt.minute < 10)
				minute = "0" + dt.minute;
			else
				minute = String.valueOf(dt.minute);

			button.setText(hour + ":" + minute);
		} else {
			button.setText(dt.year + "-" + dt.month + "-" + dt.day);
		}
	}

	/**
	 * 检查结束时间是否大于开始时间,正确返回true,错误返回false
	 * 
	 * @return
	 */
	private boolean CheckTime() {
		if (startTime.year > endTime.year) {
			return false;
		} else if (startTime.year == endTime.year) {
			if (startTime.month > endTime.month)
				return false;
			else if (startTime.month == endTime.month) {
				if (startTime.day > endTime.day)
					return false;
				else if (startTime.day == endTime.day) {
					int start = startTime.hour * 60 + startTime.minute;
					int end = endTime.hour * 60 + endTime.minute;
					if (start >= end) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public void StartSearch() {
		if (!CheckTime()) {
			Toast.makeText(this, R.string.start_big_end, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		handler = new MyHandler();

		pdLoading = new ProgressDialog(this);
		pdLoading.setTitle(R.string.load);
		pdLoading.setMessage(getResources().getString(R.string.searching));
		pdLoading.setCanceledOnTouchOutside(false);
		pdLoading.show();

		data.clear();
		new SearchThread().start();
	}

	private class SearchThread extends Thread {
		@Override
		public void run() {
			Search();
		}
	}

	private class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case SEARCH_FINISH:
				pdLoading.dismiss();
				if (data.size() == 0) {
					Toast.makeText(AcSearchRecord.this,
							R.string.not_found_record, Toast.LENGTH_SHORT)
							.show();
				} else// 打开新的页面
				{
					Intent intent = null;
					if (getIntent().getBooleanExtra("isDownLoad", false)) {
						// 下载用的界面
						intent = new Intent(AcSearchRecord.this,
								AcSearchDownLoadList.class);
					} else {
						// 直接回放用的列表界面
						intent = new Intent(AcSearchRecord.this,
								AcSearchRecordResult.class);
					}
					intent.putExtra("title", getIntent()
							.getStringExtra("title"));
					intent.putExtra("deviceId",
							getIntent().getStringExtra("deviceId"));
					startActivity(intent);
					finish();
				}
				break;
			}
		}
	}

	/**
 * 
 */
	public void Search() {
		// deviceId="18100000";
		// deviceId="14100001";
		int RecFilenum = 0;
		SearchCore = new PlayerSearchCore(this);
		// int ret = SearchCore.SearchRecFileEx(deviceId, startTime, endTime,
		// recordType);// 18100000返回1或-1
		int ret = SearchCore.SearchRecFileEx(Constants.UMID, Constants.user,
				Constants.password, Constants.iChNo, startTime, endTime,
				recordType);
		//
		// int ret =
		// SearchCore.SearchRecFileEx(2060,"192.168.10.247",34567,Constants.user,
		// Constants.password, 0,1,startTime, endTime, recordType);

		System.out.println("查找设备号：" + Constants.UMID + "(" + startTime.hour
				+ ":" + startTime.minute + "--" + endTime.hour + ":"
				+ endTime.minute + ")" + ",recordType:" + recordType);
		if (ret > 0) {
			// TVideoFile videofile1;
			while (true) {
				TVideoFile videofile = SearchCore.GetNextRecFile(); // 获取录像文件
				if (videofile == null) {
					System.out.println("查找结点结束");
					break;
				} else {
					RecFilenum++;
					data.add(Copy(videofile));
					String starttime = videofile.shour + ":"
							+ videofile.sminute + ":" + videofile.ssecond;
					String endtime = videofile.ehour + ":" + videofile.eminute
							+ ":" + videofile.esecond;
					System.out.println("videofilename is:" + videofile.FileName
							+ " " + videofile.nFileSize + " " + starttime
							+ "--" + endtime + "  时长：" + GetLen(videofile));
				}
			}

		}
		SearchCore.Release();
		handler.sendEmptyMessage(SEARCH_FINISH);
	}

	public int GetLen(TVideoFile t) {
		int day = (t.eday - t.sday) * 24 * 3600;
		int hour = (t.ehour - t.shour) * 3600;
		int minute = (t.eminute - t.sminute) * 60;
		int second = (t.esecond - t.ssecond);
		return day + hour + minute + second;
	}

	public TVideoFile Copy(TVideoFile v1) {
		TVideoFile v2 = new TVideoFile();
		v2.Channel = v1.Channel;
		v2.eday = v1.eday;
		v2.ehour = v1.ehour;
		v2.eminute = v1.eminute;
		v2.emonth = v1.emonth;
		v2.eminute = v1.eminute;
		v2.esecond = v1.esecond;
		v2.eyear = v1.eyear;
		v2.FileName = v1.FileName;
		v2.nFileSize = v1.nFileSize;
		v2.nFileType = v1.nFileType;
		v2.nParam1 = v1.nParam1;
		v2.nParam2 = v1.nParam2;
		v2.sday = v1.sday;
		v2.shour = v1.shour;
		v2.sminute = v1.sminute;
		v2.smonth = v1.smonth;
		v2.ssecond = v1.ssecond;
		v2.syear = v1.syear;

		return v2;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0:
			recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_ALL;
			break;
		case 1:
			recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_GENERAL;
			break;
		case 2:
			recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_MALUAL;
			break;
		case 3:
			recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_ALARM;
			break;
		case 4:
			recordType = PlayerSearchCore.NPC_D_MON_REC_FILE_TYPE_PROBE;
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
