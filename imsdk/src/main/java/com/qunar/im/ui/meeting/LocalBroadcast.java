package com.qunar.im.ui.meeting;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is about local broadcast registration cancellation category.
 * 本地广播注册取消类
 */
public class LocalBroadcast {

    private static final String TAG = LocalBroadcast.class.getSimpleName();

    /**
     * Instance object of LocalBroadcast component.
     * 获取一个LocBroadcast对象
     */
    private final static LocalBroadcast INS = new LocalBroadcast();

    /**
     * Store a collection of broadcast objects and accepted classes
     * 存储广播对象和接受类的集合
     */
    protected final Map<String, LinkedList<LocalBroadcastReceiver>> broadcasts = new HashMap<>();

    /**
     * Variable lock
     * 可变锁
     */
    protected final Object broadcastLock = new Object();

    /**
     * Register the broadcast name
     * 注册的广播名称
     */
    private String broadcastName;

    /**
     * This is a constructor of LocalBroadcast class.
     * 构造方法
     */
    private LocalBroadcast() {
    }

    /**
     * This method is used to get instance object of ImMgr.
     * 获取ImMgr对象实例
     *
     * @return ImMgr Return instance object of ImMgr
     * 返回一个ImMgr对象实例
     */
    public static LocalBroadcast getInstance() {
        return INS;
    }

    public String getBroadcastName() {
        return broadcastName;
    }

    /**
     * This method is used to registered broadcast.
     * 注册广播
     *
     * @param receiver Indicates receiver
     *                 广播接收对象
     * @param actions  Indicates accept the action
     *                 注册的广播名称数组
     * @return boolean Return true：registered success；false：registered failed
     * 返回TRUE表示注册成功，false表示注册失败
     */
    public boolean registerBroadcast(LocalBroadcastReceiver receiver, String[] actions) {
        if (null == receiver || null == actions) {
            return false;
        }

        synchronized (broadcastLock) {
            LinkedList<LocalBroadcastReceiver> list;

            for (String action : actions) {
                list = broadcasts.get(action);

                if (null == list) {
                    list = new LinkedList<>();
                    broadcasts.put(action, list);
                }

                if (!list.contains(receiver)) {
                    list.add(receiver);
                }
            }
        }
        return true;
    }

    /**
     * This method is used to unregistered broadcast.
     * 去注册广播(注销广播)
     *
     * @param receiver Indicates receiver
     *                 广播接收对象
     * @param actions  Indicates accept the action
     *                 注册的广播名称数组
     * @return boolean Return true：unregistered success；false：unregistered failed
     * 返回TRUE表示注册成功，false表示注册失败
     */
    public boolean unRegisterBroadcast(LocalBroadcastReceiver receiver, String[] actions) {
        if (null == receiver || null == actions) {
            return false;
        }

        List<LocalBroadcastReceiver> list;

        synchronized (broadcastLock) {
            for (String action : actions) {
                list = broadcasts.get(action);

                if (null == list) {
                    return false;
                }
                list.remove(receiver);
            }
        }
        return true;
    }

    /**
     * This method is used to send a broadcast message.
     * 发送广播消息
     *
     * @param action Indicates registered broadcast name
     *               注册的广播名称数组
     * @param data   Indicates sent data
     *               要发送的数据
     */
    public void sendBroadcast(String action, Object data) {
        if (null == action) {
            return;
        }

        this.broadcastName = action;

        synchronized (broadcastLock) {
            List<LocalBroadcastReceiver> receivers = broadcasts.get(action);
            if (null == receivers || receivers.isEmpty()) {
                Log.i(TAG, "no receiver for action#" + action);
                return;
            }else {
                Collections.reverse(receivers);
            }

            for (LocalBroadcastReceiver receiver : receivers) {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    HANDLER.post(new OnReceiver(receiver, action, data));
                } else {
                    EXECUTOR.execute(new OnReceiver(receiver, action, data));
                }
            }
        }
    }

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(6);

    private static class OnReceiver implements Runnable {
        private final LocalBroadcastReceiver receiver;

        private final String action;
        private final Object data;

        public OnReceiver(LocalBroadcastReceiver receiver, String action, Object data) {
            this.receiver = receiver;
            this.action = action;
            this.data = data;
        }

        @Override
        public void run() {
            if (receiver == null) {
                return;
            }
            receiver.onReceive(action, data);
        }
    }

    public static void destroy() {
        if (!EXECUTOR.isShutdown()) {
            EXECUTOR.shutdownNow();
        }
    }
}
