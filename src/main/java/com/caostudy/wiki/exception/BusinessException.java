package com.caostudy.wiki.exception;

public class BusinessException extends RuntimeException{

    private BusinessExceptionCodeEnum code;

    public BusinessException (BusinessExceptionCodeEnum code) {
        super(code.getDesc());
        this.code = code;
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