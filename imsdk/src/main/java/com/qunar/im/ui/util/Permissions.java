package com.qunar.im.ui.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Permissions {

    private final int mRequestCode = 100;//权限请求码
    public static boolean showSystemSetting = true;

    private Permissions() {
    }

    private static Permissions permissionsUtils;
    private IPermissionsResult mPermissionsResult;

    public static Permissions getInstance() {
        if (permissionsUtils == null) {
            permissionsUtils = new Permissions();
        }
        return permissionsUtils;
    }

    public void checkPermissions(Activity context, String[] permissions, @NonNull IPermissionsResult permissionsResult) {
        mPermissionsResult = permissionsResult;

        if (Build.VERSION.SDK_INT < 23) {//6.0才用动态权限
            if(permissionsResult != null) permissionsResult.passPermissions();
            return;
        }

        //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
        List<String> mPermissionList = new ArrayList<>();
        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(context, permissions, mRequestCode);
        } else {
            //说明权限都已经通过
            if(permissionsResult != null) permissionsResult.passPermissions();
            return;
        }

    }

    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限

    public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean hasPermissionDismiss = false;//有权限没有通过
        String notPassPermissions = "";
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    String permissionsStr = notPassPermissionsStr(permissions[i]);
                    if (!notPassPermissions.contains(permissionsStr)) {
                        notPassPermissions += permissionsStr + ",";
                    }
                }
            }
            if (notPassPermissions.endsWith(",")) {
                notPassPermissions = notPassPermissions.substring(0, notPassPermissions.length() - 1);
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                if (showSystemSetting) {
                    showSystemPermissionsSettingDialog(context, notPassPermissions);//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                } else {
                    if(mPermissionsResult != null) mPermissionsResult.forbidPermissions();
                }
            } else {
                //全部权限通过，可以进行下一步操作。。。
                if(mPermissionsResult != null) mPermissionsResult.passPermissions();
            }
        }

    }

    private String notPassPermissionsStr(String notPassPermissions){
        String str = "";
        switch (notPassPermissions) {
            case "android.permission.CALL_PHONE":
                str = "拨打电话权限";
                break;
            case "android.permission.READ_CONTACTS":
                str = "读取联系人权限";
                break;
            case "android.permission.CAMERA":
                str = "相机权限";
                break;
            case "android.permission.READ_PHONE_STATE":
                str = "读取手机状态权限";
                break;
            case "android.permission.ACCESS_FINE_LOCATION":
            case "android.permission.ACCESS_COARSE_LOCATION":
                str = "定位权限";
                break;
            case "android.permission.WRITE_EXTERNAL_STORAGE":
            case "android.permission.READ_EXTERNAL_STORAGE":
                str = "读写手机内存权限";
                break;
        }

        return str;
    }

    /**
     * 不再提示权限时的展示对话框
     */
    AlertDialog mPermissionDialog;

    private void showSystemPermissionsSettingDialog(final Activity context, String notPassPermissions) {
        final String mPackName = context.getPackageName();
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(context)
                    .setTitle("注意：")
                    .setMessage("已禁用"+notPassPermissions+"，部分功能将无法使用，请手动去设置授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();
                            //mContext.finish();
                            if(mPermissionsResult != null) mPermissionsResult.forbidPermissions();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.cancel();
            mPermissionDialog = null;
        }

    }


    public interface IPermissionsResult {
        void passPermissions();

        void forbidPermissions();
    }

}
