package com.example.extra;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.Player.Core.PlayerClient;
import com.Player.Source.OnThumbDataListener;
import com.Player.Source.SDKError;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.extra.utils.DevSettingsTask;
import com.example.extra.utils.RequestSetXXX;
import com.example.extra.utils.ResponseGetXXX;
import com.example.extra.utils.TaskCallback;
import com.example.umeyesdk.PlayActivity2;
import com.example.umeyesdk.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//发送数据到设备端demo写法
public class AcSendProtocol extends Activity {

    private String connParams;
    private long thumbid, pushThumbid;
    private Bitmap curBitmap;
    private Handler handler = new Handler();
    private PlayerClient playerClient;
    private ImageView iv_img;
    private Button btn_request;
    private ExecutorService service = Executors.newFixedThreadPool(10);
    private int iPic = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_send_protocol);
        iv_img = findViewById(R.id.iv_img);
        btn_request = findViewById(R.id.btn_request);

        //例子以缩略图获取为例，如果不需要则不用设置监听setOnThumbDataListener
        playerClient = new PlayerClient();
        playerClient.setOnThumbDataListener(new OnThumbDataListener() {
            @Override
            public void callBackDataEx(long PCamera, int messageId, final long id, byte[] pOutData,
                                       int length) {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pOutData, 0, length);
                Log.e("AcSendProtocol", PCamera + "__" + messageId + "_" + id + "_" + length + "_" + bitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(thumbid == id) {
                            if(bitmap != null) {
                                iv_img.setImageBitmap(bitmap);
                                Toast.makeText(AcSendProtocol.this, (++iPic) +"张图片",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            pushThumbid = id;
                            if(bitmap != null) {
                                curBitmap = bitmap;
                            }
                        }
                    }
                });

            }
        });
        connParams = getIntent().getStringExtra("id");
        final int functionId = 0;//功能id

        final String reqJson = "";
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevSettingsTask devSettingsTask = new DevSettingsTask(AcSendProtocol.this, playerClient, connParams, functionId, reqJson, new TaskCallback<ResponseGetXXX>() {

                    @Override
                    public void onPostExecuteSuccess(ResponseGetXXX responseGetXXX) {
                        if (isFinishing()) {
                            return;
                        }
                        try {
                            thumbid = Long.parseLong(responseGetXXX.ThumbId);//以缩略图获取为例
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        Log.e("AcSendProtocol", "ThumbId" + responseGetXXX.ThumbId);
                        if(pushThumbid == thumbid) {
                            if(curBitmap != null) {
                                iv_img.setImageBitmap(curBitmap);
                                Toast.makeText(AcSendProtocol.this, (++iPic) +"张图片",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onPostExecuteFail() {

                    }
                });
                devSettingsTask.executeOnExecutor(service);
            }
        });



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerClient.CameraDisconnectAsyncAndRelease();
    }


}
