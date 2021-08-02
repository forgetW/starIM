package com.qunar.im.ui.util;

// RTC通话状态枚举
public enum RTCStatusEnum {
    // call 拨打
    call("call"),
    // answer 接听(带回会议参数)
    answer("answer"),
    // cancel 取消通话   挂断
    cancel("cancel"),
    // reject 拒绝通话
    reject("reject"),
    // engaged 忙线
    engaged("engaged"),
    // overtime 超时
    overtime("overtime");

    // 语音通话 WebRTC_MsgType_AudioCall_VALUE
    public static int msgTypeAudioCall = 1101201;
    // 视频通话 WebRTC_MsgType_VideoCall_VALUE
    public static int msgTypeVideoCall = 1101202;
    // 语音会议 WebRTC_MsgType_Audio_VALUE
    public static int msgTypeAudio = 1101203;
    // 视频会议 WebRTC_MsgType_Video_VALUE
    public static int msgTypeVideo = 1101204;

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // 构造方法
    RTCStatusEnum(String value) {
        this.value = value;
    }
}
