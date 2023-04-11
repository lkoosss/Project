package com.example.common.value;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "service")
public class ServiceProp {

    /** Base 설정 **/
    public BaseProp baseProp;

    /** Base 설정 **/
    public ZookeeperProp zookeeperProp;


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

    @Getter @Setter
    public static class ZookeeperProp {
        public boolean serviceEnable;   // Zookeeper 사용여부 (true : 사용 | false : 미사용)
        public int retryCnt;            // Zookeeper 재시도 횟수
        public int retryInterval;       // Zookeeper 재시도 간격 (ms)
        public int lockWaitTime;        // Lock 대기시간 (sec)
        public List<String> serverList; // Zookeeper 서버목록
    }
}
