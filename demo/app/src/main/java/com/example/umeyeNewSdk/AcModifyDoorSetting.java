package com.example.umeyeNewSdk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.Player.Core.PlayerClient;
import com.alibaba.fastjson.JSON;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.entity.DoorResponse;
import com.example.umeyesdk.entity.PlayNode;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.ShowProgress;

public class AcModifyDoorSetting extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	public static final int GET_SUCCESS = 1;

	public static final int GET_FAIL = 2;

	public static final int SET_SUCCESS = 3;

	public static final int SET_FAIL = 4;

	AppMain app;

	String devId = "";

	EditText et_door_pwd, et_lock_pwd;

	Spinner sp_pir_time, sp_tamper, sp_audio, sp_power_mode;

	private CheckBox ibtn_hide, ibtn_hide1;

	Button btn_save, btn_back;

	ShowProgress progress;

	PlayNode node;

	String[] arryPirtTime, arrayPowerMode;

	ToggleButton tg_pir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_modify_door);
		initData();
		initViews();
		getDevInfo(devId);
	}

	void initData() {
		app = (AppMain) getApplication();
		devId = getIntent().getStringExtra("devId");
		node = (PlayNode) getIntent().getSerializableExtra("node");
		arryPirtTime = getResources().getStringArray(R.array.pir_time);
		arrayPowerMode = getResources()
				.getStringArray(R.array.array_power_mode);

	}

	void initViews() {
		et_door_pwd = (EditText) findViewById(R.id.et_door_pwd);
		et_lock_pwd = (EditText) findViewById(R.id.et_lock_pwd);
		et_lock_pwd.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		sp_pir_time = (Spinner) findViewById(R.id.sp_pir_time);
		sp_power_mode = (Spinner) findViewById(R.id.sp_power_mode);
		tg_pir = (ToggleButton) findViewById(R.id.sp_pir);
		sp_tamper = (Spinner) findViewById(R.id.sp_alarm);
		sp_audio = (Spinner) findViewById(R.id.sp_audio);
		btn_back = (Button) findViewById(R.id.back_btn);
		btn_back.setOnClickListener(this);

		ibtn_hide = (CheckBox) findViewById(R.id.ibtn_hide);
		ibtn_hide.setOnCheckedChangeListener(this);

		ibtn_hide1 = (CheckBox) findViewById(R.id.ibtn_hide1);
		ibtn_hide1.setOnCheckedChangeListener(this);

		btn_save = (Button) findViewById(R.id.btn_add_device);
		btn_save.setOnClickListener(this);

		progress = new ShowProgress(this);
		progress.setCancelable(false);
		progress.setCanceledOnTouchOutside(false);
		// et_lock_pwd.setOnEditorActionListener(this);
		et_lock_pwd.addTextChangedListener(watcher);
		tg_pir.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					findViewById(R.id.rl_pir_time).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.rl_pir_time).setVisibility(View.GONE);
				}
			}
		});
	}

	void getDevInfo(final String devId) {
		progress.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				PlayerClient client = app.getPlayerclient();// new
				// PlayerClient();
				String request = "{\"Operation\":1,\"Request_Type\":0}";
				byte[] b_request = request.getBytes();
				byte[] result = client.CallCustomFunc(Constants.UMID,
						Constants.user, Constants.password, 66052, b_request);
				Message msg = Message.obtain();
				msg.what = GET_FAIL;
				if (null != result) {
					String reStr = new String(result);
					// String reString = new String(Base64.decode(result,
					// Base64.DEFAULT));// new
					// String(result);
					Log.d("getDevInfo", "" + reStr);
					msg.what = GET_SUCCESS;
					msg.obj = reStr;
				}
				handler.sendMessage(msg);
			}
		}).start();
	}

	private Handler toastHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			progress.dismiss();
			switch (msg.what) {
				case 0:
					Toast.makeText(AcModifyDoorSetting.this,
							R.string.input_error_contains_space, Toast.LENGTH_SHORT)
							.show();
					break;
				case 1:
					Toast.makeText(AcModifyDoorSetting.this,
							R.string.input_error_length_error, Toast.LENGTH_SHORT)
							.show();
					break;

			}
			return false;
		}
	});

	private String doorPwd = "";

	private String lockPwd = "";

	private boolean checkInput() {
		doorPwd = et_door_pwd.getText().toString();// .trim();

		lockPwd = et_lock_pwd.getText().toString();// .trim();

		if (doorPwd.contains(" ") || lockPwd.contains(" ")) {
			toastHandler.sendEmptyMessage(0);
			return false;
		}
		if (lockPwd.length() != 8) {
			toastHandler.sendEmptyMessage(1);
			return false;
		}
		return true;
	}

	void setDevInfo() {
		progress.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// String doorPwd = et_door_pwd.getText().toString().trim();
				// String lockPwd = et_lock_pwd.getText().toString().trim();
				// Base64.encode(doorPwd, Base64.URL_SAFE);
				if (!checkInput()) {
					return;
				}
				byte[] b_doorPwd = Base64.encode(doorPwd.getBytes(),
						Base64.DEFAULT);
				byte[] b_lockPwd = Base64.encode(lockPwd.getBytes(),
						Base64.DEFAULT);

				String s_doorPwd = new String(b_doorPwd);
				String s_lockPwd = new String(b_lockPwd);

				int openPir = tg_pir.isChecked() ? 1 : 0;
				int openAlarm = sp_tamper.getSelectedItemPosition();
				String pir_time = (String) sp_pir_time.getSelectedItem();
				String powerMode = String.valueOf(sp_power_mode
						.getSelectedItemPosition() + 1);
				int openAudio = sp_audio.getSelectedItemPosition();
				String requestStr = "{\"Operation\":2,\"Request_Type\":1,\"Value\":{\"Dev_Psw\":\""
						+ s_doorPwd
						+ "\",\"Lock_Psw\":\""
						+ s_lockPwd
						+ "\",\"Pir_State\":"
						+ openPir
						+ ",\"Pir_Time\":"
						+ pir_time
						+ ",\"Power_Mode\":"
						+ powerMode
						+ ",\"Tamper_State\":"
						+ openAlarm
						+ ",\"Door_Audio\":"
						+ openAudio + "}}";
				Log.e("Test", requestStr);
				byte[] result = app.getPlayerclient().CallCustomFunc(
						Constants.UMID, Constants.user, Constants.password,
						66052, requestStr.getBytes());
				if (null != result) {
					String re = Base64.encodeToString(result, Base64.DEFAULT);// new
					// String(result);
					handler.sendEmptyMessage(SET_SUCCESS);
					Log.e("Test", "result:" + re);
					return;
				}
				handler.sendEmptyMessage(SET_FAIL);
			}
		}).start();
	}

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			progress.dismiss();
			switch (msg.what) {
				case GET_SUCCESS:
					DoorResponse response = JSON.parseObject(msg.obj.toString(),
							DoorResponse.class);
					String doorPwd = new String(Base64.decode(response.getValue()
							.getDev_Psw(), Base64.DEFAULT));
					String lockPwd = new String(Base64.decode(response.getValue()
							.getLock_Psw(), Base64.DEFAULT));// TURBd01EQXdNREE9Cg==
					et_door_pwd.setText(doorPwd);
					et_lock_pwd.setText(lockPwd);
					sp_tamper.setSelection(response.getValue().getTamper_State());
					tg_pir.setChecked(response.getValue().getPir_State() == 1 ? true
							: false);

					int position = 0;
					String pirTime = response.getValue().getPir_time();
					if (!TextUtils.isEmpty(pirTime)) {
						for (int i = 0; i < arryPirtTime.length; i++) {
							if (arryPirtTime[i].equals(pirTime)) {
								position = i;
								break;
							}
						}
					}
					sp_pir_time.setSelection(position);
					sp_audio.setSelection(response.getValue().getDoor_Audio());
					String powerModeStr = response.getValue().getPower_Mode();
					int powerMode = 0;
					try {
						powerMode = Integer.parseInt(powerModeStr);
					} catch (NumberFormatException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					sp_power_mode.setSelection(powerMode - 1);
					Log.e("Test", "result:" + response.toString());

					break;
				case GET_FAIL:
					Toast.makeText(AcModifyDoorSetting.this,
							R.string.get_parameters_failed, Toast.LENGTH_SHORT)
							.show();
					finish();
					break;
				case SET_SUCCESS:
					Toast.makeText(AcModifyDoorSetting.this,
							R.string.modify_success, Toast.LENGTH_SHORT).show();
					finish();
					break;
				case SET_FAIL:
					Toast.makeText(AcModifyDoorSetting.this, R.string.set_fail,
							Toast.LENGTH_SHORT).show();
					break;
			}
			return false;
		}
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.btn_add_device:
				setDevInfo();
				break;
		}
	}

	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {
			Log.e("TextWatcher", "onTextChanged  s:" + s + " start:" + start
					+ " before:" + before + " count:" + count);
			if (s.length() > 8) {
				s = s.subSequence(0, 8);
				et_lock_pwd.setText(s);
				et_lock_pwd.setSelection(8);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {
			// Log.e("TextWatcher", "beforeTextChanged s:" + s + " start:" +
			// start + " after:" + after + " count:" + count);
			// if (start + after > 8)
			// {
			// s = s.subSequence(0, 8);
			// return;
			// }
		}

		@Override
		public void afterTextChanged(Editable s) {
			Log.e("TextWatcher", "afterTextChanged");
		}
	};

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.ibtn_hide) {
			if (isChecked) {
				et_lock_pwd.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			} else {
				et_lock_pwd.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
		} else {
			if (isChecked) {
				et_door_pwd.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			} else {
				et_door_pwd.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
		}
	}
}
