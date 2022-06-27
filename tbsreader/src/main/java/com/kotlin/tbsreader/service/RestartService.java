package com.kotlin.tbsreader.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.tencent.smtt.sdk.QbSdk;


public class RestartService extends Service {
    //关闭应用后多久重新启动
    private static long stopDelayed = 300;
    private Handler handler = new Handler();
    private String packageName;

    public static void restartAPP(Context context) {
//        QbSdk.reset(context);
        //开启一个新的服务，用来重启本APP/
        Intent intent = new Intent(context, RestartService.class);
        intent.putExtra("packageName", context.getPackageName());
        context.startService(intent);
        //杀死整个进程/
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        packageName = intent.getStringExtra("packageName");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                startActivity(LaunchIntent);
                RestartService.this.stopSelf();
            }
        }, stopDelayed);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
