package com.example.finance.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

//全局拦截器，会捕获所有Controller抛出的异常，交给对应的@ExceptionHandler方法处理
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //由于业务异常（余额不足，账户不存在）需要返回给用户让前端显示，而系统异常（BUG：空指针，SQL语法错）不能给用户看到，需要分开处理
    //业务异常：把BusinessException中的提示语直接返回给前端
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusiness(BusinessException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    //参数校验失败：把所有字段错误拼成一句话
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + "：" + err.getDefaultMessage())
                .collect(Collectors.joining("；"));
        return R.fail(400, msg);
    }

    //兜底：其他所有异常统一返回"服务器内部错误"，但日志里记详情
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleOther(Exception e) {
        log.error("系统异常", e);
        return R.fail(500, "服务器内部错误，请联系管理员");
    }
}