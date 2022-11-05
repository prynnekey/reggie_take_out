package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * @author prynn
 */
@RestControllerAdvice(basePackages = "com.itheima.reggie.controller")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());
        //获取已存在的用户名 并通知用户该账户已存在
        if(e.getMessage().contains("Duplicate entry")){
            String[] split = e.getMessage().split("'");
            return R.error("失败："+'"'+split[1]+'"'+"已存在");
        }

        return R.error("发生了未知错误");
    }


    @ExceptionHandler(BusinessException.class)
    public R<String> exceptionHandler(BusinessException e){
        log.error(e.getMessage());

        //提醒用户异常
        return R.error(e.getMessage());
    }
}
