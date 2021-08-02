package com.qunar.im.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.ui.R;
import com.qunar.im.base.module.BaseIMMessage;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.ui.util.RTCStatusEnum;
import com.qunar.im.ui.view.baseView.ExtendBaseView;
import com.qunar.im.ui.view.baseView.processor.ProcessorFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaokai on 15-9-15.
 */
public class ExtendChatViewAdapter extends ChatViewAdapter {
    private static final String TAG = "ExtendChatViewAdapter";
    private boolean shareStatus = false;
    private Map<String, IMMessage> sharingMsg = new HashMap<>();
    public String animateMsgId = null;

    public void changeShareStatus(boolean b) {
        shareStatus = b;
        if (!b) sharingMsg.clear();
    }

    public boolean isShareStatus() {
        return shareStatus;
    }

    public List<IMMessage> getSharingMsg() {
        List<IMMessage> list = new ArrayList<>();
        list.addAll(sharingMsg.values());
        return list;
    }


    public ExtendChatViewAdapter(Context context, String toId, Handler uiHandler, boolean showNick) {
        super(context, toId, uiHandler, showNick);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExtendBaseView extendBaseView;
        LogUtil.d(TAG, "getView " + position);
        if (convertView == null) {
            extendBaseView = (ExtendBaseView) LayoutInflater.from(context.get()).inflate(R.layout.atom_ui_item_chat_extend, null);
            convertView = extendBaseView;
            extendBaseView.setGravatarHandler(gravatarHandler);
            extendBaseView.setLeftImageLongClickHandler(leftImageLongClickHandler);
            extendBaseView.setLeftImageClickHandler(leftImageClickHandler);
            extendBaseView.setContextMenuRegister(contextMenuRegister);
            extendBaseView.setRightSendFailureClickHandler(rightSendFailureClickHandler);
            extendBaseView.initFontSize();
        } else {
            extendBaseView = (ExtendBaseView) convertView;
        }
        IMMessage message = getItem(position);
        if (message.getDirection() == IMMessage.DIRECTION_RECV &&
                TextUtils.isEmpty(message.getFromID())) {
            message.setFromID(toId);
        }

        if (message.getMsgType() == RTCStatusEnum.msgTypeAudioCall || message.getMsgType() == RTCStatusEnum.msgTypeAudio
            || message.getMsgType() == RTCStatusEnum.msgTypeVideo || message.getMsgType() == RTCStatusEnum.msgTypeVideoCall) {

        }
        try {
            extendBaseView.setMessage(this, handler, message, position);
        } catch (Exception e) {
            LogUtil.e(TAG, "ERROR", e);
        }
        extendBaseView.setNickStatus(showNick && message.position == BaseIMMessage.LEFT);
//        extendBaseView.setReadStateShow(QtalkNavicationService.getInstance().isShowmsgstat() && !showNick);
//        Log.e("IM_LOG", TAG + "getView: " + QtalkNavicationService.getInstance().isShowmsgstat() + "---" + showReadState);
        extendBaseView.setReadStateShow(QtalkNavicationService.getInstance().isShowmsgstat() && showReadState);
        if (shareStatus) {
            extendBaseView.setCheckboxEvent(null);
            if (ProcessorFactory.getMiddleType().contains(message.getMsgType()) ||
                    message.getType() == IMMessage.DIRECTION_MIDDLE ||
                    (message.getType() != ConversitionType.MSG_TYPE_GROUP && message.getType() != ConversitionType.MSG_TYPE_CHAT)) {
                extendBaseView.getCheckBox().setEnabled(false);
                extendBaseView.changeChbStatus(false);
                extendBaseView.getCheckBox().setVisibility(View.GONE);
            } else {
                extendBaseView.getCheckBox().setVisibility(View.VISIBLE);
                extendBaseView.getCheckBox().setEnabled(true);
                extendBaseView.changeChbStatus(sharingMsg.containsKey(message.getId()));
            }
            extendBaseView.setCheckboxEvent(checkedChangeListener);
            extendBaseView.saveId2Chb(message);
        }
        extendBaseView.changeShareStatus(shareStatus);
        if (animateMsgId != null && message.getId().equals(animateMsgId)) {
            animateMsgId = null;
            extendBaseView.startAnimate(3);
        }

        extendBaseView.setVisibility(View.VISIBLE);
        if (message.getMsgType() == RTCStatusEnum.msgTypeAudioCall || message.getMsgType() == RTCStatusEnum.msgTypeAudio
                || message.getMsgType() == RTCStatusEnum.msgTypeVideo || message.getMsgType() == RTCStatusEnum.msgTypeVideoCall) {
            try {
                JSONObject bodyObject = new JSONObject(message.getExt());
                if (bodyObject.has("show") && !bodyObject.getBoolean("show")) {
                    extendBaseView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                extendBaseView.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                IMMessage message = (IMMessage) buttonView.getTag();
                if (isChecked) {
                    sharingMsg.put(message.getId(), message);
                } else {
                    sharingMsg.remove(message.getId());
                }
            } catch (Exception ex) {
                buttonView.setOnCheckedChangeListener(null);
                buttonView.setChecked(!isChecked);
                buttonView.setOnCheckedChangeListener(this);
            }
        }
    };
}
