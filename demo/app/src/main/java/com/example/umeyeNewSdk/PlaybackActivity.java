package com.example.umeyeNewSdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.Player.Core.PlayerCore;
import com.Player.Source.LoadProgeressListener;
import com.Player.Source.SDKError;
import com.Player.Source.TDateTime;
import com.Player.web.response.DevItemInfo;
import com.example.extra.utils.SeekTimeBar;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.ShowProgress;

import java.util.Calendar;
import java.util.Date;

@SuppressLint("HandlerLeak")
public class PlaybackActivity extends Activity implements View.OnClickListener {


    public static final byte SHOW_STATE = 0;
    private PlayerCore playerCore;
    private String id = "";
    private ImageView img;
    private TextView txtState, txtRec;
    private ImageButton btnPlay, btnSound, btnSnap, btnVideo;
    public static boolean isRun = true;
    AppMain appMain;

    public TDateTime startTime;
    public TDateTime endTime;
    TDateTime currentTime;

    SeekTimeBar seekTimeBar;

    Button[] btnSeek = new Button[5];
    int[] btnSeekIds = {R.id.btnSeek1, R.id.btnSeek2, R.id.btnSeek3, R.id.btnSeek4, R.id.btnSeek5};

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_STATE) {
                txtState.setText(GetDescription(PlaybackActivity.this, msg.arg1));
                // 是否显示录像
                txtRec.setVisibility(msg.arg2 == 1 ? View.VISIBLE : View.GONE);

                long ti = playerCore.GetCurrentTime_Int();
                int t1 = playerCore.GetCurrentPlayTime_Int();
                Log.i("handleMessage", "时间戳=" + ti);
                if (seekTimeBar != null && playerCore != null) {
                    if (ti > 0) {
                        seekTimeBar.setTime(ti);
                    }
                }

            }
            super.handleMessage(msg);
        }

    };
    private ShowProgress showProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ac_playback);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        startTime = (TDateTime) intent.getSerializableExtra("startTime");
        endTime = (TDateTime) intent.getSerializableExtra("endTime");
        currentTime = (TDateTime) intent.getSerializableExtra("currentTime");
        initeView();
        appMain = (AppMain) this.getApplicationContext();
        initePlayCore();
        if (currentTime == null)
            PlayBack();
        else
            PlayBack(currentTime);
    }

    public void initePlayCore() {
        playerCore = new PlayerCore(this);
        String curConnectParam = DevItemInfo
                .toConnectParams(1009, Constants.UMID, "", 0, Constants.user, Constants.password, 1, 0, 0);
        playerCore.InitParam(curConnectParam, -1, img);
        playerCore.SetOpenLog(false);
        playerCore.isQueryDevInfo = true;

        seekTimeBar.setPlayCoreAndParameters(playerCore,
                startTime, endTime, 0);
        seekTimeBar.setTimeArea(AcSearchRecord.data);
    }


    @Override
    protected void onResume() {
        isRun = true;
        new StateThread().start();
        super.onResume();
    }

    void initeView() {
        seekTimeBar = findViewById(R.id.id__seekbar);
        img = (ImageView) findViewById(R.id.imgLive);
        txtState = (TextView) findViewById(R.id.txt_state);
        txtRec = (TextView) findViewById(R.id.tvwRec);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnSnap = (ImageButton) findViewById(R.id.btnSnap);
        btnVideo = (ImageButton) findViewById(R.id.btnRecord);
        btnSound = (ImageButton) findViewById(R.id.btnSound);
        // 初始化其他控件
        for (int i = 0; i < btnSeekIds.length; i++) {
            btnSeek[i] = findViewById(btnSeekIds[i]);
            btnSeek[i].setOnClickListener(new SelectTimeListener(i + 1));
        }
        btnPlay.setOnClickListener(this);
        btnSnap.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        findViewById(R.id.btnSelectCh).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);

        btnSound.setOnClickListener(this);

    }

    class SelectTimeListener implements View.OnClickListener {
        int position;

        public SelectTimeListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            seekTimeBar.setSeekFlag(true);
            seekTimeBar.setTime(position * 4, 0, 0);

        }
    }

    /**
     * 状态显示线程
     *
     * @author Simula
     */
    class StateThread extends Thread {

        @Override
        public void run() {

            try {
                while (isRun) {
                    Thread.sleep(500);
                    Message msg = new Message();
                    msg.what = SHOW_STATE;
                    msg.arg1 = playerCore.PlayCoreGetCameraPlayerState();
                    if (playerCore.GetIsSnapVideo()) {
                        msg.arg2 = 1;
                    }
                    Log.w("state", "state: " + msg.arg1 + ",playerCore.GetIsPPT():"
                            + playerCore.GetIsPPT());
                    handler.sendMessage(msg);

                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }

    /**
     * 是否初始化播放地址
     */
    public boolean isPrepared() {
        return playerCore != null && !TextUtils.isEmpty(playerCore.DeviceNo);

    }

    /**
     * 连接设备、远程回放
     */
    public void PlayBack() {
        if (isPrepared()) {
            Log.d("PlayTimeFile",
                    "PlayBack 不带参数---------->" + startTime.toString() + "\n"
                            + endTime.toString());
            playerCore.PlayTimeFile(startTime, endTime, 0);
        }
    }

    /**
     * 记录回放位置，下次重连，又从该位置回放
     */
    long seekTimeSec = 0;

    public void PlayBack(final TDateTime seekTime) {
        if (isPrepared()) {
            Log.d("PlayTimeFile",
                    "PlayBack--------->seekTime" + seekTime.toString() + "\n"
                            + endTime.toString());
            playerCore.setLoadProgeressListener(new LoadProgeressListener() {
                @Override
                public void loadProgress(PlayerCore playerCore, int i) {
                    if (i == 90) {
                        long seek = getTimeSec(seekTime) - getTimeSec(startTime);
                        Log.d("PlayTimeFile",
                                "PlayBack ---------->指定时间戳：" + seekTimeSec);
                        if (seek > 0 && seek < 24 * 60 * 60)
                            playerCore.SeekFilePos((int) seek, 0);
                        playerCore.setLoadProgeressListener(null);
                    }
                }
            });
            playerCore.PlayTimeFile(startTime, endTime, 0);
        }
    }

    public static long getTimeSec(TDateTime dateTime) {

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(dateTime.iYear, dateTime.iMonth - 1, dateTime.iDay, dateTime.iHour, dateTime.iMinute, dateTime.iSecond);
        Date date = mCalendar.getTime();
        return date.getTime() / 1000;
    }

    public void Stop() {
        Stop(null);
    }

    public void Stop(final Handler handler) {
        new Thread() {
            @Override
            public void run() {
                playerCore.Stop();
                if (handler != null) {
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.btnBack:
                isRun = false;
                finish();
                break;
            case R.id.btnSelectCh:
                selectChannel();
                break;
            case R.id.btnPlay:
                int PlayerState = playerCore.PlayCoreGetCameraPlayerState();
                if (PlayerState == 6) {
                    playerCore.Resume();
                } else if (PlayerState == 2) {
                    playerCore.Pause();
                } else {
                    PlayBack();
                }
                break;
            case R.id.btnSnap:
                playerCore.SetSnapPicture(true);
                break;
            case R.id.btnRecord:
                if (playerCore.GetIsSnapVideo()) {
                    playerCore.SetSnapVideo(false);
                } else {
                    if (playerCore.GetPlayerState() == SDKError.Statue_PLAYING)
                        playerCore.SetSnapVideo(true);
                }
                break;
            case R.id.btnSound:
                if (playerCore.GetIsVoicePause()) {
                    playerCore.OpenAudio();
                } else {
                    playerCore.CloseAudio();
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onPause() {
        Stop();
        isRun = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // playerCore.Stop();
        isRun = false;
        super.onDestroy();
    }

    /*
     * private static final int NPC_D_MPI_MON_ERROR_USERID_ERROR = -101; //
     * 用户ID或用户名错误 private static final int NPC_D_MPI_MON_ERROR_USERPWD_ERROR =
     * -102; // 用户密码错误 private static final int
     * NPC_D_MPI_MON_ERROR_REJECT_ACCESS = -111; // 权限不够
     */
    public String GetDescription(Context con, int state) {
        String des = con.getString(R.string.connect_fail);
        switch (state) {
            case 0:
                des = con.getString(R.string.ready);
                break;
            case 1:
                des = con.getString(R.string.connecting);
                break;
            case 2:
                des = con.getString(R.string.playing);
                break;
            case 3:
                des = con.getString(R.string.connect_fail);
                break;
            case 4:
                des = con.getString(R.string.stop);
                break;
            case 6:
                des = con.getString(R.string.pause);
                break;
            case 7:
                des = con.getString(R.string.stop);
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_USERID_ERROR:
                des = con.getString(R.string.usererro);
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_USERPWD_ERROR:
                des = con.getString(R.string.passworderro);
                break;
            case 10:
                des = "缓冲中";
                break;
            case SDKError.NPC_D_MPI_MON_ERROR_REJECT_ACCESS:
                des = con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);
                break;
        }
        return des;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            isRun = false;
            finish();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void Vibrate(Context context, long milliseconds) {
        Vibrator vib = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public void selectChannel() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                PlaybackActivity.this);
        String[] items = new String[16];
        for (int i = 0; i < items.length; i++) {
            items[i] = "Play CH " + (i + 1);
        }
        alertBuilder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, final int which) {
                Stop(new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        String curConnectParam = DevItemInfo
                                .toConnectParams(1009, Constants.UMID, "", 0, Constants.user, Constants.password, 1, which, 0);
                        playerCore.InitParam(curConnectParam, -1, img);
                        PlayBack();
                        super.handleMessage(msg);
                    }
                });

            }
        }).setNegativeButton(R.string.negative, null).show();
    }
}