package com.example.umeyesdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.Player.Core.PlayerCore;
import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseGetInfomation;
import com.Player.web.websocket.ClientCore;
import com.example.extra.AcSendProtocol;
import com.example.umeyesdk.AcVideoPlayback;
import com.example.umeyesdk.PlayActivity;
import com.example.umeyesdk.PlayActivity2;
import com.example.umeyesdk.R;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.entity.PlayNode;
import com.getui.demo.AlarmUtils;

import java.io.File;

/**
 * 点击camera功能
 *
 * @author Administrator
 */
public class CameraFuncDialog extends AlertDialog.Builder  {
    ClientCore clientCore;
    Activity activity;
    PlayNode node;
    Handler handler;
    public String[] funcArray = {"去播放", "布防", "撤防", "查询布防", "发送自定义数据", "查看录像"};

    public CameraFuncDialog(Activity arg0, ClientCore clientCore, PlayNode node, Handler handler) {
        super(arg0);
        this.activity = arg0;
        this.clientCore = clientCore;
        this.node = node;
        this.handler = handler;
        setItems();
    }

    public void setItems() {
        setItems(funcArray, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        // 如果是通道节点，就进入播放页面
                        Intent intent = new Intent(activity, PlayActivity2.class);
                        intent.putExtra("id", node.connecParams);
                        activity.startActivity(intent);
                        break;
                    case 1:
                        WebSdkApi.setDeviceAlarm(clientCore, node, 1, AlarmUtils.GETUI_CID, new int[]{1,2,3,4,5,6});
                        break;
                    case 2:
                        WebSdkApi.setDeviceAlarm(clientCore, node, 2, AlarmUtils.GETUI_CID, new int[]{1,2,3,4,5,6});
                        break;
                    case 3:
                        WebSdkApi.getDeviceAlarm(clientCore,node.node.sDevId,AlarmUtils.GETUI_CID);
                        break;
                    case 4:
                        intent = new Intent(activity, AcSendProtocol.class);
                        intent.putExtra("id", node.connecParams);
                        activity.startActivity(intent);
                        break;
                    case 5:

                        String videoDir = "/sdcard/" + getContext().getPackageName() + "/" + node.umid + "_" + node.dev_ch_no;//录像存放目录路径
//                        String videoDir = PlayerCore.getDefaultVideoPath();
                        File directory = new File(videoDir);
                        File[] files = directory.listFiles();
                        if(files == null || files.length <= 0) {
                            Toast.makeText(getContext(), R.string.no_records, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        File updatedFile = null;
                        long updatedTime = 0;
                        for (File file: files) {
                            if(updatedTime <= file.lastModified()) {
                                updatedTime = file.lastModified();
                                updatedFile = file;
                            }
                        }
                        intent = new Intent(activity, AcVideoPlayback.class);
                        intent.putExtra("fileName", updatedFile.getAbsolutePath());//默认用最新录像
                        activity.startActivity(intent);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
    }
}

