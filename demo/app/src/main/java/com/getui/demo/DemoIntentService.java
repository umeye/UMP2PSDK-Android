package com.getui.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.Player.web.websocket.ClientCore;
import com.alibaba.fastjson.JSONObject;
import com.example.umeyesdk.AcLogo;
import com.example.umeyesdk.R;
import com.example.umeyesdk.api.WebSdkApi;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.Utility;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.igexin.sdk.message.UnBindAliasCmdMessage;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class DemoIntentService extends GTIntentService {

    private static final String TAG = "GetuiSdkDemo";

    NotificationManager nm;
    public static int notifyId = 0;
    private Intent update_intent = new Intent(
            "com.qq.xgdemo.activity.UPDATE_LISTVIEW");
    public static String notificationChannal = "ch"+Constants.custom_flag;
    private static String notificationChannalName= "chName"+Constants.custom_flag;

    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);

            AlarmMessage alarmMessage = JSONObject.parseObject(data,
                    AlarmMessage.class);

            if (alarmMessage != null && alarmMessage.aps != null
                    && alarmMessage.aps.alert != null
                    && !TextUtils.isEmpty(alarmMessage.aps.alert.body)) {
                context.sendBroadcast(update_intent);
                alarm(context, context.getString(R.string.app_name),
                        alarmMessage);
            }
        }

        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }



    public void alarm(final Context context, final String dev,
                      final AlarmMessage alarmMessage) {
        notifyId++;
        if (nm == null) {
            nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notifyMessage(context, alarmMessage.aps.alert.title,
                alarmMessage.aps.alert.body, alarmMessage.AlarmTime);

    }

    public void notifyMessage(Context context, String title, String message,
                              String time) {
        Intent intent = new Intent(context, AcLogo.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if(nm.getNotificationChannel(notificationChannal) != null){
                nm.deleteNotificationChannel(notificationChannal);
            }
            Log.d("GetuiSdkDemo","Android SDK>26, Create NotificationChannel");
            NotificationChannel channel = new NotificationChannel(notificationChannal,notificationChannalName, NotificationManager.IMPORTANCE_HIGH);
            channel.setBypassDnd(true);
            channel.enableVibration(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            nm.createNotificationChannel(channel);   //创建之后需要删除
        }else{
            notificationChannal = "default";
        }


        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.notification);
        views.setImageViewResource(R.id.icon, R.drawable.ic_launcher);
        views.setTextViewText(R.id.title, title);
        views.setTextViewText(R.id.content, message);
        views.setTextViewText(R.id.time, time);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, notificationChannal).setAutoCancel(true)
                .setTicker(message)
                .setSmallIcon(R.drawable.ic_launcher)//状态栏图标,id为icon
                .setContent(views);

        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        mBuilder.setSound(Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.alarm));
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            try {
                Uri notification = Uri.parse("android.resource://"
                        + context.getPackageName() + "/" + R.raw.alarm);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        Notification notification = mBuilder.build();

        nm.notify(notifyId, notification);
    }



    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        AlarmUtils.GETUI_CID = clientid;
        ClientCore clientCore = ClientCore.getInstance();
        if(!TextUtils.isEmpty(AlarmUtils.GETUI_CID) && clientCore.IsLoginEx()) {
            WebSdkApi.setUserPush(clientCore, 1,
                    Utility.isZh(context) ? 2 : 1,
                    AlarmUtils.GETUI_CID, 1, 0,0);
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if(action == PushConsts.BIND_ALIAS_RESULT) {
            bindAliasResult((BindAliasCmdMessage) cmdMessage);
        } else if (action == PushConsts.UNBIND_ALIAS_RESULT) {
            unbindAliasResult((UnBindAliasCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                        + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                        + message.getTitle() + "\ncontent = " + message.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        int text = R.string.add_tag_unknown_exception;
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = R.string.add_tag_success;
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = R.string.add_tag_error_count;
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = R.string.add_tag_error_frequency;
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = R.string.add_tag_error_repeat;
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = R.string.add_tag_error_unbind;
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = R.string.add_tag_unknown_exception;
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = R.string.add_tag_error_null;
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = R.string.add_tag_error_not_online;
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = R.string.add_tag_error_black_list;
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = R.string.add_tag_error_exceed;
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));
    }

    private void bindAliasResult(BindAliasCmdMessage bindAliasCmdMessage) {
        String sn = bindAliasCmdMessage.getSn();
        String code = bindAliasCmdMessage.getCode();

        int text = R.string.bind_alias_unknown_exception;
        switch (Integer.valueOf(code)) {
            case PushConsts.BIND_ALIAS_SUCCESS:
                text = R.string.bind_alias_success;
                break;
            case PushConsts.ALIAS_ERROR_FREQUENCY:
                text = R.string.bind_alias_error_frequency;
                break;
            case PushConsts.ALIAS_OPERATE_PARAM_ERROR:
                text = R.string.bind_alias_error_param_error;
                break;
            case PushConsts.ALIAS_REQUEST_FILTER:
                text = R.string.bind_alias_error_request_filter;
                break;
            case PushConsts.ALIAS_OPERATE_ALIAS_FAILED:
                text = R.string.bind_alias_unknown_exception;
                break;
            case PushConsts.ALIAS_CID_LOST:
                text = R.string.bind_alias_error_cid_lost;
                break;
            case PushConsts.ALIAS_CONNECT_LOST:
                text = R.string.bind_alias_error_connect_lost;
                break;
            case PushConsts.ALIAS_INVALID:
                text = R.string.bind_alias_error_alias_invalid;
                break;
            case PushConsts.ALIAS_SN_INVALID:
                text = R.string.bind_alias_error_sn_invalid;
                break;
            default:
                break;

        }

        Log.d(TAG, "bindAlias result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));

    }

    private void unbindAliasResult(UnBindAliasCmdMessage unBindAliasCmdMessage) {
        String sn = unBindAliasCmdMessage.getSn();
        String code = unBindAliasCmdMessage.getCode();

        int text = R.string.unbind_alias_unknown_exception;
        switch (Integer.valueOf(code)) {
            case PushConsts.UNBIND_ALIAS_SUCCESS:
                text = R.string.unbind_alias_success;
                break;
            case PushConsts.ALIAS_ERROR_FREQUENCY:
                text = R.string.unbind_alias_error_frequency;
                break;
            case PushConsts.ALIAS_OPERATE_PARAM_ERROR:
                text = R.string.unbind_alias_error_param_error;
                break;
            case PushConsts.ALIAS_REQUEST_FILTER:
                text = R.string.unbind_alias_error_request_filter;
                break;
            case PushConsts.ALIAS_OPERATE_ALIAS_FAILED:
                text = R.string.unbind_alias_unknown_exception;
                break;
            case PushConsts.ALIAS_CID_LOST:
                text = R.string.unbind_alias_error_cid_lost;
                break;
            case PushConsts.ALIAS_CONNECT_LOST:
                text = R.string.unbind_alias_error_connect_lost;
                break;
            case PushConsts.ALIAS_INVALID:
                text = R.string.unbind_alias_error_alias_invalid;
                break;
            case PushConsts.ALIAS_SN_INVALID:
                text = R.string.unbind_alias_error_sn_invalid;
                break;
            default:
                break;

        }

        Log.d(TAG, "unbindAlias result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));

    }


    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

}
