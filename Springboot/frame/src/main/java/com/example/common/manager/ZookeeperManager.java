package com.example.common.manager;

import com.example.common.util.StringUtil;
import com.example.common.value.ServiceProp;
import com.example.common.value.ServiceProp.ZookeeperProp;
import com.example.common.value.SystemProp;
import com.example.common.value.Constant.LogMarker;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateModable;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    /**
     * <pre>
     *  destroy
     * </pre>
     */
    public void destroy() {
        log.info(LogMarker.zookeeper, "Destroy start");

        // 서버 리스트 제거
        if (this.zookeeperProp.serverList != null) {
            this.zookeeperProp.serverList.clear();
            this.zookeeperProp.serverList = null;
        }

        // Curator Event 제거
        if (this.client != null && this.curatorEvent != null) {
            this.client.getCuratorListenable().removeListener(this.curatorEvent);
            this.curatorEvent = null;
        }

        // Connection Event 제거
        if (this.client != null && this.connectionEvent != null) {
            this.client.getConnectionStateListenable().removeListener(this.connectionEvent);
            this.connectionEvent = null;
        }

        // 등록된 Watcher 제거
        if (this.treeCacheHashMap != null) {
            log.debug(LogMarker.zookeeper, "Watcher List: {}", this.getWatcherKeyList());
            this.treeCacheHashMap.forEach((znodeKey, treeCache) -> {
                log.debug(LogMarker.zookeeper, "Watcher close start: {}", znodeKey);
                treeCache.close();
                log.debug(LogMarker.zookeeper, "Watcher close end: {}", znodeKey);
            });
            this.treeCacheHashMap.clear();
            this.treeCacheHashMap = null;
        }

        // Connection Event 제거
        if (this.connectionEventList != null) {
            this.connectionEventList.clear();
            this.connectionEventList = null;
        }

        if (this.client != null) {
            CloseableUtils.closeQuietly(this.client);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error(LogMarker.zookeeper, "Zookeeper client destroy Exception: {}", e.getMessage(), e);
            }
            this.client = null;
        }

        log.info(LogMarker.zookeeper, "Destroy end");
    }

    /**
     * <pre>
     *  addConnectionEvent
     * </pre>
     *
     * @param event
     */
    public void addConnectionEvent(@NonNull Consumer<ConnectionState> event) {
        this.connectionEventList.add(event);
    }

    /**
     * <pre>
     *  createZnode
     *  - zookeeper에 znodeKey 등록
     *    > 이미 등록된 znodeKey인 경우 등록 안함 (false 반환)
     * </pre>
     *
     * @param znodeType
     * @param znodeKey
     * @param znodeValue
     * @return
     */
    public boolean createZnode(CreateMode znodeType, String znodeKey, String znodeValue) {
        boolean result = false;

        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return false;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return false;
            }
            // ZnodeKey 존재여부 확인
            if (this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey already exists");
                return false;
            }

            /** 정상인 경우 **/
            if (StringUtil.isNotEmpty(znodeValue)) {
                this.client.create().creatingParentsIfNeeded().withMode(znodeType).forPath(repairZnodeKey, znodeValue.getBytes());
            } else {
                this.client.create().creatingParentsIfNeeded().withMode(znodeType).forPath(repairZnodeKey);
            }

            result = true;

        } catch (Exception e) {
            log.error(LogMarker.zookeeper, "Exception: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * <pre>
     *  existsZnode
     *  - Znode 존재여부 확인
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    public boolean existsZnode(String znodeKey) {
        boolean result = false;

        try {
            /** 유효성 체크 **/
            if (!this.zookeeperProp.serviceEnable) {
                return false;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);

            if (StringUtil.isBlank(repairZnodeKey)) {
                return false;
            }

            /** 정상인 경우 **/
            result = this.client.checkExists().forPath(repairZnodeKey) != null;
        } catch (Exception e) {
            log.debug(LogMarker.zookeeper, "Exception: {}", e.getMessage());
        }

        return result;
    }

    /**
     * <pre>
     *  repairZnodeKey
     *  - znodeKey 값 체크 및 수정
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    private String repairZnodeKey(String znodeKey) {
        if (znodeKey == null || "".equals(znodeKey.trim())) {
            return "";
        }

        String repairZnodeKey = znodeKey.trim();
        if (repairZnodeKey.charAt(0) != '/') {
            repairZnodeKey = "/" + repairZnodeKey;
        }

        if (repairZnodeKey.charAt(repairZnodeKey.length() - 1) == '/') {
            repairZnodeKey = repairZnodeKey.substring(0, repairZnodeKey.length() -1);
        }

        return repairZnodeKey;
    }
}
