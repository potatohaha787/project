package com.gk.study.handler;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public APIResponse handleException(Exception ex){
        // 依然在后台控制台打印错误，方便你开发时看报错信息
        logger.error("系统异常======>" + ex.getMessage(), ex);

        // 直接返回错误信息给前端，不再调用 service 保存到数据库
        return new APIResponse(ResponeCode.FAIL, ex.getMessage());
    }
}