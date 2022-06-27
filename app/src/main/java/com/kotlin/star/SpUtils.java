package com.kotlin.star;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

    private static String FILLNAME = "FlutterSharedPreferences";// 文件名称
    private static SharedPreferences mSharedPreferences = null;

    public final static String SPLASH_IMG = "splashImg";
    public final static String TBS_VERSION = "TbsVersion";
    public final static String APP_EXIT = "flutter.appExit";
    public final static String ZHYFAAR_ISFIRSTFLAG = "flutter.zhyfaar_isFirstFlag";


    /**
     * 单例模式
     */
    public static synchronized SharedPreferences getInstance() {
        if (mSharedPreferences == null) {
            mSharedPreferences = App.getWitApplication().getSharedPreferences(FILLNAME, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    /**
     * SharedPreferences常用的10个操作方法
     */
    public static void putBoolean(String key, boolean value) {
        SpUtils.getInstance().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return SpUtils.getInstance().getBoolean(key, defValue);
    }

    public static void putString(String key, String value) {
        SpUtils.getInstance().edit().putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return SpUtils.getInstance().getString(key, defValue);
    }

    public static void putInt(String key, int value) {

        SpUtils.getInstance().edit().putInt(key, value).apply();
    }

    public static int getInt(String key, int defValue) {
        return SpUtils.getInstance().getInt(key, defValue);
    }

    /**
     * 移除某个key值已经对应的值
     */
    public static void remove(String key) {
        SpUtils.getInstance().edit().remove(key).apply();
    }

    /**
     * 清除所有内容
     */
    public static void clear() {
        SpUtils.getInstance().edit().clear().apply();
    }
}
