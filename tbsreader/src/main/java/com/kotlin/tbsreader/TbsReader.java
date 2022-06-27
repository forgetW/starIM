package com.kotlin.tbsreader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.kotlin.tbsreader.callback.X5PreInitCallback;
import com.kotlin.tbsreader.utils.ExceptionHandler;
import com.kotlin.tbsreader.utils.LoadX5Progress;
import com.kotlin.tbsreader.utils.TbsFileUtils;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import java.util.HashMap;

public class TbsReader {

    public final static String TAG = "TbsReader";
    private static TbsReader tbsReader;

    public boolean isNotDealUrl() {
        return notDealUrl;
    }

    public boolean notDealUrl = false;

    public String getDealUrl() {
        return dealUrl != null ? dealUrl : "";
    }

    public String dealUrl;

    public int getTbsVersion(Context context) {
        return QbSdk.getTbsVersion(context);
    }

    private int tbsVersion;

    public boolean isX5Init() {
        return X5Init;
    }

    private boolean X5Init = false;
    private boolean loading = false;

    public void setLoadX5Progress(LoadX5Progress loadX5Progress) {
        this.loadX5Progress = loadX5Progress;
    }

    public static TbsReader getInstance() {
        if (tbsReader == null) {
            tbsReader = new TbsReader();
        }
        return tbsReader;
    }

    private LoadX5Progress loadX5Progress;

    public void initX5(Context context) {

        initX5Env(context);

        // 监听内核的下载
        QbSdk.setTbsListener(new TbsListener() {
            /**
             * @param stateCode 110: 表示当前服务器认为该环境下不需要下载；200下载成功
             */
            @Override
            public void onDownloadFinish(int stateCode) {
                Log.i(TAG, "Core onDownloadFinish: " + stateCode);
            }
            /**
             * @param i 200、232安装成功
             */
            @Override
            public void onInstallFinish(int i) {
                Log.i(TAG, "Core onInstallFinish: " + i);
            }
            /**
             * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
             * @param progress 0 - 100
             */
            @Override
            public void onDownloadProgress(int progress) {
                Log.i(TAG, "Core Downloading: " + progress);
            }
        });

        /* 此过程包括X5内核的下载、预初始化，接入方不需要接管处理x5的初始化流程，希望无感接入 */
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖wifi网络下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * 内核下发请求发起有24小时间隔，卸载重装、调整系统时间24小时后都可重置
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {

                Log.i(TAG, "Core onViewInitFinished: " + isX5);
            }
        });
    }

    public boolean localInit(Context context, String assetsPath) {
        if (TextUtils.isEmpty(assetsPath)) {
            assetsPath = "tbscore";
        }

        Log.d(TAG, "App----localInit------");
        boolean copy = TbsFileUtils.copyFileFromAssets(context, assetsPath, TbsFileUtils.createDocumentsFile(context).getPath());
        return copy;
    }

    public void instalCore(Context context){
        initX5Env(context);
    }

    public void instalCore(Context context, X5PreInitCallback x5PreInitCallback){
        initX5Env(context, x5PreInitCallback);
    }

    public void initX5Env(Context context, X5PreInitCallback x5PreInitCallback) {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "onCoreInitFinished" + "adding listener");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                tbsVersion = QbSdk.getTbsVersion(context);
                Log.e(TAG, "onViewInitFinished--" + tbsVersion + "--adding listener--" + b);
                X5Init = b;
                if (x5PreInitCallback != null) {
                    x5PreInitCallback.x5InitCallback(b, tbsVersion);
                }
            }
        });
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            ExceptionHandler.getInstance().initConfig(context);
        }
    }

    public void initX5Env(Context context) {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.e(TAG, "onCoreInitFinished" + "adding listener");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                tbsVersion = QbSdk.getTbsVersion(context);
                Log.e(TAG, "onViewInitFinished--" + tbsVersion + "--adding listener--" + b);
                X5Init = b;
            }
        });
        if (ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            ExceptionHandler.getInstance().initConfig(context);
        }
    }
}
