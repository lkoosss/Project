package com.example.common.value;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/** System 설정 (system-properties.yml) **/
@Component
@Getter @Setter
@ConfigurationProperties(prefix = "system")
public class SystemProp {

    /** Base 설정 **/
    public BaseProp baseProp;

    /** A 설정 **/
    public AProp aProp;

    /** B 설정 **/
    public BProp bProp;


    /** Base 설정 **/
    @Getter @Setter
    public static class BaseProp {
        public String appName;      // App 이름
        public String serverType;   // 서버 타입
        public String serverId;     // 서버 ID
        public String serverIp;     // 서버 IP
        public String serverPort;   // 서버 Port
        public String version;      // 서버 Version
        public String saltKey;      // 서버 Salt Key
    }

    /** A 설정 **/
    @Getter @Setter
    public static class AProp {
        public int A1;
        public String A2;
        public boolean A3;
        public List<String> A4;
        public List<String> A5;
        public AGroup aGroup;

        @Getter @Setter
        public static class AGroup {
            public int A6;
        }
    }

    /** B 설정 **/
    @Getter @Setter
    public static class BProp {
        public int B1;
        public String B2;
        public boolean B3;
        public List<String> B4;
        public List<String> B5;
        public BGroup aGroup;

        @Getter @Setter
        public static class BGroup {
            public int B6;
        }
    }
}
