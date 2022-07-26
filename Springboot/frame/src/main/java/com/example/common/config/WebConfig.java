package com.example.common.config;

import com.example.common.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //////////// JS, CSS 파일의 Search Path 지정부분 ////////////
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(20);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(20);
    }

    //////////// 인터셉터 추가설정 부분 ////////////
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())       // Interceptor추가하고
                .addPathPatterns("/**")                         // 어느 URI일 때 인터셉터에 넘겨줄지
                .excludePathPatterns("");                       // 어느 URI는 제외시킬 것인지
    }
}
