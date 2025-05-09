package com.example.umeyesdk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.Player.Core.PlayerCore;
import com.Player.Source.SDKError;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PlayM3u8Activity extends Activity {

    PlayerCore player;
    private ImageView imgLive;
    private ImageButton iv_play;
    private TextView tv_time, txtRec, txtState;
    private SeekBar seekBar;
    boolean flag = true;
    private int currentPosition, duration;
    private boolean isSeeking;
    private Button btnSnap, btnVideo;
    Handler handler = new Handler() {
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

            Log.d("state", state + "");

            currentPosition = player.GetPlayFile_CurPlayPos();
            if (state == SDKError.Statue_PLAYING || state == SDKError.Statue_ConnectingSucess) {

                duration = player.GetFileAllTime_Int();
                //让进度条滚动起来
                seekBar.setProgress(currentPosition / 1000 * 1000);
                seekBar.setMax(duration);
                tv_time.setText(generateTime(currentPosition) + "/" + generateTime(duration));

                iv_play.setImageResource(android.R.drawable.ic_media_pause);
                if (duration > 0 && currentPosition >= duration) {
                    player.StopAsync();
                    seekBar.setProgress(0);
                    tv_time.setText(generateTime(0) + "/" + generateTime(duration));
                    iv_play.setImageResource(android.R.drawable.ic_media_play);
                }


            } else if (state == SDKError.Statue_Ready || state == SDKError.Statue_STOP) {
                seekBar.setProgress(0);
                iv_play.setImageResource(android.R.drawable.ic_media_play);
                tv_time.setText(generateTime(0) + "/" + generateTime(duration));

            } else if (state == SDKError.Statue_Pause) {
                iv_play.setImageResource(android.R.drawable.ic_media_play);
            } else if (state != SDKError.Statue_ConnectingSucess) {

            }
            txtState.setText(GetDescription(PlayM3u8Activity.this, state));


        }
    };


    public String GetDescription(Context con, int state) {
        Log.i("GetDescription", "GetDescription:" + state);
        String des = con.getString(R.string.connect_fail);
        switch (state) {
            case SDKError.Statue_Ready:
                des = con.getString(R.string.ready);
                break;
            case SDKError.Statue_PLAYING:
                des = con.getString(R.string.playing);
                break;
            case SDKError.Statue_Pause:
                des = con.getString(R.string.pause);
                break;
            case SDKError.Statue_STOP:
                des = con.getString(R.string.stop);
                break;
            case SDKError.Exception_ERROR:
            case SDKError.Statue_ConnectFail:
            case SDKError.MERR_ALLOC:
            case SDKError.MERR_ENCODE:
            case SDKError.MERR_DECODE:
            case SDKError.MERR_OPEN:
            case SDKError.MERR_PARAM:
            case SDKError.MERR_READ:
            case SDKError.MERR_SEEK:
            case SDKError.MERR_SWR:
            case SDKError.MERR_SIZE:
                des = con.getString(R.string.connect_fail);
                break;
            case SDKError.Statue_ConnectingSucess:
                des = con.getString(R.string.buffering);
                break;
        }
        des = des + "             " + player.GetPlayFrameRate() + "fps";
        return des;

    }


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
        txtState = findViewById(R.id.txt_state);


        player = new PlayerCore(this, PlayerCore.HLSSERVER);
//        player.SetOpenFFmpegLog(true);
        // player.InitParam("https://video.kssznuu.cn/20200807/TiqZwGQt/index.m3u8", imgLive);

        String url = "https://umsz.oss-cn-shenzhen.aliyuncs.com/m3u8/2023/5/18/1684406282207.m3u8?Expires=1684492682&OSSAccessKeyId=LTAI5tQw1hJbTrZXsA7s85HH&Signature=hJnf2Fe%2BKKJg%2FN7S%2BxwDTxo3wUQ%3D";

        //String url = "https://camplat.suning.com/camplat-web/app/play.do?playCode=ffe4e50f96162cbe9350801bd68594794f6787d7&startTime=1638201600000&endTime=1638205201621&eventType=6";
        //String url = "https://sod.bunediy.com/20211112/L57EDNF2/index.m3u8";
        player.InitParam(url, imgLive);
        player.SetOpenLog(true);
        player.OpenAudio();
        player.Play();

        tv_time.setText(generateTime(0) + "/" + generateTime(0));

        new ProgressThread().start();
        //  HlsDecode.openFFmpegLog(1);
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.GetPlayerState() == SDKError.Statue_Pause) {
                    player.Resume();

                } else if (player.GetPlayerState() == SDKError.Statue_STOP) {
                    player.Play();
                } else if (player.GetPlayerState() == SDKError.Statue_PLAYING || player.GetPlayerState() == SDKError.Statue_ConnectingSucess) {
                    player.Pause();

                } else if (player.GetPlayerState() != SDKError.Statue_ConnectingSucess) {
                    player.StopAsync();
                    player.Play();
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (duration > 0) {
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
                player.SeekFilePos(progress / 1000, 0);
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

                if (player.GetIsVoicePause())
                    player.OpenAudio();
                else
                    player.CloseAudio();


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
//                String cmds[]=new String[]{"ffmpeg","-i","https://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8","-c","copy","/sdcard/test.m3u8"};
//                Streamtomp4.ffmpegExecute(cmds.length,cmds);
                // http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8 -c:a copy -c:v copy
            }
        });


    }

    int ffmpegLog = 0;

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
