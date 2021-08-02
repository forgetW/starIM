package com.qunar.im.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qunar.im.utils.HttpUtil;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.jsonbean.NoticeRequestBean;
import com.qunar.im.base.protocol.ProtocolCallback;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.Utils;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.ui.R;
import com.qunar.im.ui.activity.PbChatActivity;

import java.util.List;

/**
 * 提醒通知
 * Created by lihaibin.li on 2018/2/8.
 */

public class NoticeView extends LinearLayout {
    private TextView textView;//展示的文本
    private ImageView closeBtn;//关闭按钮

    private NoticeBean noticeBean;
    private RequestCallBack requestCallBack;
    private OnClickListener closeListener;

    public NoticeView(Context context) {
        super(context);
        init(context);
    }

    public NoticeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoticeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER_VERTICAL);

        int padding = Utils.dipToPixels(context, 8);
        setPadding(padding, padding, padding, padding);

        textView = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        textView.setLayoutParams(layoutParams);
//        textView.setText("test");

        addView(textView);

        closeBtn = new ImageView(context);
        closeBtn.setImageResource(R.drawable.atom_ui_close);
        addView(closeBtn);

        setBackgroundColor(Color.parseColor("#f8fbdf"));
    }

    public void setData(NoticeBean noticeBean) {
        if (noticeBean == null) return;
        this.noticeBean = noticeBean;
        List<NoticeBean.NoticeStrBean> strBeans = noticeBean.getNoticeStr();
        if (strBeans != null && !strBeans.isEmpty()) {
            textView.setText("");
            for (NoticeBean.NoticeStrBean bean : strBeans) {
                if (bean == null) continue;
                String str = bean.getStr();
                if (TextUtils.isEmpty(str)) continue;
                SpannableString spanString = new SpannableString(str);
                ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor(bean.getStrColor()));
                int start = 0;
                int end = str.length();
                spanString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if (!"text".equals(bean.getType()))
                    spanString.setSpan(new MyClickableSpan(bean), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.append(spanString);
            }
            textView.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        }
    }

    /**
     * 设置发送request监听
     *
     * @param requestCallBack
     */
    public void setRequestCallBack(RequestCallBack requestCallBack) {
        this.requestCallBack = requestCallBack;
    }

    interface RequestCallBack {
        void callBack(String response);
    }

    public void setCloseListener(OnClickListener closeListener) {
        this.closeListener = closeListener;
        closeBtn.setOnClickListener(this.closeListener);
    }


    /**
     * 内部类，用于截获点击富文本后的事件
     */
    class MyClickableSpan extends ClickableSpan {
        private NoticeBean.NoticeStrBean bean;
        private String type;

        public MyClickableSpan(NoticeBean.NoticeStrBean bean) {
            this.bean = bean;
            type = bean.getType();
        }


        @Override
        public void onClick(View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
//            ds.setColor(ds.linkColor);
            ds.setUnderlineText("text".equals(type) ? false : true);//超链接的下划线
        }
    }
}
