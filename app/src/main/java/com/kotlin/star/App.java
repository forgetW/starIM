package com.kotlin.star;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.kotlin.tbsreader.TbsReader;

import java.util.List;


public class App extends Application {

    public static final String TAG = "mmmm";

    private static App sApplication = null;

    public static App getWitApplication() {
        return sApplication;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        String processName = getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            Log.i(TAG, "processName-----" + processName);
            boolean defaultProcess = processName.equals(getPackageName());
            // 默认的主进程启动时初始化应用
            if (defaultProcess) {
                int tbsVersion = SpUtils.getInt(SpUtils.TBS_VERSION, 0);
                if (tbsVersion == 0) {
                    //TbsReader.getInstance().localInit(this, "tbscore");
                }
            }
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
