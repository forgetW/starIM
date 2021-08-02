package com.qunar.im.ui.view.baseView;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qunar.im.ui.R;
import com.qunar.im.ui.sdk.QIMSdk;
import com.qunar.im.ui.util.FacebookImageUtil;
import com.qunar.im.ui.util.ProfileUtils;
import com.qunar.im.ui.view.bigimageview.view.MyGlideUrl;

/**
 * Created by zhaokai on 15-11-3.
 */
public class MapView extends RelativeLayout {
    SimpleDraweeView image;
    TextView text;
    Context context;
    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.atom_ui_layout_map, this, true);
        image = findViewById(R.id.image);
        text = findViewById(R.id.text);
        this.context = context;
    }

    public void setMapInfo(String url, String position) {
        text.setText(position);

        Glide.with(context)
                .load(new MyGlideUrl(url))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .asBitmap()
                .centerCrop()
                .thumbnail(0.1f)
                .transform(new CenterCrop(context))
                .placeholder(ProfileUtils.getDefaultRes())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)//缓存全尺寸
                .dontAnimate()
                .into(image);
//        FacebookImageUtil.loadWithCache(uri.toString(), image);
    }
}
