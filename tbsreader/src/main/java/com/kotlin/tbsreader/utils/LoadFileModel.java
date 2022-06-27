package com.kotlin.tbsreader.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Created by 12457 on 2017/8/21.
 */

public class LoadFileModel {

    private OkHttpClient mOkHttpClient;
    private static LoadFileModel mInstance;
    private String TAG = "LoadFileModel";

    /**
     * OkHttp的一些参数配置
     */
    private LoadFileModel() {
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

    }

    /**
     * 单例加静态对象
     */
    public static LoadFileModel getInstance() {
        if (mInstance == null) {
            synchronized (LoadFileModel.class) {
                if (mInstance == null) {
                    mInstance = new LoadFileModel();
                }
            }
        }
        return mInstance;
    }

    public void callUrl(final String url, Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .addHeader("Accept-Encoding", "identity")
                        .addHeader("Accept-Language", "zh-CN")
                        .url(url)
                        .build();
                mOkHttpClient.newCall(request).enqueue(callback);
            }
        }).start();
    }

}
