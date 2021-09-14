package com.caostudy.wiki.exception;

public class BusinessException extends RuntimeException{

    private BusinessExceptionCodeEnum code;

    /**
     * 通过BusinessExceptionCodeEnum这个枚举内定义的message进行固定信息返回
     * @param code
     */
    public BusinessException (BusinessExceptionCodeEnum code) {
        super(code.getDesc());
        this.code = code;
    }

    /**
     * 用于处理需要变量的错误信息返回
     * @param message
     */
    public BusinessException (String message) {
        BusinessExceptionCodeEnum exceptionCodeEnum=BusinessExceptionCodeEnum.CUSTOM_MESSAGE;
        exceptionCodeEnum.setDesc(message);
        this.code=BusinessExceptionCodeEnum.CUSTOM_MESSAGE;
    }

    public BusinessExceptionCodeEnum getCode() {
        return code;
    }

    public void setCode(BusinessExceptionCodeEnum code) {
        this.code = code;
    }

    /**
     * 不写入堆栈信息，提高性能
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}