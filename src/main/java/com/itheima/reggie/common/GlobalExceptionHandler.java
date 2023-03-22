package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//@ResponseBody的作用其实是将java对象转为json格式的数据。
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 进行异常处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHander(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            return R.error(split[2]+"已存在");

        }
        return R.error("失败了");

    }

}
