package com.kotlin.tbsreader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.FileUtils;
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

    public boolean canLoadX5(Context context) {
        boolean canLoadX5 = QbSdk.canLoadX5(context);
        return canLoadX5;
    }


    public void qbsInit(Context context) {
        X5Init = QbSdk.canLoadX5(context);
        if (!X5Init || QbSdk.getTbsVersion(context) < 46007) {
            TbsFileUtils.copyAssets(context, "046007_x5.tbs.apk", TbsFileUtils.tbsFilePath() + "/046007_x5.tbs.apk");
        }

        HashMap<String, Object> map = new HashMap<>(2);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        boolean canLoadX5 = QbSdk.canLoadX5(context);
        Log.e(TAG, "canLoadX5: " + canLoadX5 + "|TbsVersion:" + QbSdk.getTbsVersion(context));
        if (canLoadX5) {
            return;
        }
        QbSdk.reset(context);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {

            }

            @Override
            public void onInstallFinish(int i) {
                Log.e(TAG, "onInstallFinish: " + i);
                int tbsVersion = QbSdk.getTbsVersion(context);
                Log.e(TAG, "tbsVersion: " + tbsVersion);
            }

            @Override
            public void onDownloadProgress(int i) {

            }
        });
        QbSdk.reset(context);
        QbSdk.installLocalTbsCore(context, 46007,  TbsFileUtils.tbsFilePath() + "/046007_x5.tbs.apk");

        int a = QbSdk.getTbsVersion(context);
        Log.e(TAG, "getTbsVersion111111: " + a);
    }

    private boolean X5Init;
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

        // ?????????????????????
        QbSdk.setTbsListener(new TbsListener() {
            /**
             * @param stateCode 110: ?????????????????????????????????????????????????????????200????????????
             */
            @Override
            public void onDownloadFinish(int stateCode) {
                Log.i(TAG, "Core onDownloadFinish: " + stateCode);
            }

            /**
             * @param i 200???232????????????
             */
            @Override
            public void onInstallFinish(int i) {
                Log.i(TAG, "Core onInstallFinish: " + i);
            }

            /**
             * ???????????????????????????????????????????????????????????????????????????????????????
             * @param progress 0 - 100
             */
            @Override
            public void onDownloadProgress(int progress) {
                Log.i(TAG, "Core Downloading: " + progress);
            }
        });

        /* ???????????????X5???????????????????????????????????????????????????????????????x5??????????????????????????????????????? */
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // ????????????????????????????????????????????????????????????????????????
            }

            /**
             * ??????????????????
             * ??????X5?????????????????????????????????wifi??????????????????????????????????????????????????????????????????false???????????????????????????????????????
             * ???????????????????????????24????????????????????????????????????????????????24?????????????????????
             * @param isX5 ????????????X5??????
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

    public void instalCore(Context context) {
        initX5Env(context);
    }

    public void instalCore(Context context, X5PreInitCallback x5PreInitCallback) {
        initX5Env(context, x5PreInitCallback);
    }

    public void initX5Env(Context context, X5PreInitCallback x5PreInitCallback) {
        // ?????????TBS??????????????????WebView????????????????????????
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
        // ?????????TBS??????????????????WebView????????????????????????
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
