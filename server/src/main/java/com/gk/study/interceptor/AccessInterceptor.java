package com.gk.study.interceptor;

import com.google.gson.Gson;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 该拦截器有两个用途：1.记录请求耗时日志 2.接口验权
 */
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    private static UserService userService;

    @Autowired
    public void setUserService( UserService userService) {
        // 为解决先@Component 后@Autowired失效的方案
        AccessInterceptor.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        request.setAttribute("_startTime", System.currentTimeMillis());

        //**********************验权代码*************************
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);
        if (access == null) {
            // 如果注解为null, 说明不需要拦截, 直接放过
            return true;
        }

        // 管理员
        if(access.level().getCode() == AccessLevel.ADMIN.getCode()) {
            String token = request.getHeader("ADMINTOKEN");
            User user = userService.getUserByToken(token);
            if(user != null && user.getRole().equals(String.valueOf(User.AdminUser))){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "无操作权限");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        // 用户
        if(access.level().getCode() == AccessLevel.LOGIN.getCode()) {
            String token = request.getHeader("TOKEN");
            User user = userService.getUserByToken(token);
            if(user != null && user.getRole().equals(String.valueOf(User.NormalUser))){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "未登录");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //**********************记录log代码 (改为控制台打印)*************************
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) request.getAttribute("_startTime");
        if (startTime != null) {
            Long diff = (endTime - startTime);
            // 既然不存数据库了，就在后台控制台打印一下请求URL和耗时即可
            logger.info("请求URL: {} | 耗时: {} ms", request.getRequestURI(), diff);
        }
    }

    public void writeResponse(HttpServletResponse response, APIResponse apiResponse) throws IOException {
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(apiResponse);
        response.getWriter().println(jsonStr);
        response.getWriter().flush();
    }
}