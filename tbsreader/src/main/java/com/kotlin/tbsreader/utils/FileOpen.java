package com.kotlin.tbsreader.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class FileOpen {

    /**
     * 打开文件
     *
     * @paramfile
     */

    public static void openOrShareFile(Activity activity, File file) {

        try {
            if (file == null || !file.exists()) {
                Toast.makeText(activity, "文件不存在", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.equals(TbsFileUtils.getFileType(file.getName()), ".key")) {
                Toast.makeText(activity, "此文件类型不支持打开", Toast.LENGTH_LONG).show();
                return;
            }
            String name = file.getName();//关于加强当前新冠肺炎疫情防控工作的紧急通知.pdf
            String[] strings = name.split(".");
            if (strings.length > 1) {
                Toast.makeText(activity, "此文件类型不支持打开", Toast.LENGTH_LONG).show();
                return;
            }
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //设置intent的Action属性
//            intent.setAction(Intent.ACTION_VIEW);
//            //获取文件file的MIME类型
//            String fileType = file.getName().split(".")[1];
//            String type = TbsFileUtils.getFileAction(fileType);
            //设置intent的data和Type属性。
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.setDataAndType(uri, type);
//            activity.startActivity(intent);

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            activity.startActivity(intent);
            Intent.createChooser(intent, "请选择对应的软件打开该附件！");

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TbsReader", "openOrShareFile: 外部打开文件失败----" + e.getMessage() );
        }

    }

}
