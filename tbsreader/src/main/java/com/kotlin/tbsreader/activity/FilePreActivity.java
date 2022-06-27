package com.kotlin.tbsreader.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kotlin.tbsreader.R;
import com.kotlin.tbsreader.TbsReader;
import com.kotlin.tbsreader.utils.ExceptionHandler;
import com.kotlin.tbsreader.utils.LoadFileModel;
import com.kotlin.tbsreader.utils.Md5Tool;
import com.kotlin.tbsreader.utils.TbsFileUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FilePreActivity extends AppCompatActivity {

    private String TAG = "TbsReader---FilePreActivity";
    private String tbsFileName;
    private TextView textTip;

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FilePreActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_pre);

        Intent intent = this.getIntent();
        String path = (String) intent.getSerializableExtra("path");
        tbsFileName = (String) intent.getSerializableExtra("tbsFileName");

        Uri uri = getIntent().getData();
        if (uri != null) {
            // 完整的url信息
            String totalUrl = uri.toString();
            Log.i(TAG, "完整url: " + totalUrl);
            //获取指定参数值
            path = uri.getQueryParameter("fileUrl");
            String token = uri.getQueryParameter("logintoken");
            if (TbsReader.getInstance().isNotDealUrl()) {
                String replaceUrl = "app://zhyf/filePreview?fileUrl=";
                if (!TbsReader.getInstance().getDealUrl().equals("")) {
                    replaceUrl = TbsReader.getInstance().getDealUrl();
                }
                path = totalUrl.replace(replaceUrl, "");
            }
//            if (token != null) {
//                path = path + "&logintoken=" + token;
//            }
            tbsFileName = uri.getQueryParameter("tbsFileName");
            if (tbsFileName.equals("市国资委")) {
                tbsFileName = "市国资委.doc";
            }
        }
        Log.i(TAG, "文件path: " + path);

        if (path.contains("http")) {//网络地址要先下载
            downLoadFromNet(path);
        } else {
            openFile(path);
        }


        textTip = findViewById(R.id.text_tip);
    }

    private void downLoadFromNet(String url) {
        LoadFileModel.getInstance().callUrl(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "文件下载失败");
                File file = getCacheFile(url);
                if (!file.exists()) {
                    Log.d(TAG, "删除下载失败文件--" + file.getAbsolutePath());
                    file.delete();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "下载文件-->onResponse");
                boolean flag;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                HttpUrl realUrl = response.request().url();
                Log.d(TAG, "文件下载成功,准备展示文件。realUrl----" + realUrl);
                try {
                    ResponseBody responseBody = response.body();
                    is = responseBody.byteStream();
                    String contentType = response.header("Content-Type").split(";")[0];
                    long total = responseBody.contentLength();
                    File file1 = getCacheDir(url);
                    if (!file1.exists()) {
                        file1.mkdirs();
                        Log.d(TAG, "创建缓存目录： " + file1.toString());
                    }
                    File fileN = getCacheFile(url, contentType);//new File(getCacheDir(url), getFileName(url))
                    Log.d(TAG, "创建缓存文件： " + fileN.toString());
                    if (!fileN.exists()) {
                        fileN.createNewFile();
                    }
                    fos = new FileOutputStream(fileN);

                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (textTip.getVisibility() != View.VISIBLE) {
                                    textTip.setVisibility(View.VISIBLE);
                                }
                                textTip.setText("正在准备文件：" + progress + "%");
                            }
                        });
                        Log.d(TAG, "写入缓存文件" + fileN.getName() + "进度: " + progress);
                    }

                    fos.flush();
                    fos.close();
                    String codeString = TbsFileUtils.codeString(fileN.getAbsolutePath());
                    Log.d(TAG, "文件下载成功,准备展示文件。" + codeString);
                    //2.ACache记录文件的有效期
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            openFile(fileN.getPath());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "文件下载异常 = " + e.toString());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        Log.d(TAG, "finally--文件下载异常 = " + e.toString());
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        Log.d(TAG, "finally--文件下载异常 = " + e.toString());
                    }
                }
            }
        });
    }

    /***
     * 获取缓存目录
     *
     * @param url
     * @return
     */
    private File getCacheDir(String url) {
        return new File(ExceptionHandler.fileDownloadPath + "/Download/");
    }

    /***
     * 绝对路径获取缓存文件
     *
     *
     * @param url
     * @param contentType
     * @return
     */
    private File getCacheFile(String url, String contentType) {
        File cacheFile;
        if (tbsFileName != null && !TextUtils.isEmpty(tbsFileName) && TbsFileUtils.normalFile(tbsFileName)) {
            cacheFile = new File(ExceptionHandler.fileDownloadPath + "/Download/" + getFileName(url));
        } else
            cacheFile = new File(ExceptionHandler.fileDownloadPath + "/Download/" + getFileName(url, contentType));
        Log.d(TAG, "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 绝对路径获取缓存文件
     *
     *
     * @param url
     * @return
     */
    private File getCacheFile(String url) {
        File cacheFile = new File(ExceptionHandler.fileDownloadPath + "/Download/" + getFileName(url));
        Log.d(TAG, "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @param contentType
     * @return
     */
    private String getFileName(String url, String contentType) {
        String fileType = getFileType(url);
        if (!TbsFileUtils.normalFile(fileType)) {
            fileType = TbsFileUtils.getFileType(contentType).replace(".", "");
        }
        String fileName = Md5Tool.hashKey(url) + "." + fileType;
        return fileName;
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        String fileType, fileName;
        if (tbsFileName != null && !TextUtils.isEmpty(tbsFileName)) {
            fileName = tbsFileName;
        } else {
            fileType = getFileType(url);
            fileName = Md5Tool.hashKey(url) + "." + fileType;
        }

        return fileName;
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d(TAG, "paramString---->null");
            return str;
        }
        Log.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d(TAG, "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        Log.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }

    private void openFile(String filePath) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("style", "1");
        params.put("local", "true");
        JSONObject object = new JSONObject();
        try {
            object.put("pkgName", getApplicationContext().getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("menuData", object.toString());
        QbSdk.openFileReader(FilePreActivity.this, filePath, params, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.e("TbsReader", "onReceiveValue: " + s);
                if (s.equals("fileReaderClosed")) {
                    finish();
                    overridePendingTransition(0, 0);
                }
                if (s.equals("open success")) {
                    textTip.setVisibility(View.INVISIBLE);
                }
            }
        });
        overridePendingTransition(0, 0);
    }

}