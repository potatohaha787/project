package com.gk.study.config;

import com.gk.study.interceptor.AccessInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 自定义拦截器
        registry.addInterceptor(new AccessInterceptor());
    }

    /**
     * 推荐使用 CorsFilter 解决跨域！
     * 它的优先级高于拦截器，能完美解决带 Token 自定义请求头时的 OPTIONS 预检请求失败问题
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有域名 (或者你的前端地址 http://localhost:8000)
        config.setAllowCredentials(true);    // 允许携带 Cookie / Token 等凭证
        config.addAllowedMethod("*");        // 允许所有请求方法 (GET, POST, OPTIONS 等)
        config.addAllowedHeader("*");        // 关键点：允许所有自定义请求头 (比如 TOKEN, ADMINTOKEN)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对项目中所有接口生效
        return new CorsFilter(source);
    }
}