package com.example.common.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constant {

    /** Spring default 설정 상수 **/
    public static class SpringConst {
        public static final String LOCATION_KEY             = "spring.config.location";                     // application.yml 파일 Path Key
        public static final String LOCATION_VALUE           = "classpath:config/springboot/";               // application.yml 파일 Path Value (Default)
        public static final String PROFILES_ACTIVE_KEY      = "spring.profiles.active";                     // Profiles Active Key
        public static final String PROFILES_ACTIVE_VALUE    = "local";                                      // Profiles Active Value (Default)
        public static final String LOG_CONFIG_KEY           = "logging.config";                             // Logging 설정파일 Path Key
        public static final String LOG_CONFIG_VALUE         = "classpath:config/logback/logback-%s.xml";    // Logging 설정파일 Path Value
        public static final String LOCAL_IP_KEY             = "server.ip";                                  // app이 실행되는 서버 IP
    }

    /** Log Marker 상수 **/
    public static class LogMarker {
        public static final Marker config       = MarkerFactory.getMarker("CONFIG");
        public static final Marker api          = MarkerFactory.getMarker("API");
    }
}
