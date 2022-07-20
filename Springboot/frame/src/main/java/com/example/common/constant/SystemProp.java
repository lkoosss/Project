package com.example.common.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * System 설정
 * - system-properties.yml 설정
 */
@Component
@ConfigurationProperties(prefix = "system")
@Data
public class SystemProp {
    public String name;
    public String id;
    public String ip;

    public AConfig aConfig;
    public BConfig bConfig;

    @Data
    public static class AConfig {
        public int A1;
        public String A2;
    }

    @Data
    public static class BConfig {
        public int B1;
        public String B2;
    }
}
