package com.example.common.service.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ZookeeperService {
	// 연결 시도 Rule
	private int sleepMsBetweenRetries = 100;
	private int maxRetries = 3;
	private RetryPolicy retryPolicy = new RetryNTimes(maxRetries, sleepMsBetweenRetries);

	// 연결 서버 선택 관련 변수
	private List<String> serverArrayList = new ArrayList<>(Arrays.asList("192.168.0.109:2181", "192.168.0.110:2181", "192.168.0.111:2181"));

	private CuratorFramework client;

	// 분산 Lock을 위한 객체
	private int waitTimeForAcquireLock = 5;

	// znode Watcher를 위한 변수
	HashMap<String, TreeCache> treeCacheHashMap;


	/**
	 * Instantiates a new Zookeeper service.
	 */
	public ZookeeperService() {
		Collections.shuffle(serverArrayList);
		String serverList = serverArrayList.toString().replace("[", "").replace("]", "").replace(" ", "");

		this.client = CuratorFrameworkFactory.newClient(serverList, retryPolicy);
		this.client.start();

		serverList = null;
		treeCacheHashMap = new HashMap<String, TreeCache>();
	}

	/**
	 * <pre>
	 * selectZnode
	 * ZnodeValue 조회
	 * </pre>
	 *
	 * @param znodeKey
	 * @return the string
	 */
	public String selectZnode(String znodeKey) {
		String result = "";
		String repairZnodeKey = this.repairZnodeKey(znodeKey);
		try {
			if (client.checkExists().forPath(repairZnodeKey) != null) {
				result = new String(this.client.getData().forPath(repairZnodeKey));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			znodeKey		= null;
			repairZnodeKey 	= null;
		}

		return result;
	}

	/**
	 * <pre>
	 * createZnode
	 * Znode 등록
	 * </pre>
	 *
	 * @param znodeType
	 * @param znodeKey
	 * @param znodeValue
	 * @return the boolean
	 */
	public boolean createZnode(CreateMode znodeType, String znodeKey, String znodeValue) {
		boolean result = false;
		String repairZnodeKey = this.repairZnodeKey(znodeKey);

		try {
			if (this.client.checkExists().forPath(repairZnodeKey) == null) {
				this.client.create().creatingParentsIfNeeded().withMode(znodeType).forPath(repairZnodeKey, znodeValue.getBytes());
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			znodeType 		= null;
			znodeKey		= null;
			repairZnodeKey 	= null;
			znodeValue		= null;
		}

		return result;
	}

	/**
	 * <pre>
	 * updateZnode
	 * Znode 수정
	 * </pre>
	 *
	 * @param znodeKey
	 * @param znodeValue
	 * @return the boolean
	 */
	public boolean updateZnode(String znodeKey, String znodeValue) {
		boolean result = false;
		String repairZnodeKey = this.repairZnodeKey(znodeKey);

		// 정상 서비스용
		InterProcessMutex lock = new InterProcessMutex(this.client, repairZnodeKey);

		try {
			if (lock.acquire(this.waitTimeForAcquireLock, TimeUnit.SECONDS) && this.client.checkExists().forPath(repairZnodeKey) != null) {
				this.client.setData().forPath(repairZnodeKey, znodeValue.getBytes());
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			try {
				lock.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
			znodeKey		= null;
			repairZnodeKey 	= null;
			znodeValue 		= null;
			lock 			= null;
		}

		return result;
	}

	/**
	 * <pre>
	 * deleteZnode
	 * Znode 삭제
	 * </pre>
	 *
	 * @param znodeKey
	 * @return the boolean
	 */
	public boolean deleteZnode(String znodeKey) {
		boolean result = false;
		String repairZnodeKey = this.repairZnodeKey(znodeKey);

		try {
			if (this.client.checkExists().forPath(repairZnodeKey) != null) {
				this.client.delete().deletingChildrenIfNeeded().forPath(repairZnodeKey);

				// 부모 Znode가 자식 Znode를 가지고 있지 않으면 Depth를 올라가며 연쇄적으로 삭제한다.
				while (repairZnodeKey.lastIndexOf("/") > 0) {											// 최상위 /가 아닐때까지 반복
					repairZnodeKey = repairZnodeKey.substring(0,repairZnodeKey.lastIndexOf("/"));		// "/1/2/3" -> "/1/2" 가됨
					if (this.client.getChildren().forPath(repairZnodeKey).size() == 0) {					// 부모 Znode에 속한 자식 Znode가 없으면 삭제
						this.client.delete().deletingChildrenIfNeeded().forPath(repairZnodeKey);
					} else {	// 부모 Znode에 속한 자식 Znode가 있으면 연쇄삭제 멈춤
						break;
					}
				}

				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			znodeKey		= null;
			repairZnodeKey 	= null;
		}

		return result;
	}

	/**
	 * <pre>
	 * selectZnodeState
	 * Znode State 조회
	 * </pre>
	 *
	 * @param znodeKey
	 * @return the string
	 */
	public String selectZnodeState(String znodeKey) {
		String repairZnodeKey = this.repairZnodeKey(znodeKey);
		String result = "";
		try {
			if (this.client.checkExists().forPath(repairZnodeKey) != null) {
				Stat stat = new Stat();
				String receiveZnodeValue = new String(this.client.getData().storingStatIn(stat).forPath(repairZnodeKey));
				Date ctime = new Date(stat.getCtime());
				Date mtime = new Date(stat.getMtime());
				result = "znodeKey : " + repairZnodeKey + ", ";
				result = result + "znodeValue : " + receiveZnodeValue + ", ";
				result = result + "cZxid : " + stat.getCzxid() + ", ";
				result = result + "ctime : " + ctime.toString() + ", ";
				result = result + "mZxid : " + stat.getMzxid() + ", ";
				result = result + "mtime : " + mtime.toString() + ", ";
				result = result + "pZxid : " + stat.getPzxid() + ", ";
				result = result + "cVersion : " + stat.getCversion() + ", ";
				result = result + "aclVersion : " + stat.getAversion() + ", ";
				result = result + "ephemeralOwner : " + stat.getEphemeralOwner() + ", ";
				result = result + "dataLength : " + stat.getDataLength() + ", ";
				result = result + "numChildren : " + stat.getNumChildren();

				receiveZnodeValue = null;
				stat = null;
				ctime = null;
				mtime = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			znodeKey		= null;
			repairZnodeKey 	= null;
		}

		return result;
	}

	/**
	 * <pre>
	 * selectZnodeChildren
	 * Znode에 속한 하위 znodeKey들을 조회
	 * </pre>
	 *
	 * @param znodeKey
	 * @return the list
	 */
	public List<String> selectZnodeChildren(String znodeKey) {
		String repairZnodeKey = this.repairZnodeKey(znodeKey);
		List<String> result = null;
		try {
			if (this.client.checkExists().forPath(repairZnodeKey) != null) {
				result = this.client.getChildren().forPath(repairZnodeKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			znodeKey		= null;
			repairZnodeKey 	= null;
		}

		return result;
	}

	/**
	 * <pre>
	 * setWatcher
	 * Znode에 Watcher 설정
	 * </pre>
	 *
	 * @param znodeKey
	 */
	public void setWatcher(String znodeKey) {
		String repairZnodeKey = this.repairZnodeKey(znodeKey);
		TreeCache treeCache = null;

		// Watcher 중복검사
		if (this.treeCacheHashMap.containsKey(repairZnodeKey) == false) {		// 중복된 Watcher가 없을 때
			treeCache = new TreeCache(this.client, repairZnodeKey);
			this.treeCacheHashMap.put(repairZnodeKey, treeCache);

			try {
				// repairZnodeKey 에 해당하는 treeCache를 생성
				treeCache.start();
				// 리스너를 생성하고 실행할 코드를 정의
				TreeCacheListener listener = new TreeCacheListener() {
					boolean isInit = false;
					@Override
					public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
						// 리스너가 시작 될 때
						if (event.getType() == TreeCacheEvent.Type.INITIALIZED) {
							isInit = true;
						}

						// 리스너가 시작이 된 후
						if (isInit == true) {
							// 이벤트 타입별로 구분될 수 있게한다.
							switch (event.getType()) {
								case NODE_ADDED: {		// 새로운 Znode가 생성 될 때
									System.out.println("\n" + "Node added : " + event.toString());
									if (event.getData().getPath().contains("command_1")) {
										System.out.println("do command 1");
									} else if (event.getData().getPath().contains("command_2")) {
										System.out.println("do command 2");
									} else if (event.getData().getPath().contains("command_3")) {
										System.out.println("do command 3");
									}
								} break;
								case NODE_UPDATED: {	// Znode가 변경 될 때
									System.out.println("\n" + "Node updated : " + event.toString());
								} break;
								case NODE_REMOVED: {	// Znode가 삭제 될 때
									System.out.println("\n" + "Node removed : " + event.toString());
								} break;
							}
						}
					}
				};
				// 생성한 리스너를 등록하고 실행함
				treeCache.getListenable().addListener(listener);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} else {		// 중복된 Watcher가 존재할 때
			System.out.println("Watcher already registered in Znode, " + repairZnodeKey);
		}

		// 리소스 반환 처리
		repairZnodeKey	= null;
		znodeKey		= null;
		treeCache		= null;
	}

	/**
	 * <pre>
	 * unsetWatcher
	 * Znode Watcher 해제, 리스너 해제
	 * </pre>
	 *
	 * @param znodeKey
	 */
	public void unsetWatcher(String znodeKey) {
		String repairZnodeKey = this.repairZnodeKey(znodeKey);

		if (treeCacheHashMap.containsKey(repairZnodeKey) == true) {
			treeCacheHashMap.get(repairZnodeKey).close();
			treeCacheHashMap.remove(repairZnodeKey);
		} else {
			System.out.println(repairZnodeKey + " Watcher not exist");
		}

		// 리소스 반환처리
		repairZnodeKey	= null;
		znodeKey		= null;
	}

	/**
	 * <pre>
	 * repairZnodeKey
	 * Znode Key 값 체크 및 수정
	 * </pre>
	 *
	 * @param znodeKey
	 * @return the string
	 */
	private String repairZnodeKey(String znodeKey) {
		znodeKey = znodeKey.trim();

		if (znodeKey.charAt(0) != '/') {
			znodeKey = "/" + znodeKey;
		}

		if (znodeKey.charAt(znodeKey.length() - 1) == '/') {
			znodeKey = znodeKey.substring(0, znodeKey.length() - 1);
		}

		return znodeKey;
	}

}
