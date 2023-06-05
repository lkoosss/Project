package com.example.app.lifecycle;

import com.example.common.manager.ContextManager;
import com.example.common.manager.ZookeeperManager;
import com.example.common.value.ServiceProp;
import com.example.common.value.ServiceProp.BaseProp;
import com.example.common.value.ServiceProp.ZookeeperProp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import com.example.common.value.Constant.LogMarker;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Component
@DependsOn(value = {"contextManager"})
public class AppLifeCycle {
    /*-------------------
    |   ServiceProp 부분  |
     -------------------*/
    private BaseProp baseProp;
    private ZookeeperProp zookeeperProp;


    /**
     * <pre>
     *  appInit
     *  - Was 시작
     * </pre>
     */
    @PostConstruct
    public void appInit() {
        log.info(LogMarker.config, "################## All bean objects loaded ##################");
        ServiceProp serviceProp = ContextManager.getBean("serviceProp");
        this.baseProp = serviceProp.baseProp;
        this.zookeeperProp = serviceProp.zookeeperProp;

//        ZookeeperManager.getInstance().init();
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
