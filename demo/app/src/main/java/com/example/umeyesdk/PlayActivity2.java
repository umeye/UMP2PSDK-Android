package com.example.umeyesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.Player.Core.OnFinishListener;
import com.Player.Core.PlayerCore;
import com.Player.Source.AudioDecodeListener;
import com.Player.Source.SDKError;
import com.Player.Source.StopRecodeVideoListener;
import com.Player.web.websocket.PermissionUtils;
import com.example.umeyesdk.utils.Imagedeal;
import com.getui.demo.AlarmUtils;
import com.mp4.maker.MP4make;
import com.video.h264.DecodeDisplay;
import com.video.h264.OnFrameChangeListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 */
public class PlayActivity2 extends Activity implements OnTouchListener,
        OnClickListener {
    // 云台控制命令
    public static final byte MD_STOP = 0; // 停止
    public static final byte MD_LEFT = 11; // 左
    public static final byte MD_RIGHT = 12; // 右
    public static final byte MD_UP = 9; // 上
    public static final byte MD_DOWN = 10; // 下
    public static final byte ACTION_ZOOMADD = 6; // 拉近
    public static final byte ACTION_ZOOMReduce = 5;// 拉远
    public static final byte ACTION_FOCUSADD = 7;// 焦距+
    public static final byte ACTION_FOCUSReduce = 8;// 焦距减
    public static final byte ACTION_Circle_Add = 13; // 光圈+
    public static final byte ACTION_Circle_Reduce = 14;// 光圈-

    public static final byte SHOW_STATE = 0;
    public static final byte RECONENT = 1;
    private PlayerCore pc;
    private String id = "";
    private ImageView img;
    private TextView txtState, txtRec;
    private boolean isStopCloudCommand = false;
    private ImageButton btnUp, btnDown, btnLeft, btnRight, btnZoomIn,
            btnZoomOut, btnNear, btnFar, btnCircleAdd, btnCircleReduce,
            btnSound;
    private Button btnPlay, btnPause, btnSnap, btnVideo, btnQuality, btnTalk;
    private boolean isRun = true;
    private Imagedeal deal;
    private MyGestureListener myGestureListener;
    private GestureDetector mGestureDector;
    private boolean isSetStream = false;
    private int stream = 1;
    private boolean isStoping = false;
    private Handler handler = new Handler() {

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if(isFinishing()) {
                return;
            }
            if (msg.what == SHOW_STATE) {
                txtState.setText(GetDescription(PlayActivity2.this, msg.arg1));
                // 是否显示录像
                txtRec.setVisibility(msg.arg2 == 1 ? View.VISIBLE : View.GONE);

                btnQuality.setText(stream == 1 ? "标清" : "高清");

                int state = pc.PlayCoreGetCameraPlayerState();

                if(!(state == 0 || state == 1 || state == 2 || state == 4 || state == 10 || state == 7 || state == SDKError.NPC_D_MPI_MON_ERROR_USERID_ERROR
                        || state == SDKError.NPC_D_MPI_MON_ERROR_USERPWD_ERROR || state == SDKError.NPC_D_MPI_MON_ERROR_REJECT_ACCESS)) {
                    Reconnect();
                }

            } else if (msg.what == RECONENT) {

//                pc.Play();


            }

            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main2);
        initeView();
        pc = new PlayerCore(this);
        pc.isQueryDevInfo = true;
        id = getIntent().getStringExtra("id");
        pc.InitParam(id, 0, img);
        pc.SetbCleanLastView(false);
        // 关闭播放日志输出
        pc.SetOpenLog(true);
        pc.setStopRecodeVideoListener(new StopRecodeVideoListener() {

            @Override
            public void finish(boolean isSuccess, String path) {
                // TODO Auto-generated method stub
                Log.d("StopRecodeVideoListener", "isSuccess:" + isSuccess
                        + ",path=" + path);
            }
        });
        // 设置平滑播放
        // pc.SetPlayModel(1);
        // 设置自定义解码，播放线程，使用默认的无需设置

        /*
         * pc.setAudioDecodeListener(new AudioDecodeListener() {
         *
         * @Override public void StartTalk(PlayerCore playercore) { // TODO
         * Auto-generated method stub // 对讲、录音线程 MyRecoredThread myRecoredThread
         * = new MyRecoredThread( playercore); myRecoredThread.start(); }
         *
         * @Override public void StartAudioDecode(PlayerCore playercore,
         * DecodeDisplay decodeDisplay) { // TODO Auto-generated method stub //
         * 音频解码播放线程 MyAudioDecodeThread AudioThreadDecode = new
         * MyAudioDecodeThread( playercore, decodeDisplay);
         * AudioThreadDecode.start(); }
         *
         * @Override public void startVideoDecode(DecodeDisplay arg0) { // TODO
         * Auto-generated method stub // 视频解码线程 MyVideoDecodeThread
         * defualtVideoDecodeThread = new MyVideoDecodeThread( arg0);
         * defualtVideoDecodeThread.start();
         *
         * }
         *
         * });
         */


        //手势操作
        myGestureListener = new MyGestureListener();
        mGestureDector = new GestureDetector(this, myGestureListener);

        deal = Imagedeal.getdeal(img);
        pc.setOnFrameChangeListener(new OnFrameChangeListener() {
            @Override
            public void onFirstFrameArrived() {//第一帧到达事件

            }

            @Override
            public void onFrameSizeChanged(int i, int i1) {//分辨率改变，手势无效因此恢复图像原位，i改变后的帧宽度，i1改变后的帧高度
                if (img != null) {
                    deal = Imagedeal.getdeal(img);
                    deal.resetImageAndMatrix();
                }
            }


        });
        pc.setOnFinishListener(new OnFinishListener() {
            @Override
            public void onComplete() {//收到结束帧事件

            }

            @Override
            public void onError() {//收到帧错误事件

            }
        });


    }



    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            deal.resetImageAndMatrix();//双击还原
            return true;
        }


    }



    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        pc.Play();

        isRun = true;
        new StateThread().start();
        super.onResume();
    }

    void initeView() {
        img = (ImageView) findViewById(R.id.imgLive);
        img.setOnTouchListener(this);
        txtState = (TextView) findViewById(R.id.txt_state);
        txtRec = (TextView) findViewById(R.id.tvwRec);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnSnap = (Button) findViewById(R.id.btnSnap);
        btnVideo = (Button) findViewById(R.id.btnVideo);
        btnQuality = (Button) findViewById(R.id.btnQuality);
        btnTalk = findViewById(R.id.btnTalk);

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnSnap.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnQuality.setOnClickListener(this);
        btnTalk.setOnClickListener(this);
        // 云台控制按钮

        btnUp = (ImageButton) findViewById(R.id.btnUp);
        btnUp.setOnTouchListener(this);
        btnUp.setBackgroundResource(R.drawable.btn_down_0);
        btnDown = (ImageButton) findViewById(R.id.btnDown);
        btnDown.setOnTouchListener(this);

        btnLeft = (ImageButton) findViewById(R.id.btnLeft);
        btnLeft.setOnTouchListener(this);

        btnRight = (ImageButton) findViewById(R.id.btnRight);
        btnRight.setOnTouchListener(this);
    }

    /**
     * 执行云台命令
     *
     * @param action  按钮的上下
     * @param command 云台命令
     */
    private void ExcuteCommand(View btn, int bg0, int bg1, int action,
                               byte command) {
        if (action == MotionEvent.ACTION_DOWN) {
            btn.setBackgroundResource(bg1);
            isStopCloudCommand = false;
            new ColudThread(command).start();

        } else if (action == MotionEvent.ACTION_UP) {

            btn.setBackgroundResource(bg0);
            isStopCloudCommand = true;

        }
    }

    class ColudThread extends Thread {
        private byte cmd;

        public ColudThread(byte cmd) {
            this.cmd = cmd;
        }

        @Override
        public void run() {
            while (!isStopCloudCommand
                    && pc.GetPlayerState() == SDKError.Statue_PLAYING) {

                pc.SetPtz(cmd, 5);
                System.out.println("发送云台命令：" + cmd);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                pc.SetPtz(MD_STOP, 0);
            }
            System.out.println("停止命令");
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
                    msg.arg1 = pc.PlayCoreGetCameraPlayerState();
                    if (pc.GetIsSnapVideo()) {
                        msg.arg2 = 1;
                    }
                    Log.w("GetIsPPT", "GetIsPPT: " + pc.GetIsPPT());
                    handler.sendMessage(msg);

                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

    }









    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnPlay:

                pc.Play();

                break;
            case R.id.btnPause:

//                new Thread() {
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        pc.Stop();
////						handler.post(new Runnable() {
////							@Override
////							public void run() {
////								pc.Play();
////							}
////						});
//                    }
//
//                }.start();


//                Stop();

                pc.StopAsync();

                break;
            case R.id.btnSnap:// 默认路径 //sdcard/snapShot
                // 判断SDCard
                // .......
                // pc.SetAlbumPath(TempALBUM_PATH); 设置图片保存路径
                // pc.SetSnapPicture(SnapPicture, TempFilenamePrefix),截图，设置图片保存名称
                 pc.SetSnapPicture(true);

                break;
            case R.id.btnVideo:// 默认路径 //sdcard/snapShot
                // 判断SDCard
                // .......
                // pc.SetVideoPath(TempVIDEO_PATH);
                if (pc.GetIsSnapVideo()) {
                    pc.SetSnapVideo(false);
                } else {
                    pc.SetSnapVideo(true);
                }

                break;
            case R.id.btnQuality:
                deal.resetImageAndMatrix();
                setMediaStreamType();
                break;
            case R.id.btnTalk:
                if (!PermissionUtils.checkRecordePermission(this)) {
                    PermissionUtils.verifyRecordePermissions(this, 1);
                } else {
                    ppt();
                }
                break;
            default:
                break;
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ppt();
                }
        }
    }





    private void ppt() {
        if (pc.GetIsPPT()) {
            pc.StopPPTAudio();
            btnTalk.setBackgroundResource(R.drawable.ch_talk);
        } else {
//            pc.SetPPtMode(true);//是否双工模式
            pc.StartPPTAudio();
            btnTalk.setBackgroundResource(R.drawable.ch_talk_h);
        }
    }


    private int CameraSwitchChannel() {
        // TODO Auto-generated method stub
        int ret = -1;
        if (pc.PlayCoreGetCameraPlayerState() == 2) {
            stream = getStreamType();

            if (stream == 1) {
                stream = 0;
            } else {
                stream = 1;
            }
            Log.d("CameraSwitchChannel", "CameraSwitchChannel:" + stream);
            ret = pc.CameraSwitchChannel(stream);
        }
        return ret;
    }



    private void setMediaStreamType() {
        if (pc.PlayCoreGetCameraPlayerState() == 2) {
            if (!isSetStream) {
                new Thread() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        isSetStream = true;
                        CameraSwitchChannel();
                        isSetStream = false;
                        super.run();
                    }
                }.start();
            }

        } else {
            if (stream == 1) {
                stream = 0;
            } else {
                stream = 1;
            }
            pc.setMediaStreamType(stream);
            Reconnect();
        }
    }


    private void Stop() {
        if (isStoping) {
            return;
        }
        isStoping = true;
        new Thread() {

            @Override
            public void run() {

                pc.Stop();
                isStoping = false;

            }
        }.start();
    }


    private void Reconnect() {
//        if (isStoping) {
//            return;
//        }
//        isStoping = true;
//        new Thread() {
//            @Override
//            public void run() {
//
//                pc.Stop();
//                isStoping = false;
//                handler.sendEmptyMessage(RECONENT);
//
//            }
//        }.start();
        pc.StopAsync();
        pc.Play();
    }



    private int getStreamType() {
        int ret = 1;
        if (pc != null) {
            if (pc.tDevNodeInfor != null) {
                return pc.tDevNodeInfor.streamtype;
            }

        }
        return ret;
    }




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch (v.getId()) {
            case R.id.btnUp:
                ExcuteCommand(btnUp, R.drawable.btn_up_0, R.drawable.btn_up_1,
                        action, MD_UP);
                break;
            case R.id.btnDown:
                ExcuteCommand(btnDown, R.drawable.btn_down_0,
                        R.drawable.btn_down_1, action, MD_DOWN);
                break;
            case R.id.btnLeft:
                ExcuteCommand(btnLeft, R.drawable.btn_left_0,
                        R.drawable.btn_left_1, action, MD_LEFT);
                break;
            case R.id.btnRight:
                ExcuteCommand(btnRight, R.drawable.btn_right_0,
                        R.drawable.btn_right_1, action, MD_RIGHT);
                break;
            case R.id.imgLive:
                mGestureDector.onTouchEvent(event);
                if (deal.set(img, event, null)) {
                    deal.resetImageAndMatrix();
                }
                break;

        }
        return true;

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        isRun = false;
        handler.removeCallbacksAndMessages(null);
//        if (pc != null) {
//            pc.Stop();
//        }
//        Stop();

        pc.StopAsync();


        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // pc.Stop();
        isRun = false;

//        if (pc != null) {
//            pc.Stop();
//        }
//        Stop();

        pc.StopAsync();

        super.onDestroy();
    }

    // public static String GetDescription(Context con, int state) {
    // String des = con.getString(R.string.error_code) + ":" + state;
    // switch (state) {
    // case SDKError.Statue_Ready:
    // des = con.getString(R.string.ready);
    // break;
    // case SDKError.Statue_PLAYING:
    // des = con.getString(R.string.playing);
    // break;
    // case SDKError.Statue_STOP:
    // des = con.getString(R.string.stop);
    // break;
    // case SDKError.Statue_ConnectingServer:
    // des = con.getString(R.string.connecting);
    // break;
    // case SDKError.Statue_ConnectingSucess:
    // des = con.getString(R.string.connect_success);
    // break;
    // case SDKError.Statue_ConnectFail:
    // des = con.getString(R.string.connect_fail);
    // break;
    // case SDKError.NET_LOGIN_ERROR_PASSWORD:
    // des = con.getString(R.string.passworderro);
    // break;
    // case SDKError.NET_LOGIN_ERROR_TIMEOUT:
    // des = con.getString(R.string.time_out);
    // break;
    // case SDKError.NET_LOGIN_ERROR_USER:
    // des = con.getString(R.string.usererro);
    // break;
    //
    // case SDKError.Unknow:
    // des = con.getString(R.string.unknown_error);
    // break;
    // case SDKError.NET_ERROR:
    // des = con.getString(R.string.net_error);
    // break;
    // case SDKError.NET_NODATA_ERROR:
    // des = con.getString(R.string.no_data);
    //
    // case SDKError.Exception_ERROR:
    // des = con.getString(R.string.exception_error);
    // break;
    // case SDKError.NosupportDevice_ERROR:
    // des = con.getString(R.string.unsupport_device);
    // break;
    // case SDKError.Beyondmaxchannels_ERROR:
    // des = con.getString(R.string.max_channel);
    // break;
    // }
    // return des;
    // }
    public String GetDescription(Context con, int state) {
        Log.i("GetDescription", "GetDescription:" + state);
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
            case 10:
                des = con.getString(R.string.buffering);
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
            case SDKError.NPC_D_MPI_MON_ERROR_REJECT_ACCESS:
                des = con.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);
                break;
            case -112:
                des = con.getString(R.string.NPC_D_MPI_MON_ERROR_CAMERA_OFFLINE);
                break;
        }
        des = des + "             " + pc.GetPlayFrameRate() + "fps";
        return des;

    }
}
