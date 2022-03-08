package com.kotlin.starim;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //此方法非常耗时，应当开个线程
                QbSdk.preinstallStaticTbs(getApplicationContext());
            }
        }).start();
    }
}
