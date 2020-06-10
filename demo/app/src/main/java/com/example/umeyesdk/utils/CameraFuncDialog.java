package com.example.umeyesdk.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.Player.web.response.ResponseCommon;
import com.Player.web.response.ResponseGetInfomation;
import com.Player.web.websocket.ClientCore;
import com.example.extra.AcSendProtocol;
import com.example.umeyesdk.PlayActivity;
import com.example.umeyesdk.PlayActivity2;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.entity.PlayNode;
import com.getui.demo.AlarmUtils;

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
    public String[] funcArray = {"去播放", "布防", "撤防", "查询布防", "发送自定义数据"};

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
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
    }
}

