package com.zy.chat.netty.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @AUTHOR zhangy
 * 2020-07-22  20:58
 * 跨域配置
 */
@Configuration
public class WebMvcConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 4 对接口配置跨域设置
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1 设置访问源地址
        corsConfiguration.addAllowedOrigin("*");
        // 2 设置访问源请求头
        corsConfiguration.addAllowedHeader("*");
        // 3 设置访问源请求方法
        corsConfiguration.addAllowedMethod("*");
        // 4 设置访问源带cookie
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

}
