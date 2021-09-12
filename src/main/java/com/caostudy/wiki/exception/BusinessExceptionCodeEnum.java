package com.caostudy.wiki.exception;

/**
 * 错误代码信息枚举
 */
public enum BusinessExceptionCodeEnum {
    //用户名存在
    USER_LOGIN_NAME_EXIST("登录名已存在"),
    //用户名不存在或密码错误
    LOGIN_USER_ERROR("用户名不存在或密码错误"),
    //您已点赞过
    VOTE_REPEAT("您已点赞过"),
    //用户还未登录
    NOBODY_OPERATING("当前没有用户在操作文档"),
    //当前文档已有用户在操作
    HAS_PEOPLE_OPERATING("当前文档仍有其他用户在操作");


    private String desc;

    BusinessExceptionCodeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}