package com.example.umeyesdk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.Player.Core.PlayerCore;
import com.Player.Source.SDKError;

public class PlayM3u8Activity extends Activity {

    PlayerCore player;
    private ImageView imgLive;
    private ImageButton iv_play;
    private TextView tv_time,txtRec;
    private SeekBar seekBar;
    boolean flag = true;
    private int duration;
    private boolean isSeeking;
    private Button btnSnap, btnVideo;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (isFinishing()) {
                return;
            }
            if (isSeeking) {
                return;
            }

            txtRec.setVisibility(player.GetIsSnapVideo() ? View.VISIBLE : View.GONE);

               int state = player.GetPlayerState();
            if (state == SDKError.Statue_PLAYING) {

                int currentPosition = player.GetPlayFile_CurPlayPos();

                Log.d("currentPosition", currentPosition+"");

                duration = player.GetFileAllTime_Int();
                //让进度条滚动起来
                seekBar.setProgress(currentPosition / 1000 * 1000);
                seekBar.setMax(duration);
                tv_time.setText(generateTime(currentPosition) + "/" + generateTime(duration));

                if(currentPosition >= duration) {
                    iv_play.setImageResource(android.R.drawable.ic_media_play);
                    player.StopAsync();
                } else {
                    iv_play.setImageResource(android.R.drawable.ic_media_pause);
                }

            } else if (state == SDKError.Statue_Ready || state == SDKError.Statue_STOP) {
                seekBar.setProgress(0);
                iv_play.setImageResource(android.R.drawable.ic_media_play);
                tv_time.setText(generateTime(0) + "/" + generateTime(duration));

            }



        }
    };



    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_videoview_hls);
        iv_play = findViewById(R.id.iv_play);
        imgLive = findViewById(R.id.imgLive);
        tv_time = findViewById(R.id.tv_time);
        seekBar = findViewById(R.id.seekBar);

        player = new PlayerCore(this,PlayerCore.HLSSERVER);
        player.InitParam("http://123.207.88.138:5888/tsvod/test.m3u8",imgLive);
        player.SetOpenFFmpegLog(true);
        player.Play();

        new ProgressThread().start();

        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(player.GetPlayerState() == SDKError.Statue_Pause) {
                   player.Resume();
                   iv_play.setImageResource(android.R.drawable.ic_media_pause);

               } else if(player.GetPlayerState() == SDKError.Statue_STOP) {
                   player.Play();
                   iv_play.setImageResource(android.R.drawable.ic_media_pause);
               }
               else {
                   player.Pause();
                   iv_play.setImageResource(android.R.drawable.ic_media_play);
               }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(duration > 0) {
                    tv_time.setText(generateTime(progress) + "/" + generateTime(duration));
                }
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
                player.SeekFilePos(progress/1000,0);
                isSeeking = false;
            }
        });

        btnSnap = findViewById(R.id.btnSnap);
        btnVideo = findViewById(R.id.btnVideo);
        txtRec = (TextView) findViewById(R.id.tvwRec);

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.SetSnapPicture(true);
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.GetIsSnapVideo()) {
                    player.SetSnapVideo(false);
                } else {
                    if (player.GetPlayerState() == SDKError.Statue_PLAYING)
                        player.SetSnapVideo(true);
                }
            }
        });


    }


    class ProgressThread extends Thread {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = false;
        player.StopAsync();
    }
}
