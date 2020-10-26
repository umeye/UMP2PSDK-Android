package com.example.umeyesdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.Player.Core.PlayerLocalFileCore;
import com.Player.Source.SDKError;
import com.Player.Source.TMp4FileInfo;

import java.util.Timer;
import java.util.TimerTask;

public class AcVideoPlayback extends Activity {
    private PlayerLocalFileCore player;
    private SeekBar seekBar, fps;
    private SurfaceView sv_main_surface;
    private String fileName;
    private TextView tv_time;
    private ImageButton iv_play;
    private boolean flag = false;
    private boolean isSeeking = false;
    private TMp4FileInfo tmpMp4FileInfo;
    private boolean isPaused = false;
    private int duration;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            if (isSeeking) {
                return;
            }

            if (player.GetPlayerState() == SDKError.Statue_PLAYING) {

                int currentPosition = player.getCurrentPosition();
                duration = player.getDuration();
                //让进度条滚动起来
                seekBar.setProgress(currentPosition / 1000 * 1000);
                seekBar.setMax(duration);
                tv_time.setText(generateTime(currentPosition) + "/" + generateTime(duration));

            } else if (player.GetPlayerState() == SDKError.Statue_Ready) {
                seekBar.setProgress(0);
                iv_play.setImageResource(android.R.drawable.ic_media_play);
                tv_time.setText(generateTime(0) + "/" + generateTime(duration));

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_videoview);
        iv_play = findViewById(R.id.iv_play);
        seekBar = findViewById(R.id.seekBar);
        fps = findViewById(R.id.fps);
        fps.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setPlaySpeed(seekBar.getProgress());
            }
        });

        sv_main_surface = findViewById(R.id.sv_main_surface);
        tv_time = findViewById(R.id.tv_time);


        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        player = new PlayerLocalFileCore(this);

        fileName = getIntent().getStringExtra("fileName");
        player.InitParam(fileName, sv_main_surface);

        duration = player.getDuration();
        tmpMp4FileInfo = player.GetMp4FileInfoEx();
        if (tmpMp4FileInfo != null) {
            System.out.println(player.GetCurrentPlayTime() + "信息:帧"
                    + tmpMp4FileInfo.fps + ",长度" + tmpMp4FileInfo.totaltime
                    + ",宽" + tmpMp4FileInfo.width + "x高"
                    + tmpMp4FileInfo.height);
            fps.setProgress(tmpMp4FileInfo.fps);
            fps.setMax(tmpMp4FileInfo.fps * 2);
        }





        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //获取拖动结束之后的位置
                int progress = seekBar.getProgress();
                //跳转到某个位置播放
                player.SeekFilePos(progress);
                isSeeking = false;
            }
        });

        player.Play();


    }


    private void setPlaySpeed(int fps) {
        if(fps <= 0) {
            fps = 1;
        }
        player.ControlMp4PlaySpeed(fps);

        duration = player.getDuration();
        seekBar.setMax(duration);

        int progress = player.getCurrentPosition();
        seekBar.setProgress(progress);

        tv_time.setText(generateTime(progress) + "/" + generateTime(duration));
    }


    @Override
    protected void onPause() {
        super.onPause();
        //把图标变为播放图标
        iv_play.setImageResource(android.R.drawable.ic_media_play);
        flag = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isPaused) {
            // 把图标变为暂停图标
            iv_play.setImageResource(android.R.drawable.ic_media_pause);
            if (!flag) {
                flag = true;
                new MyThread().start();
            }
        }
    }

    public void play() {
        if (player.GetPlayerState() == SDKError.Statue_PLAYING) {
            player.Pause();
            //把图标变为播放图标
            iv_play.setImageResource(android.R.drawable.ic_media_play);
            isPaused = true;
        } else {
            if (player.IsPausing) {
                player.Resume();
            } else {
                player.Play();
            }
            //把图标变为暂停图标
            iv_play.setImageResource(android.R.drawable.ic_media_pause);
            isPaused = false;
            if (!flag) {
                flag = true;
                new MyThread().start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.Release();
        flag = false;
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (flag) {
                //获取当前位置音乐播放的位置
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        player.changeVideoSize();
    }


    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            super.onBackPressed();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }


}
