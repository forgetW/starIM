package com.kotlin.starim;

import android.app.Application;

import com.qunar.im.ui.sdk.QIMSdk;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        QIMSdk.getInstance().init(this);
    }
}
