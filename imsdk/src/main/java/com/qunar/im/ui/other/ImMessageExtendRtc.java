package com.qunar.im.ui.other;

import java.io.Serializable;

public class ImMessageExtendRtc implements Serializable {

    /**
     * show : true
     * status : 1
     * signature : {"appId":"","nonce":"","userId":"","channelId":"","timestamp":2233,"token":"","joinPassword":"","gslb":"","username":"","meetingName":"","type":"","startTime":"","founderName":"","founder":false,"meeting":false}
     */

    private boolean show;
    private String status;
    private MeetingInfoBean meetingInfo;

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MeetingInfoBean getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(MeetingInfoBean meetingInfo) {
        this.meetingInfo = meetingInfo;
    }
}
