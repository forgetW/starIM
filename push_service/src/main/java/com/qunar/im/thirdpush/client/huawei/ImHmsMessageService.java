package com.qunar.im.thirdpush.client.huawei;


import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.qunar.im.thirdpush.QTPushConfiguration;
import com.qunar.im.utils.HttpUtil;

public class ImHmsMessageService extends HmsMessageService {
    public ImHmsMessageService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("ImHmsMessageService", "onMessageReceived" + "adding listener");
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        Log.e("ImHmsMessageService", "onDeletedMessages" + "adding listener");
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        Log.e("ImHmsMessageService", "onMessageSent--" + s);
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        Log.e("ImHmsMessageService", "onSendError-s-" + s);
        Log.e("ImHmsMessageService", "onSendError-e-" + e);
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String token) {
        Log.e("IM_LOG", "onNewToken--" + token);
        HttpUtil.registPush(token, QTPushConfiguration.getPlatName());
        super.onNewToken(token);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("IM_LOG", "onBind" + "adding listener");
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        Log.e("ImHmsMessageService", "onStartCommand-i" + i + "--i1--"+i1);
        return super.onStartCommand(intent, i, i1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
