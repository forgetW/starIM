package com.qunar.im.ui.meeting;

import com.qunar.im.ui.util.RTCStatusEnum;

import java.io.Serializable;
import java.util.List;

public class ChatMeetingBean implements Serializable {

    int itemType;
    String talkType;
    String fromJid;
    List<String> toJids;
    RTCStatusEnum rtcStatus;

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTalkType() {
        return talkType;
    }

    public void setTalkType(String talkType) {
        this.talkType = talkType;
    }

    public String getFromJid() {
        return fromJid;
    }

    public void setFromJid(String fromJid) {
        this.fromJid = fromJid;
    }

    public List<String> getToJids() {
        return toJids;
    }

    public void setToJids(List<String> toJids) {
        this.toJids = toJids;
    }

    public RTCStatusEnum getRtcStatus() {
        return rtcStatus;
    }

    public void setRtcStatus(RTCStatusEnum rtcStatus) {
        this.rtcStatus = rtcStatus;
    }
}
