package com.example.finance.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(BusinessException.class);
    private final int code;
    public BusinessException(String message){
        super(message);
        this.code = 400;
    }
    public BusinessException(int code, String message){
        super(message);
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}