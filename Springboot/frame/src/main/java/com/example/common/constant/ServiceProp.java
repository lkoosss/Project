package com.example.common.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "service")
@Data
public class ServiceProp {
    public String name;
    public String id;
    public String ip;

    public SystemProp.AConfig aConfig;
    public SystemProp.BConfig bConfig;

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
