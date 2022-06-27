package com.kotlin.tbsreader.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.kotlin.tbsreader.R;
import com.kotlin.tbsreader.TbsReader;
import com.kotlin.tbsreader.callback.ReadFileCallback;
import com.kotlin.tbsreader.share.Share2;
import com.kotlin.tbsreader.share.ShareContentType;
import com.kotlin.tbsreader.share.ShareFileUtil;
import com.kotlin.tbsreader.utils.ExceptionHandler;
import com.kotlin.tbsreader.utils.FileOpen;
import com.kotlin.tbsreader.utils.LoadFileModel;
import com.kotlin.tbsreader.utils.LoadX5Progress;
import com.kotlin.tbsreader.utils.Md5Tool;
import com.kotlin.tbsreader.utils.OnGetFilePathListener;
import com.kotlin.tbsreader.utils.SuperFileView;
import com.kotlin.tbsreader.utils.TbsFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class FileDisplayActivity extends AppCompatActivity {

    private String TAG = "TbsReader---FileDisplayActivity";
    SuperFileView mSuperFileView;
    TextView textTip;
    TextView otherOpen;

    String filePath;
    String tbsFileName;
    private Toolbar toolbar;

    private int reBuildFile = 0;
    private String resetStr = "正在重新准备...";;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(FileDisplayActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 10210);

        setContentView(R.layout.activity_file_display);

        init();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.tbs_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.inflateMenu(R.menu.file_display_menu);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_forward_vx) {
                        shareFile("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                    } else if (itemId == R.id.action_forward_qq) {
                        shareFile("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                    }  else if (itemId == R.id.action_dowonload) {
                        showLocation(getFilePath());
                    } else if (itemId == R.id.action_forward_other) {
                        FileOpen.openOrShareFile(FileDisplayActivity.this, new File(getFilePath()));
                    }
                    return false;
                }
            });
        }
    }

    private void shareFile(String packageName, String clazzName) {
        new Share2.Builder(FileDisplayActivity.this)
                .setContentType(ShareContentType.FILE)
                .setShareFileUri(
                        ShareFileUtil.getFileUri(
                                FileDisplayActivity.this,
                                ShareContentType.FILE,
                                new File(getFilePath()))
                )
                .setTitle("分享")
                .forcedUseSystemChooser(true)
                .setShareToComponent(packageName, clazzName)
                .setOnActivityResult(Share2.REQUEST_SHARE_FILE_CODE)
                .build()
                .shareBySystem();
    }

    public void showLocation(String location) {
        new AlertDialog.Builder(this)
                .setTitle("下载成功")
                .setMessage("保存路径:" + location)
                .create()
                .show();
    }

    public void init() {
        mSuperFileView = (SuperFileView) findViewById(R.id.mSuperFileView);
        textTip = (TextView) findViewById(R.id.text_tip);
        mSuperFileView.setVisibility(View.VISIBLE);
        otherOpen = (TextView) findViewById(R.id.other_open);
        otherOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.showOverflowMenu();
            }
        });
        mSuperFileView.setOnGetFilePathListener(new OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView mSuperFileView2) {
                getFilePathAndShowFile(mSuperFileView2);
            }
        });

        mSuperFileView.setReadFileCallback(new ReadFileCallback() {
            @Override
            public void readFileCallback(boolean b) {
                if (!b) {
                    textTip.setText("该文件暂不支持在线预览");
                    mSuperFileView.setVisibility(View.GONE);
                    textTip.setVisibility(View.VISIBLE);
                    otherOpen.setVisibility(View.VISIBLE);
                } else {
                    otherOpen.setVisibility(View.GONE);
                    textTip.setVisibility(View.GONE);
                    mSuperFileView.setVisibility(View.VISIBLE);
                }
            }
        });

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
            tbsFileName = uri.getQueryParameter("tbsFileName");
            if (tbsFileName.equals("市国资委")) {
                tbsFileName = "市国资委.doc";
            }
        }
        Log.i(TAG, "文件path: " + path);
        if (!TextUtils.isEmpty(path)) {
            Log.d(TAG, "文件path:" + path);
            setFilePath(path);
        }

        if (TbsReader.getInstance().isX5Init()) {
            mSuperFileView.show();
        }

        TbsReader.getInstance().setLoadX5Progress(new LoadX5Progress() {
            @Override
            public void progressRote(int progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textTip.setText("正在初始化插件：" + progress + "%");
                        if (progress == 100) mSuperFileView.show();
                        Log.e(TAG, "progressRote: " + progress);
                        if (textTip.getVisibility() != View.VISIBLE) {
                            textTip.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }


    private void getFilePathAndShowFile(SuperFileView mSuperFileView2) {
        if (getFilePath().contains("http")) {//网络地址要先下载
            downLoadFromNet(getFilePath(), mSuperFileView2);
        } else {
            mSuperFileView2.displayFile(new File(getFilePath()));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "FileDisplayActivity-->onDestroy");
        if (mSuperFileView != null) {
            mSuperFileView.onStopDisplay();
        }
    }


    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FileDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    private void downLoadFromNet(final String url, final SuperFileView mSuperFileView2) {

        //1.网络下载、存储路径、
        File cacheFile = getCacheFile(url);
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                Log.d(TAG, "删除空文件！！");
                cacheFile.delete();
                return;
            }
        }
        LoadFileModel.getInstance().callUrl(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "文件下载失败");
                File file = getCacheFile(url);
                if (!file.exists()) {
                    Log.d(TAG, "删除下载失败文件");
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
                    boolean mkdir = false;
                    if (!fileN.exists()) {
                        mkdir = fileN.createNewFile();
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
                    Log.d(TAG, "文件下载成功,准备展示文件。");

                    //2.ACache记录文件的有效期
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textTip.setVisibility(View.GONE);
                            setFilePath(fileN.getAbsolutePath());
                            mSuperFileView2.displayFile(fileN);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (textTip.getVisibility() != View.VISIBLE) {
                        textTip.setVisibility(View.VISIBLE);
                    }

                    reBuildFile++;
                    if (reBuildFile < 2) {
                        resetStr = "正在重新准备...";
                        mSuperFileView2.show();
                    }
                    textTip.setText("文件预览异常，" + resetStr + "\n请稍等或重新进入");
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
//        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/");

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


}
