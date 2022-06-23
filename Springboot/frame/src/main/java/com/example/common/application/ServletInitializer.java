package com.example.common.application;

import com.example.common.application.FrameApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("spring.config.location", "classpath:/config/springboot/");   // application.yml 위치를 변경한다.

        return application.sources(FrameApplication.class);
    }

}
