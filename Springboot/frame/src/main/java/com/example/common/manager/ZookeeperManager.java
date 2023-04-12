package com.example.common.manager;

import com.example.common.value.ServiceProp;
import com.example.common.value.ServiceProp.ZookeeperProp;
import com.example.common.value.SystemProp;
import com.example.common.value.Constant.LogMarker;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class ZookeeperManager {
    /*-------------------
    |   ServiceProp 부분  |
     -------------------*/
    private ZookeeperProp zookeeperProp;
    /*------------------
    |   클래스 귀속 변수   |
     ------------------*/
    private CuratorFramework client;								// Zookeeper client
    HashMap<String, TreeCache> treeCacheHashMap;					// Znode Watcher Map
    private ConnectionStateListener connectionEvent;				// connection Event
    private CuratorListener curatorEvent;							// curator Event
    private List<Consumer<ConnectionState>> connectionEventList;	// Connection Event목록


    /** 싱글톤 유지를 위한 instance 생성 **/
    private static class Holder {
        public static final ZookeeperManager INSTANCE = new ZookeeperManager();
    }

    /**
     * <pre>
     *  getInstance
     * </pre>
     *
     * @return
     */
    public static ZookeeperManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * <pre>
     *  Constructor of ZookeeperManager class
     * </pre>
     */
    public ZookeeperManager() {
        /*-------------------
        |   ServiceProp 부분  |
         -------------------*/
        ServiceProp serviceProp = ContextManager.getBean("serviceProp");
        this.zookeeperProp          = serviceProp.zookeeperProp;
        this.treeCacheHashMap       = new HashMap<>();
        this.connectionEventList    = new ArrayList<>();
        /*------------------
        |   클래스 귀속 변수   |
         ------------------*/
        // Connection Event
        this.connectionEvent        = (cli, newState) -> {
            log.info(LogMarker.config, "connectionState: {}", newState);
            // Connection 추가 이벤트
            for (int i = 0; i < this.connectionEventList.size(); i++) {
                this.connectionEventList.get(i).accept(newState);
            }
        };
        // Curator Event
        this.curatorEvent           = (cli, event) -> {
            log.info(LogMarker.config, "curator: {}", cli.getState());
        };
    }

    public void init() {
        log.info(LogMarker.config, "-----------------------------------------------------------");
        log.info(LogMarker.config, "|                     Zookeeper Config                    |");
        log.info(LogMarker.config, "-----------------------------------------------------------");
        log.info(LogMarker.config, "- serviceEnable: {}", this.zookeeperProp.serviceEnable);
        if (!this.zookeeperProp.serviceEnable) {
            return;
        }
        if (!CollectionUtils.isEmpty(this.zookeeperProp.serverList)) {
            // Server 랜덤섞기
            Collections.shuffle(this.zookeeperProp.serverList);
            // Server 리스트를 문자열로 변환
            String serverList = this.zookeeperProp.serverList.toString().replaceAll("\\[|\\]| ", "");
            // Server 재시도 정책 설정
            RetryPolicy retryPolicy = new RetryNTimes(this.zookeeperProp.retryCnt, this.zookeeperProp.retryInterval);
            // Zookeeper Client 생성
            this.client = CuratorFrameworkFactory.newClient(serverList, retryPolicy);
            // Zookeeper Connection Event 등록
            this.client.getConnectionStateListenable().addListener(this.connectionEvent);
            // Zookeeper Curator Event 등록
            this.client.getCuratorListenable().addListener(this.curatorEvent);
            // Zookeeper Client 시작
            this.client.start();
        }
        log.info(LogMarker.config, "- retryCnt: {}", this.zookeeperProp.retryCnt);
        log.info(LogMarker.config, "- retryInterval: {}", this.zookeeperProp.retryInterval);
        log.info(LogMarker.config, "- lockWaitTime: {}", this.zookeeperProp.lockWaitTime);
        log.info(LogMarker.config, "- serverList: {}", this.zookeeperProp.serverList);
        log.info(LogMarker.config, "- clientState: {}", this.client.getState());
    }
}
