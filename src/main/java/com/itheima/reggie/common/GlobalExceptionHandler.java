package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ControllerAdvice(annotations = {RestController.class,ControllerAdvice.class})
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info("异常信息为:"+ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已经存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomerException.class)
    public R<String> exceptionHandler(CustomerException ex){
        log.info("异常信息为:"+ex.getMessage());
        return R.error(ex.getMessage());
    }
}
