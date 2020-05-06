package com.example.extra;

import android.app.Activity;
import android.os.Bundle;

import com.Player.Core.PlayerClient;
import com.alibaba.fastjson.JSON;
import com.example.extra.utils.DevSettingsTask;
import com.example.extra.utils.RequestSetXXX;
import com.example.extra.utils.ResponseGetXXX;
import com.example.extra.utils.TaskCallback;

//发送数据到设备端demo写法
public class AcSendProtocol extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlayerClient playerClient = new PlayerClient();
        String connParams = "";//连接参数
        int functionId = 0x0000;//功能id

        RequestSetXXX requestSetXXX = new RequestSetXXX();
        String reqJson = JSON.toJSONString(requestSetXXX);//协议json数据
        new DevSettingsTask(this, playerClient, connParams, functionId, reqJson, new TaskCallback<ResponseGetXXX>() {

            @Override
            public void onPostExecuteSuccess(ResponseGetXXX responseGetXXX) {

            }

            @Override
            public void onPostExecuteFail() {

            }
        }).execute();

    }
}
