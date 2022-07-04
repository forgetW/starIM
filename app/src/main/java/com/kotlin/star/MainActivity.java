package com.kotlin.star;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kotlin.tbsreader.TbsReader;
import com.kotlin.tbsreader.activity.FilePreActivity;
import com.kotlin.tbsreader.callback.X5PreInitCallback;

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
                        TbsReader.getInstance().qbsInit(MainActivity.this);
                    }
                });

        findViewById(R.id.init).setOnClickListener(v -> {
            boolean canLoad = TbsReader.getInstance().canLoadX5(MainActivity.this);
            int version = TbsReader.getInstance().getTbsVersion(MainActivity.this);
            Log.e(App.TAG, "canLoadX5: " + canLoad);
            Log.e(App.TAG, "TbsVersion: " + version);
        });
        findViewById(R.id.preview).setOnClickListener(v -> {
            String file = "https://zhdjfile.nmgdj.gov.cn/nmg/%E5%B9%B3%E5%8F%B0%E9%97%AE%E9%A2%98%E5%8F%8D%E9%A6%88%E8%A1%A8%EF%BC%88%E7%BB%84%E7%BB%87%E5%90%8D%E7%A7%B0%EF%BC%89.xlsx";
            FilePreActivity.show(MainActivity.this, file);
        });
    }

    private void initTbs() {
        int tbsVersion = SpUtils.getInt(SpUtils.TBS_VERSION, 0);
        Log.d(App.TAG, "TbsReader-app--SpUtils--tbsVersion--" + tbsVersion);
        TbsReader.getInstance().instalCore(App.getWitApplication(), (initFinished, version) -> {
            SpUtils.putInt(SpUtils.TBS_VERSION, version);
            Log.d(App.TAG, "TbsReader-app--instalCore--tbsVersion--" + version);
        });
    }
}