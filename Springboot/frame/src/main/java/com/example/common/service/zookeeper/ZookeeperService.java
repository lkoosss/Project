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
	private List<String> serverArrayList = new ArrayList<>(Arrays.asList("192.168.5.151:2181", "192.168.5.152:2181", "192.168.5.153:2181"));

	private CuratorFramework client;
	
	// 분산 Lock을 위한 객체
	private int waitTimeForAcquireLock = 5;

	// znode Watcher를 위한 변수
	HashMap<String, TreeCache> treeCacheHashMap;


	public ZookeeperService() {
		Collections.shuffle(serverArrayList);
		String serverList = serverArrayList.toString().replace("[", "").replace("]", "").replace(" ", "");
		
		this.client = CuratorFrameworkFactory.newClient(serverList, retryPolicy);
		this.client.start();
		
		//CloseableUtils.closeQuietly(client);
		serverList = null;
		treeCacheHashMap = new HashMap<String, TreeCache>();
	}
	
	///// ZnodeValue 조회 /////
	public String selectZnode(String znodeKey) {
		String result = "";
		znodeKey = this.repairZnodeKey(znodeKey);
		try {
			if (client.checkExists().forPath(znodeKey) != null) {
				result = new String(this.client.getData().forPath(znodeKey));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			znodeKey = null;
		}
		
		return result;
	}
	
	///// Znode 등록 /////
	public boolean createZnode(CreateMode znodeType, String znodeKey, String znodeValue) {
		boolean result = false;
		znodeKey = this.repairZnodeKey(znodeKey);
		
		try {
			if (this.client.checkExists().forPath(znodeKey) == null) {
				this.client.create().creatingParentsIfNeeded().withMode(znodeType).forPath(znodeKey, znodeValue.getBytes());
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			znodeType 	= null;
			znodeKey 	= null;
			znodeValue	= null;
		}
		
		return result;
	}

	///// Znode 수정 /////
	public boolean updateZnode(String znodeKey, String znodeValue) {
		boolean result = false;
		znodeKey = this.repairZnodeKey(znodeKey);

		// 정상 서비스용
		InterProcessMutex lock = new InterProcessMutex(this.client, znodeKey);

		try {
			if (lock.acquire(this.waitTimeForAcquireLock, TimeUnit.SECONDS) && this.client.checkExists().forPath(znodeKey) != null) {
				this.client.setData().forPath(znodeKey, znodeValue.getBytes());
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
			znodeKey 	= null;
			znodeValue 	= null;
			lock 		= null;
		}


		//테스트용
//		try {
//			if (this.client.checkExists().forPath(znodeKey) != null) {
//				this.client.setData().forPath(znodeKey, znodeValue.getBytes());
//				result = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			result = false;
//		} finally {
//			try {
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			znodeKey 	= null;
//			znodeValue 	= null;
//		}
//
		return result;
	}
	
	///// Znode 삭제 /////
	public boolean deleteZnode(String znodeKey) {
		boolean result = false;
		znodeKey = this.repairZnodeKey(znodeKey);
		String parentZnodeKey = null;
		try {
			if (this.client.checkExists().forPath(znodeKey) != null) {
				this.client.delete().deletingChildrenIfNeeded().forPath(znodeKey);

				// 부모 Znode가 자식 Znode를 가지고 있지 않으면 Depth를 올라가며 연쇄적으로 삭제한다.
				while (znodeKey.lastIndexOf("/") != 0) {							// 최상위 /가 아닐때까지 반복
					znodeKey = znodeKey.substring(0,znodeKey.lastIndexOf("/"));		// "/1/2/3" -> "/1/2" 가됨
					if (this.client.getChildren().forPath(znodeKey).size() == 0) {		// 부모 Znode에 속한 자식 Znode가 없으면 삭제
						this.client.delete().deletingChildrenIfNeeded().forPath(znodeKey);
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
			znodeKey = null;
		}
		
		return result;
	}
	
	///// Znode State 조회 /////
	public String selectZnodeState(String znodeKey) {
		znodeKey = this.repairZnodeKey(znodeKey);
		String result = "";
		try {
			if (this.client.checkExists().forPath(znodeKey) != null) {
				Stat stat = new Stat();
				String receiveZnodeValue = new String(this.client.getData().storingStatIn(stat).forPath(znodeKey));
				Date ctime = new Date(stat.getCtime());
				Date mtime = new Date(stat.getMtime());
				result = "znodeKey : " + znodeKey + ", ";
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
			znodeKey = null;
		}
		
		return result;
	}
	
	///// Znode에 속한 하위 znodeKey들을 조회 /////
	public List<String> selectZnodeChildren(String znodeKey) {
		znodeKey = this.repairZnodeKey(znodeKey);
		List<String> result = null;
		try {
			if (this.client.checkExists().forPath(znodeKey) != null) {
				result = this.client.getChildren().forPath(znodeKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			znodeKey = null;
		}
		
		return result;
	}

	// Znode에 Watcher 설정
	public void setWatcher(String znodeKey) {
		String inputZnodeKey = this.repairZnodeKey(znodeKey);
		TreeCache treeCache = new TreeCache(this.client, inputZnodeKey);
		treeCacheHashMap.put(inputZnodeKey, treeCache);

		try {
			treeCache.start();
			addListener(treeCache);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Znode에 Watcher 리스너 등록
	public void addListener(TreeCache treeCache) {

		TreeCacheListener listener = new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				String targetZnodeKey = event.getData().getPath();
				switch (event.getType()) {
					case NODE_ADDED: {
						System.out.println("Node added : " + targetZnodeKey);
						if (targetZnodeKey.contains("command_1")) {
							System.out.println("do command 1");
						} else if (targetZnodeKey.contains("command_2")) {
							System.out.println("do command 2");
						} else if (targetZnodeKey.contains("command_3")) {
							System.out.println("do command 3");
						}
						System.out.println("");
					} break;
					case NODE_UPDATED: {
						System.out.println("Node updated : " + targetZnodeKey);
						System.out.println("");
					} break;
					case NODE_REMOVED: {
						System.out.println("Node removed : " + targetZnodeKey);
						System.out.println("");
					} break;
				}
			}
		};
		treeCache.getListenable().addListener(listener);
	}

	// Znode Watcher 등록해제, 리스너 등록해제
	public void unsetWatcher(String znodeKey) {
		String inputZnodeKey = this.repairZnodeKey(znodeKey);
		treeCacheHashMap.get(inputZnodeKey).close();
	}
	
	// Znode Key 값 체크 및 수정
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
