package com.qunar.im.ui.custom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.gyf.immersionbar.ImmersionBar;
import com.qunar.im.base.module.Nick;
import com.qunar.im.ui.R;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class UserCardActivity extends SwipeBackActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_card);

        ImmersionBar.with(this).transparentStatusBar().transparentNavigationBar().transparentBar().init();
        Nick nick;
        Intent intent = getIntent();
        if (intent.hasExtra("nick")) {
            nick = (Nick) intent.getSerializableExtra("nick");
            String avatar = nick.getHeaderSrc();
            ImageView hBack = (ImageView) findViewById(R.id.h_back);
            Glide.with(this).load(avatar)
                    .bitmapTransform(new BlurTransformation(this, 55), new CenterCrop(this))
                    .into(hBack);


            ImageView userIcon = (ImageView) findViewById(R.id.user_icon);
            Glide.with(this)
                    .load(avatar)
                    .into(userIcon);

            TextView userName = (TextView) findViewById(R.id.user_name);
            userName.setText(nick.getName());
        }
    }
}
