package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常 当输入的用户名字重复的时候
     * @param ex
     * @return
     */

//    @ExceptionHandler
//    public Result duplicate
    @ExceptionHandler
    public Result handleSQLIntegrityException(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();

        if(message.contains("Duplicate entry")){
            String[] s = message.split(" ");
            String name = s[2];
            String errorMessage = name + MessageConstant.ALREADY_EXISTS;
            return Result.error(errorMessage);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }
}