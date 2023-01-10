package com.distributed.master;

import common.setting.Constants;
import com.distributed.domain.Parameters;
import com.distributed.master.init.InitAction;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class Master implements Runnable {

    // zookeeper对象
    private Coordinator coordinator;
    public Master() {

    }


    // 启动master节点
    private void openMaster() {
        this.coordinator = new Coordinator();

        // 连接zookeeper
        this.coordinator.openCoordinator(Parameters.Zookeeper.connectString);

        // 获取master锁，确保只有一台master
        this.coordinator.getLock("/master_lock");

        // 创建workers节点用于存储worker
        this.coordinator.createNode(CreateMode.PERSISTENT, "/workers", "");

        //
    }

    // 关闭master节点
    private void closeMaster() {
        this.coordinator.releaseLock();
        this.coordinator.closeCoordinator();
    }

    // 监听worker
    private void watchWorkers() throws Exception {
        // 子节点缓存，用于监听/workers下的子节点。cacheData为true，接收到节点列表变更事件的同时，会将获得节点内容
        PathChildrenCache childrenCache = new PathChildrenCache(coordinator.getCoordinator(), "/workers", true);
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE); // 同步初始化cache
//        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        List<ChildData> childDataList = childrenCache.getCurrentData();
        System.out.println("当前worker列表:");
        for(ChildData childData : childDataList){
            String childInfo = childData.getPath() + ": " + new String(childData.getData());
            System.out.println(childInfo);
            InitAction.addWorker(childData);
        }

        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                ChildData childData = event.getData();
                String eventPath = childData.getPath();
                byte[] eventData = childData.getData();
                if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    System.out.println("添加worker路径: " + eventPath);
                    System.out.println("worker数据: "+new String(eventData));
                    InitAction.addWorker(childData);
                }
                else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    System.out.println("删除worker: "+eventPath);
                }
                else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("修改worker: "+eventPath);
                    System.out.println("修改worker数据: "+new String(eventData));
                    List<ChildData> childDataList;
                    switch (new String(eventData)) {
                        case Constants.WorkerStatus.HAS_SORT:
                            childDataList = childrenCache.getCurrentData();
                            System.out.println(eventPath + " 已经排序");
                            InitAction.checkSort(childDataList);
                            break;
                        case Constants.WorkerStatus.HAS_SENT_SAX_STATISTIC:
                            childDataList = childrenCache.getCurrentData();
                            System.out.println(eventPath + " 已经将sax值的统计发给master");
                            InitAction.checkSendSaxStatistic(childDataList);
                            break;
//                        case Constants.WorkerStatus.CHANGE_VERSION:
//                            VersionAction.sendVersion(childData);
//                            VersionAction.checkWorkerVersion();
//                            break;
                    }

                }
//                else if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
//                    System.out.println("worker初始化成功");
//                }
            }
        });

        Thread.sleep(Long.MAX_VALUE);
    }

    public void run() {
        // master启动
        openMaster();

        // master运行
        System.out.println("开始监听workers");
        try {
            watchWorkers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // master关闭
        closeMaster();
    }


}
