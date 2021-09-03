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
    VOTE_REPEAT("您已点赞过");

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