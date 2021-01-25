package com.example.umeyesdk.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.Player.Core.PlayerCore;
import com.Player.Source.TSourceFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveStreamThread extends Thread {
    String TAG = "SaveStreamThread";
    PlayerCore playercore;
    File file;

    public SaveStreamThread(PlayerCore playercore, File file) {
        this.playercore = playercore;
        this.file = file;
    }

    @SuppressLint({"SdCardPath", "SimpleDateFormat"})
    @Override
    public void run() {
        super.run();
        if (playercore == null)
            return;
        FileOutputStream fileOutputStream = null;
        if (file == null || !file.exists()) {
            String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            try {
                String videofilePath = "/sdcard/VideoFile_" + time + ".h264";
                file = new File(videofilePath);
                boolean success = file.createNewFile();
                if (!success) {
                    Log.e("TAG", videofilePath + ",Create file failed!!1");
                    return;
                }
                fileOutputStream = new FileOutputStream(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            while (playercore.ThreadisTrue) {
                //// 暂停
                if (playercore.IsPausing) {
                    Thread.sleep(20);
                    continue;
                }
                TSourceFrame mFrame = null;
                int leftvideo = playercore.GetVideoFrameLeft();
                if (leftvideo > 0) {
                    mFrame = playercore.GetNextVideoFrame();// 新加入
                    if (mFrame != null && fileOutputStream != null)
                        fileOutputStream.write(mFrame.iData);
                    else Thread.sleep(20);

                } else {
                    Thread.sleep(20);
                }
            }
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }


    }
}
