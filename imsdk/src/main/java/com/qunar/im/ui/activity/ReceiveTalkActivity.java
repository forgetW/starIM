//package com.qunar.im.ui.activity;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.SystemClock;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.gyf.immersionbar.ImmersionBar;
//import com.qunar.im.base.common.QunarIMApp;
//import com.qunar.im.base.module.RecentConversation;
//import com.qunar.im.base.structs.MessageStatus;
//import com.qunar.im.base.util.glide.GlideCircleTransform;
//import com.qunar.im.ui.R;
//import com.qunar.im.ui.meeting.LocalBroadcastReceiver;
//import com.qunar.im.ui.util.AppManager;
//import com.qunar.im.ui.util.MediaUtil;
//import com.qunar.im.ui.util.Permissions;
//import com.qunar.im.ui.util.RTCStatusEnum;
//import com.qunar.im.ui.view.CircleImageView;
//import com.qunar.im.utils.ConnectionUtil;
//
//public class ReceiveTalkActivity extends Activity implements LocalBroadcastReceiver {
//
//    /**
//     * 检查运行时权限
//     */
//    private boolean mCheckPermissionResult = false;
//
//    /**
//     * 震动
//     */
//    private String userSrc;
//    private String jid;
//    private String toId;
//    private String name;
//    private String audio;
//    private String TAG = "ReceiveTalkActivity";
//
//    public static void startReceiveTalkActivity(Context context, RecentConversation recentConversation, String userJid, String audio) {
//        Intent calIntent = new Intent(context, ReceiveTalkActivity.class);
//        //携带数据
//        calIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        try {
//            calIntent.putExtra("audio", audio);
//            calIntent.putExtra("userSrc", recentConversation.getNick().getHeaderSrc());
//            calIntent.putExtra("name", recentConversation.getNick().getName());
//            calIntent.putExtra("jid", recentConversation.getId());
//            calIntent.putExtra("toId", userJid);
//        }catch (Exception e){
//            Log.e("ReceiveTalkActivity", "startReceiveTalkActivity" + "获取头像失败");
//            e.printStackTrace();
//        }
//        context.startActivity(calIntent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(getLayoutId());
//
//        ImmersionBar.with(this).statusBarColor("#111E2E").init();
//        getIntent(getIntent());
//
//        AppManager.addActivity(this);
//        //初始化倒计时器
//        initCountDownTimer();
//        //请求权限
//        requestMustPermission();
//        initViews();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SystemClock.sleep(500);
//                MediaUtil.playRing(getApplicationContext());
//            }
//        });
//    }
//
//    /**
//     * 60秒后关闭activity
//     */
//    private void initCountDownTimer() {
//        long time = 60000;
//        long countDownInterval = 1000;
//        CountDownTimer downTimer = new CountDownTimer(time, countDownInterval) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//                MediaUtil.stopRing();
//                finish();
//            }
//        };
//        downTimer.start();
//    }
//
//    protected int getLayoutId() {
//        Log.e("ReceiveTalkActivity", "getLayoutId" + "adding listener");
//        return R.layout.activity_reply_copy;
//    }
//
//    protected void getIntent(Intent intent) {
//        userSrc = intent.getStringExtra("userSrc");
//        jid = intent.getStringExtra("jid");
//        toId = intent.getStringExtra("toId");
//        name = intent.getStringExtra("name");
//        audio = intent.getStringExtra("audio");
//    }
//
//    protected void initViews() {
//        Log.e("ReceiveTalkActivity", "initViews");
//
//        CircleImageView userIcon = findViewById(R.id.userIcon);
//        ImageView btnCancel = findViewById(R.id.bt_invite_drop);
//        ImageView btnAnswer = findViewById(R.id.bt_invite_accept);
//        TextView tv_invite_number = findViewById(R.id.tv_invite_number);
//        TextView tv_invite_type = findViewById(R.id.tv_invite_type);
//        TextView encryption = findViewById(R.id.encryption);
//
//        if (!TextUtils.isEmpty(name)) {
//            tv_invite_number.setText(name);
//        }
//
//        if (audio != null && !TextUtils.isEmpty(audio)) {
//            if (audio.contains("语音")) {
//                encryption.setText("加密语音通话");
//            }else {
//                encryption.setText("加密视频通话");
//            }
//            tv_invite_type.setText("邀请你进行" + audio);
//        }
//
//        if (!TextUtils.isEmpty(userSrc) && userIcon != null) {
//            Glide.with(this)
//                    .load(userSrc)
//                    .placeholder(R.drawable.avatar_male)
//                    .dontAnimate()
//                    .transform(new GlideCircleTransform(getApplicationContext()))
//                    .into(userIcon);
//        }
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConnectionUtil.getInstance().sendSingleAllRead(jid, jid, MessageStatus.STATUS_SINGLE_READED + "");
//
//
////                Intent intent = new Intent("com.broadcasereceiver.PdChatItemClickReceiver");
////                intent.putExtra("rtcStatus", RTCStatusEnum.reject);
////                intent.putExtra("talkType", "singleTalk");
////                intent.putExtra("startSingleMettingJid", jid);         //向广播接收器传递数据
////                intent.putExtra("fromSingleMettingJid", toId);         //向广播接收器传递数据
////                QunarIMApp.getContext().sendBroadcast(intent);      //发送广播
//                MediaUtil.stopRing();
//                finish();
//            }
//        });
//
//        btnAnswer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConnectionUtil.getInstance().sendSingleAllRead(jid, jid, MessageStatus.STATUS_SINGLE_READED + "");
//
//                Log.e("ReceiveTalkActivity", "btnAnswer" + "setOnClickListener");
//                MediaUtil.stopRing();
////                Intent intent = new Intent("com.broadcasereceiver.PdChatItemClickReceiver");
////                if (!TextUtils.isEmpty(aliMeetingDetailConfigJson)) {
////                    intent.putExtra("aliMeetingDetailConfig", aliMeetingDetailConfigJson);         //向广播接收器传递数据
////                    intent.putExtra("startSingleMettingJid", jid);         //向广播接收器传递数据
////                    intent.putExtra("fromSingleMettingJid", toId);         //向广播接收器传递数据
////                    intent.putExtra("headerSrc", recentConversation.getNick().getHeaderSrc());         //向广播接收器传递数据
////                    QunarIMApp.getContext().sendBroadcast(intent);      //发送广播
////                }
//                finish();
//            }
//        });
//    }
//
//    public String[] permissions = {
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.VIBRATE,
//            Manifest.permission.DISABLE_KEYGUARD,
//            Manifest.permission.WAKE_LOCK
//    };
//
//    /**
//     * 申请应用必须的权限
//     */
//    private void requestMustPermission() {
//        Permissions.getInstance().checkPermissions(this, permissions, new Permissions.IPermissionsResult() {
//            @Override
//            public void passPermissions() {
//                mCheckPermissionResult = true;
//            }
//
//            @Override
//            public void forbidPermissions() {
//                mCheckPermissionResult = false;
//            }
//        });
//    }
//
//    /**
//     * 界面未销毁，启动此界面时回调
//     */
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        userSrc = intent.getStringExtra("userSrc");
//        jid = intent.getStringExtra("jid");
//        toId = intent.getStringExtra("toId");
//        name = intent.getStringExtra("name");
//        audio = intent.getStringExtra("audio");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        MediaUtil.stopRing();
//    }
//
//    @Override
//    public void onReceive(String broadcastName, Object obj) {
//
//    }
//}
