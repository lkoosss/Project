package com.example.common.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.format.DateTimeFormatter;

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

    /** 날짜시간 형식 상수 **/
    public static class TimeFormat {
        public static final String FORMAT_00 = "yyyy";

        public static final String FORMAT_01 =  "yyyy-MM";
        public static final String FORMAT_02 =  "yyyy-MM-dd";
        public static final String FORMAT_03 =  "yyyy-MM-dd HH";
        public static final String FORMAT_04 =  "yyyy-MM-dd HH:mm";
        public static final String FORMAT_05 =  "yyyy-MM-dd HH:mm:ss";
        public static final String FORMAT_06 =  "yyyy-MM-dd HH:mm:ss.SSS";

        public static final String FORMAT_07 = "yyyyMM";
        public static final String FORMAT_08 = "yyyyMMdd";
        public static final String FORMAT_09 = "yyyyMMddHH";
        public static final String FORMAT_10 = "yyyyMMddHHmm";
        public static final String FORMAT_11 = "yyyyMMddHHmmss";
        public static final String FORMAT_12 = "yyyyMMddHHmmssSSS";

        /** DateTimeFormatter (for LocalDateTime) **/
        public static final DateTimeFormatter DTF_FORMAT_YYYYMM         = DateTimeFormatter.ofPattern(FORMAT_07);
        public static final DateTimeFormatter DTF_FORMAT_YYYYMMDD       = DateTimeFormatter.ofPattern(FORMAT_08);
        public static final DateTimeFormatter DTF_FORMAT_YYYYMMDDHH     = DateTimeFormatter.ofPattern(FORMAT_09);
        public static final DateTimeFormatter DTF_FORMAT_YYYYMMDDHHMM   = DateTimeFormatter.ofPattern(FORMAT_10);

    }

    /** Log Marker 상수 **/
    public static class LogMarker {
        public static final Marker config       = MarkerFactory.getMarker("CONFIG");
        public static final Marker api          = MarkerFactory.getMarker("API");
        public static final Marker zookeeper    = MarkerFactory.getMarker("ZOOKEEPER");
        public static final Marker filter       = MarkerFactory.getMarker("FILTER");
        public static final Marker tus          = MarkerFactory.getMarker("TUS");
    }
}
