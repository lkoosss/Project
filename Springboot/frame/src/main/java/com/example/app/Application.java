package com.example.app;

import com.example.common.value.Constant.SpringConst;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Application **/
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.example.app", "com.example.common", "com.example.sample", "com.example.web"})
public class Application extends SpringBootServletInitializer {

    /**
     * SpringBoot Application을 실행하는 메서드 (내장 톰캣용)
     */
    public static void main(String[] args) {
        SetupHelper.initSystemProperty();
        SpringApplication.run(Application.class, args);
    }

    /**
     * SpringBoot Application을 실행하는 메서드 (외부 WAS용)
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        SetupHelper.initSystemProperty();
        setRegisterErrorPageFilter(false);
        return application.sources(Application.class);
    }


    /** SpringBoot 설정 도우미 **/
    public static class SetupHelper {


        /**
         * <pre>
         *  initSystemProperty
         *  - Profile, application.yml 경로, logback.xml 경로 설정
         * </pre>
         */
        public static void initSystemProperty() {
            // profile Mode 체크 후 없으면 Local로 설정
            checkSystemProperty(SpringConst.PROFILES_ACTIVE_KEY, SpringConst.PROFILES_ACTIVE_VALUE);

            // Springboot의 application.yml 파일 위치
            System.setProperty(SpringConst.LOCATION_KEY, SpringConst.LOCATION_VALUE);

            // Logging 설정 파일 Path 설정
            System.setProperty(SpringConst.LOG_CONFIG_KEY, String.format(SpringConst.LOG_CONFIG_VALUE, System.getProperty(SpringConst.PROFILES_ACTIVE_KEY)));
        }

        /**
         * <pre>
         * 	checkSystemProperty
         * 	- System property 확인
         * 	  > Null or "" or " " 인 경우 Default Value로 설정
         * </pre>
         *
         * @param key
         * @param value
         */
        public static void checkSystemProperty(String key, String value) {
            if (System.getProperty(key) == null || System.getProperty(key).trim().isEmpty()) {
                System.setProperty(key, value);
            }
        }
    }

}
