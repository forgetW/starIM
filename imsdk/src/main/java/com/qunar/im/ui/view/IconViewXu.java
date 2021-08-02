package com.qunar.im.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by boyuan on 16/5/20.
 */
public class IconViewXu extends TextView {
    public IconViewXu(Context context) {
        this(context,null);
    }

    public IconViewXu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IconViewXu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IconViewXu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "iconfont.ttf");
        setTypeface(tf);

    }
}