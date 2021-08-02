package com.qunar.im.ui.other;

import java.io.Serializable;

public class MeetingInfoBean implements Serializable {
    public String getJoinPassword() {
        return joinPassword;
    }

    public void setJoinPassword(String joinPassword) {
        this.joinPassword = joinPassword;
    }

    /***
     * "appId": "67k13w28",
     * "nonce": "AK-36dbfa3f58b14f1e971838b763cdb112",
     * "userId": "efe338f4220789a8",
     * "channelId": "fb0a367ca0ce4a3d9dcc900b1b51e35d",
     * "timestamp": 1599384638,
     * "token": "69710bf0ab6e86cbdef9b18e64dc92606b52ef7b3cf93ea3537c9235b69663a8",
     * "gslb": "https://rgslb.rtc.aliyuncs.com",
     * "username": "郑运"
     * "founder": false
     * "metting": false
     * "meetingName": ""
     * "startTime": ""
     * "joinPassword": ""
     */

    private String joinPassword;
    private String meetingName;
    private String startTime;
    private String appId;
    private String nonce;
    private String userId;
    private String channelId;
    private long timestamp;
    private String token;
    private String gslb;
    private String username;

    public String getToName() {
        return toName == null ? "" : toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getHeadSrc() {
        return headSrc == null ? "" : headSrc;
    }

    public void setHeadSrc(String headSrc) {
        this.headSrc = headSrc;
    }

    private String headSrc;
    private String toName;
    private String type;
    private boolean founder = false;
    private boolean meeting = false;

    public boolean isMeeting() {
        return meeting;
    }

    public void setMeeting(boolean metting) {
        this.meeting = metting;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFounder() {
        return founder;
    }

    public void setFounder(boolean founder) {
        this.founder = founder;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGslb() {
        return gslb;
    }

    public void setGslb(String gslb) {
        this.gslb = gslb;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
