package com.qunar.im.ui.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qunar.im.base.module.IMMessage;
import com.qunar.im.ui.util.RTCStatusEnum;

public class PdChatItemClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (onPdChatItemClick == null) return;
        if (intent.hasExtra("talkType")) {
            String talkType = intent.getStringExtra("talkType");

            if (talkType.equals("singleTalk")) {
                int itemType = intent.getIntExtra("itemType", -1);
                String toJid = intent.hasExtra("toSingleJid") ? intent.getStringExtra("toSingleJid") : "";
                String fromJid = intent.hasExtra("fromSingleJid") ? intent.getStringExtra("fromSingleJid") : "";
                RTCStatusEnum rtcStatus = intent.hasExtra("rtcStatus") ? (RTCStatusEnum) intent.getSerializableExtra("rtcStatus") : null;
                onPdChatItemClick.onPdChatSingleTalk(toJid, fromJid, itemType, rtcStatus);
            }else if (talkType.equals("meeting")) {
                onPdChatItemClick.onPdChatNoticeMeetingClick("toJid", "fromJid", "itemType" );
            }
        }
    }

    public OnPdChatTextItemClick onPdChatItemClick;

    public void setOnPdChatTextItemClick(OnPdChatTextItemClick onPdChatItemClick) {
        this.onPdChatItemClick = onPdChatItemClick;
    }

    public interface OnPdChatTextItemClick {

        void onPdChatNoticeMeetingClick(String toJid, String fromJid, String aliMeetingDetailConfig);

        void onPdChatSingleTalk(String toJid, String fromJid, int itemType, RTCStatusEnum talkType);
    }
}
