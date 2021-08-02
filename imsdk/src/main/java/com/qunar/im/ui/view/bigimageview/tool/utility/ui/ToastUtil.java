package com.qunar.im.ui.view.bigimageview.tool.utility.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author SherlockHolmes
 */
public class ToastUtil {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private Toast toast;
    private String oldMsg;
    private long twoTime;
    private long oneTime;

    public ToastUtil() {

    }

    public static ToastUtil getInstance() {
        return InnerClass.instance;
    }

    public void _short(final Context context, final String text) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
//                Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void _long(final Context context, final String text) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if(toast==null){
                    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                    oneTime = System.currentTimeMillis();
                }else{
                    twoTime = System.currentTimeMillis();
                    if(text.equals(oldMsg)){
                        if(twoTime - oneTime>Toast.LENGTH_SHORT){
                            toast.show();
                        }
                    }else{
                        oldMsg = text;
                        toast.setText(text);
                        toast.show();
                    }
                }
                oneTime = twoTime;
//                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.CENTER, 0, 0);
//                toast.show();
//                Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static class InnerClass {
        private static ToastUtil instance = new ToastUtil();
    }
}