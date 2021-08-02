/**
 * Copyright © 2013 Qunar.com Inc. All Rights Reserved.
 */
package com.qunar.im.ui.schema;

/**
 * jerry.li
 * 度假scheme跳转的枚举类
 */
public enum QchatSchemaEnum {
    //todo 待删除
    chat(QchatSchemaImpl.instance,"/chat"),
    group_member(QGroupMemberSchemaImpl.instance,"/group_member"),

    searchDetails(SearchDetailsImpl.instance,"/search_details"),
    developer_chat(QOpenDeveloperChat.instance,"/developer_chat"),
    logout(QLogoutImpl.instance,"/logout"),
    openGroupChat(QOpenGroupCaht.instance,"/openGroupChat"),
    openSingleChat(QOpenSingleChat.instance,"/openSingleChat"),
    //本地搜索
    openChatForSearch(QOpenChatForSearch.instance,"/openChatForSearch"),
    //搜索返回
    openChatForNetSearch(QopenChatForNetSearch.instance,"/openChatForNetSearch"),
    //会话内图片视频搜索
    openSearchChatImage(QOpenSearchChatImage.instance,"/openSearchChatImage"),
    //打开大图
    openBigImage(QOpenBigImage.instance,"/openBigImage"),
    //图片选择
    openPictureSelector(QOpenPictureSelector.instance,"/openPictureSelector"),
    //图片处理
    openCamerSelecter(QOpenCamerSelecter.instance,"/openCamerSelecter"),

    openPhoneNumber(QOpenPhoneNumber.instance,"/openPhoneNumber"),
    //我的文件
    myfile(QMyFileImpl.instance,"/myfile"),
    //配置域名
    openAccountSwitch(QAccountSwitchSchemaImpl.getInstance(),"/accountSwitch"),
    //下载文件
    openDownLoad(QOpenFileDownLoadImpl.instance,"/openDownLoad"),
    openHeadLine(QOpenHeadLineSchemaImpl.getInstance(),"/headLine"),
    openNavConfig(QOpenNavConfigImpl.getInstance(),"/openNavConfig");






    private String path;
    private QChatSchemaService service;
    /**
     * 如果是startActivityForResult启动的，需要backToActivity的scheme需要在这个集合里
     */

    QchatSchemaEnum(QChatSchemaService service, String path) {
        this.service = service;
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }

    public QChatSchemaService getService() {
        return service;
    }


    public static QchatSchemaEnum getSchemeEnumByPath(String path){
        if(path == null)
            return null;
        for(QchatSchemaEnum e:QchatSchemaEnum.values()){
            if(e.path.equalsIgnoreCase(path)){
                return e;
            }
        }
        return null;
    }

}
