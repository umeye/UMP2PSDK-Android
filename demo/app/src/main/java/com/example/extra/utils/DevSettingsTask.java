package com.example.extra.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.Player.Core.CoustomFun.Entity.CFResponse;
import com.Player.Core.PlayerClient;
import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;

public class DevSettingsTask extends AsyncTask<Void, Void, CFResponse> {

    private String connParams;
    private String reqJson;
    private TaskCallback callback;
    private Activity activity;
    private ProgressDialog progressDialog;
    private int functionId;
    private PlayerClient playerClient;

    public DevSettingsTask(Activity activity, PlayerClient playerClient, String connParams, int functionId, String reqJson, TaskCallback callback) {
        this.playerClient = playerClient;
        this.functionId = functionId;
        this.activity = activity;
        this.connParams = connParams;
        this.reqJson = reqJson;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        if(activity != null && !activity.isFinishing()) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.show();
        }
        if (callback != null) {
            callback.onPreExecute();
        }
    }

    @Override
    protected CFResponse doInBackground(Void... params) {
        try {
            return playerClient.CallCustomFuncExExEx(connParams, functionId, reqJson.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(CFResponse cfResponse) {
        if(activity != null && !activity.isFinishing()) {
            progressDialog.dismiss();
        }
        if (callback != null) {
            if (!TextUtils.isEmpty(cfResponse.responseJson)) {
                try {
                    callback.onPostExecuteSuccess(JSON.parseObject(cfResponse.responseJson, callback.clazz));
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onPostExecuteFail();
                }
            } else {
                callback.onPostExecuteFail();
            }
        }
    }

}