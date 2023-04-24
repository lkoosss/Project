package com.example.common.manager;

import com.example.common.util.DateUtil;
import com.example.common.util.StringUtil;
import com.example.common.value.Constant;
import com.example.common.value.Constant.TimeFormat;
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
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** Zookeeper Management **/
@Slf4j
public class ZookeeperManager {
    /*-------------------
    |   ServiceProp 부분  |
     -------------------*/
    private ZookeeperProp zookeeperProp;
    /*------------------
    |   클래스 귀속 변수   |
     ------------------*/
    private CuratorFramework client;                                // Zookeeper client
    HashMap<String, TreeCache> treeCacheHashMap;                    // Znode Watcher Repository
    private ConnectionStateListener connectionEvent;                // Connection Event
    private CuratorListener curatorEvent;                           // Curator Event
    private List<Consumer<ConnectionState>> connectionEventList;    // Connection Event Repository


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
            // Zookeeper Client 시작
            this.client.start();
            // Zookeeper Connection Event 등록
            this.client.getConnectionStateListenable().addListener(this.connectionEvent);
            // Zookeeper Curator Event 등록
            this.client.getCuratorListenable().addListener(this.curatorEvent);
        }
        log.info(LogMarker.config, "- retryCnt: {}",        this.zookeeperProp.retryCnt);
        log.info(LogMarker.config, "- retryInterval: {}",   this.zookeeperProp.retryInterval);
        log.info(LogMarker.config, "- lockWaitTime: {}",    this.zookeeperProp.lockWaitTime);
        log.info(LogMarker.config, "- serverList: {}",      this.zookeeperProp.serverList);
        log.info(LogMarker.config, "- clientState: {}",     this.client.getState());
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
     *  - Znode 생성
     *    > 이미 등록된 znodeKey인 경우 등록 안함 (false 반환)
     * </pre>
     *
     * @param znodeType
     * @param znodeKey
     * @param znodeValue
     * @return
     */
    public boolean createZnode(CreateMode znodeType, String znodeKey, String znodeValue) {
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
            // Znode 존재여부 확인
            if (this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey already exists");
                return false;
            }

            /** 정상인 경우 **/
            // Znode 생성
            if (StringUtil.isNotEmpty(znodeValue)) {
                this.client.create().creatingParentsIfNeeded().withMode(znodeType).forPath(repairZnodeKey, znodeValue.getBytes());
            } else {
                this.client.create().creatingParentsIfNeeded().withMode(znodeType).forPath(repairZnodeKey);
            }

        } catch (Exception e) {
            log.error(LogMarker.zookeeper, "Exception: {}", e.getMessage(), e);
        }

        return true;
    }

    /**
     * <pre>
     *  readZnode
     *  - Znode 조회
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    public String readZnode(String znodeKey) {
        String result = null;
        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return "";
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return "";
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return "";
            }

            /** 정상인 경우 **/
            // Znode 조회
            result = new String(this.client.getData().forPath(repairZnodeKey));
        } catch (Exception e) {
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * <pre>
     *  updateZnode
     *  - Znode 수정
     * </pre>
     *
     * @param znodeKey
     * @param znodeValue
     * @param isLockUsed
     * @return
     */
    public boolean updateZnode(String znodeKey, String znodeValue, boolean isLockUsed) {
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
            // ZnodeValue 확인
            if (znodeValue == null) {
                log.warn(LogMarker.zookeeper, "ZnodeValue is null");
                return false;
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return false;
            }

            /** 정상인 경우 **/
            // Znode 수정
            switch (isLockUsed ? "true" : "false") {
                case "true" :
                    InterProcessMutex lock = new InterProcessMutex(this.client, repairZnodeKey);
                    boolean lockResult = lock.acquire(this.zookeeperProp.lockWaitTime, TimeUnit.SECONDS);

                    if (lockResult) {
                        this.client.setData().forPath(repairZnodeKey, znodeValue.getBytes());
                        return true;
                    }

                    lock.release();
                    break;
                case "false" :
                    this.client.setData().forPath(repairZnodeKey, znodeValue.getBytes());
                    return true;
            }

        } catch (Exception e) {
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        }

        return true;
    }

    /**
     * <pre>
     *  deleteZnode
     *  - Znode 삭제
     * </pre>
     *
     * @param znodeKey
     */
    public void deleteZnode(String znodeKey) {
        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return;
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return;
            }

            /** 정상인 경우 **/
            // Znode 삭제
            this.client.delete().deletingChildrenIfNeeded().forPath(repairZnodeKey);
        } catch (Exception e) {
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        }
    }

    /**
     * <pre>
     *  readZnodeParent
     *  - 부모 Znode 조회
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    public String readZnodeParent(String znodeKey) {
        /** 유효성 체크 **/
        // Zookeeper 사용여부 체크
        if (!this.zookeeperProp.serviceEnable) {
            return "";
        }

        String repairZnodeKey = this.repairZnodeKey(znodeKey);
        // ZnodeKey 확인
        if (StringUtil.isBlank(repairZnodeKey)) {
            log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
            return "";
        }

        /** 정상인 경우 **/
        int index = repairZnodeKey.lastIndexOf("/");
        return index > 0 ? repairZnodeKey.substring(0, index) : repairZnodeKey;
    }

    /**
     * <pre>
     *  getZnodeChildrenCnt
     *  - 하위 자식 Znode 개수 확인
     * </pre>
     * @param znodeKey
     * @return
     */
    public int getZnodeChildrenCnt(String znodeKey) {
        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return 0;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return 0;
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return 0;
            }

            /** 정상인 경우 **/
            // Znode 조회
            Stat stat = this.client.checkExists().forPath(repairZnodeKey);
            return stat == null ? 0 : stat.getNumChildren();
        } catch (Exception e) {
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        }
    }

    /**
     * <pre>
     *  getZnodeChildrenList
     *  - 하위 자식 Znode List 조회
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    public List<String> getZnodeChildrenList(String znodeKey) {
        List<String> result = null;

        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return Collections.emptyList();
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return Collections.emptyList();
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return Collections.emptyList();
            }

            /** 정상인 경우 **/
            // Znode Children 조회
            result = this.client.getChildren().forPath(repairZnodeKey);
            Collections.sort(result);
        } catch (Exception e) {
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        }

        return result == null ? Collections.emptyList() : result;
    }

    /**
     * <pre>
     *  getZnodeState
     *  - Znode 상태값 조회
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    public Map<String, String> getZnodeState(String znodeKey) {
        Map<String, String> result = new HashMap<>();

        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return Collections.emptyMap();
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return Collections.emptyMap();
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return Collections.emptyMap();
            }

            /** 정상인 경우 **/
            Stat stat = new Stat();
            String znodeValue = new String(this.client.getData().storingStatIn(stat).forPath(repairZnodeKey));

            result.put("znodeKey", repairZnodeKey);
            result.put("znodeValue", znodeValue);
            result.put("cZxid", String.valueOf(stat.getCzxid()));
            result.put("ctime", DateUtil.getDate(stat.getCtime(), TimeFormat.FORMAT_06));
            result.put("mZxid", String.valueOf(stat.getMzxid()));
            result.put("mtime", DateUtil.getDate(stat.getMtime(), TimeFormat.FORMAT_06));
            result.put("pZxid", String.valueOf(stat.getPzxid()));
            result.put("cVersion", String.valueOf(stat.getCversion()));
            result.put("aclVersion", String.valueOf(stat.getAversion()));
            result.put("ephemeralOwner", String.valueOf(stat.getEphemeralOwner()));
            result.put("dataLength", String.valueOf(stat.getDataLength()));
            result.put("numChildren", String.valueOf(stat.getNumChildren()));
        } catch (Exception e) {
            log.error(LogMarker.zookeeper, "Exception: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * <pre>
     *  getZnodeCreateTime
     *  - Znode 생성시간 조회 (ms)
     * </pre>
     *
     * @param znodeKey
     * @return
     */
    public long getZnodeCreateTime(String znodeKey) {
        long result = 0L;

        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return 0L;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return 0L;
            }
            // Znode 존재여부 체크
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return 0L;
            }

            /** 정상인 경우 **/
            Stat stat = new Stat();
            this.client.getData().storingStatIn(stat).forPath(repairZnodeKey);
            result = stat.getCtime();
        } catch (Exception e) {
            log.error(LogMarker.zookeeper, "Exception: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * <pre>
     *  addWatcher
     *  - Znode에 Watcher Event 등록
     * </pre>
     *
     * @param znodeKey
     * @param listener
     */
    public void addWatcher(String znodeKey, TreeCacheListener listener) {
        try {
            /** 유효성 체크 **/
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
            if (StringUtil.isBlank(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
                return;
            }
            // Znode 존재여부 확인
            if (!this.existsZnode(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "ZnodeKey does not exist");
                return;
            }
            // Listener 유효성 체크
            else if (listener == null) {
                log.warn(LogMarker.zookeeper, "Listener is null");
                return;
            }
            // 중복등록 확인
            if (this.treeCacheHashMap.containsKey(repairZnodeKey)) {
                log.warn(LogMarker.zookeeper, "This znode already has a watcher registered: znodeKey={}", repairZnodeKey);
                return;
            }

            /** 정상인 경우 **/
            TreeCache treeCache = new TreeCache(this.client, repairZnodeKey);
            treeCache.getListenable().addListener(listener);
            treeCache.start();

            // Watcher Map 등록
            this.treeCacheHashMap.put(repairZnodeKey, treeCache);
        } catch (Exception e) {
            throw new RuntimeException("addWatcher Exception: " + e.getMessage(), e);
        }
    }

    /**
     * <pre>
     *  delWatcher
     *  - 주어진 znodeKey에 Watcher 삭제
     * </pre>
     *
     * @param znodeKey
     */
    public void delWatcher(String znodeKey) {
        /** 유효성 체크 **/
        // Zookeeper 사용여부 체크
        if (!this.zookeeperProp.serviceEnable) {
            return;
        }
        String repairZnodeKey = this.repairZnodeKey(znodeKey);
        // ZnodeKey 확인
        if (StringUtil.isBlank(repairZnodeKey)) {
            log.warn(LogMarker.zookeeper, "ZnodeKey is empty");
            return;
        }
        // Watcher 등록여부 확인
        if (!this.treeCacheHashMap.containsKey(repairZnodeKey)) {
            log.warn(LogMarker.zookeeper, "There are no watcher registered for this znode");
            return;
        }

        /** 정상인 경우 **/
        this.treeCacheHashMap.remove(repairZnodeKey).close();
    }

    /**
     * <pre>
     *  getWatcherCount
     *  - 등록된 Watcher 전체 개수
     * </pre>
     *
     * @return
     */
    public int getWatcherCount() {
        return this.treeCacheHashMap.size();
    }

    /**
     * <pre>
     *  getWatcherKeyList
     *  - 등록된 Watcher 전체 key 목록
     * </pre>
     *
     * @return
     */
    public List<String> getWatcherKeyList() {
        return new ArrayList<>(this.treeCacheHashMap.keySet());
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
            // Zookeeper 사용여부 체크
            if (!this.zookeeperProp.serviceEnable) {
                return false;
            }

            String repairZnodeKey = this.repairZnodeKey(znodeKey);
            // ZnodeKey 확인
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
