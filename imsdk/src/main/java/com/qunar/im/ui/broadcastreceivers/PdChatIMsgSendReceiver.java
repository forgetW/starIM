package com.qunar.im.ui.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.model.LatLng;

public class PdChatIMsgSendReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String chatjid = (String) intent.getSerializableExtra("chatjid");
        String type = (String) intent.getSerializableExtra("type");
        boolean isFromChatRoom = false;
        String clickType = "stop";
        if (intent.hasExtra("isFromChatRoom")) {
            isFromChatRoom = intent.getBooleanExtra("isFromChatRoom", false);
        }
        if (intent.hasExtra("clickType")) {
            clickType = intent.getStringExtra("clickType");
        }

        if (onChatMsgClick != null && null != type){
            switch (type) {
                case "onStop":
                    onChatMsgClick.onChatMsgClick(chatjid, clickType);
                    break;
                case "onLeftImageClickEvent":
                    onChatMsgClick.onLeftImageClickEvent(chatjid, context);
                    break;
                    case "onChatRoomClick":
                    onChatMsgClick.onChatRoomClick(chatjid, isFromChatRoom);
                    break;
            }
        }

        if (onChatBaiMapNavi != null && intent.hasExtra("lat") && intent.hasExtra("lng")) {
            String lat = intent.getStringExtra("lat");
            String lng = intent.getStringExtra("lng");
            onChatBaiMapNavi.onBaidiMapNaviClick(lat, lng);
        }

    }

    public OnChatMsgClick onChatMsgClick;

    public void setOnChatMsgClick(OnChatMsgClick onChatMsgClick) {
        this.onChatMsgClick = onChatMsgClick;
    }

    public interface OnChatMsgClick{

        void onChatRoomClick(String jid, boolean isFromChatRoom);

        void onChatMsgClick(String jid, String clickType);

        void onLeftImageClickEvent(String jid, Context context);

    }

    public void setOnChatBaiMapNavi(OnChatBaiMapNavi onChatBaiMapNavi) {
        this.onChatBaiMapNavi = onChatBaiMapNavi;
    }

    public OnChatBaiMapNavi onChatBaiMapNavi;

    public interface OnChatBaiMapNavi{

        void onBaidiMapNaviClick(String lat, String lng);
    }
}
