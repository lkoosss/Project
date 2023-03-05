package com.example.app.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.example.common.value.Constant.LogMarker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
public class AppLifeCycle {

    /**
     * <pre>
     *  appInit
     *  - Was 시작
     * </pre>
     */
    @PostConstruct
    public void appInit() {
        log.info(LogMarker.config, "################## All bean objects loaded ##################");
    }

    /**
     * <pre>
     *  appDestroy
     *  - Was 종료
     * </pre>
     */
    @PreDestroy
    public void appDestroy() {
        log.info(LogMarker.config, "######################## Server Destroy ########################");
    }
}
