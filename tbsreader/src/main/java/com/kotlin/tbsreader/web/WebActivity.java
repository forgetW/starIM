package com.kotlin.tbsreader.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kotlin.tbsreader.R;


public class WebActivity extends BaseWebActivity {
  private FrameLayout mFl;

  public static void start(Context context) {
    Intent starter = new Intent(context, WebActivity.class);
    context.startActivity(starter);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web);
    initView();
    setWebSettings();
    mWebSettings.setJavaScriptEnabled(true);
    // WebView加载web资源
    loadUrl("http://debugtbs.qq.com");
  }

  private void initView() {
    mFl = (FrameLayout) findViewById(R.id.webview);
  }

  @Override
  protected ViewGroup setWebViewContainer() {
    return mFl;
  }
}
