package com.kotlin.star;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kotlin.tbsreader.TbsReader;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {
                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        Log.e(App.TAG, "onDenied: " + permissions.toString());
                    }

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        Log.e(App.TAG, "onGranted: " + permissions.toString());
                    }
                });

        findViewById(R.id.button).setOnClickListener(v -> {
            int tbsVersion = SpUtils.getInt(SpUtils.TBS_VERSION, 0);
            Log.d(App.TAG,"TbsReader-app--sp--tbsVersion--"  + tbsVersion);
            TbsReader.getInstance().instalCore(App.getWitApplication());
        });
    }
}