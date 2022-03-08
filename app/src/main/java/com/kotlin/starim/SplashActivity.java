package com.kotlin.starim;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;


public class SplashActivity extends Activity {

  private TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      View decorView = window.getDecorView();
      decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
        @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
        @Override
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
          WindowInsets defaultInsets = v.onApplyWindowInsets(insets);
          return defaultInsets.replaceSystemWindowInsets(
            defaultInsets.getSystemWindowInsetLeft(), 0,
            defaultInsets.getSystemWindowInsetRight(),
            defaultInsets.getSystemWindowInsetBottom());
        }
      });
      ViewCompat.requestApplyInsets(decorView);
      //将状态栏设成透明，如不想透明可设置其他颜色
      window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
    }

    if (!isTaskRoot()) {
      final Intent intent = getIntent();
      final String intentAction = intent.getAction();
      if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
        finish();
        return;
      }
    }
    setContentView(R.layout.activity_splash);

    timer.start();
  }

  private void start2Main() {
    startActivity(new Intent(SplashActivity.this, MainActivity.class));
    finish();
  }

  CountDownTimer timer = new CountDownTimer(500, 500) {
    @Override
    public void onTick(long millisUntilFinished) {
    }

    @Override
    public void onFinish() {
      start2Main();
    }
  };

}
