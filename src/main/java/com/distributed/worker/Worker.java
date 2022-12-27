package com.distributed.worker;

import com.distributed.domain.Constants;
import com.distributed.domain.Parameters;
import com.distributed.util.CacheUtil;
//import com.distributed.worker.sort.MergeSort;
import com.distributed.worker.init.MemorySort;
import org.apache.zookeeper.CreateMode;

import java.io.IOException;

public class Worker extends Thread{

    // zookeeper对象
    private final Coordinator coordinator;

    public Worker(Coordinator coordinator) {
        this.coordinator = coordinator;

    }

    // 启动worker节点
    private void openWorker() {
        // 连接zookeeper
        coordinator.openCoordinator(Parameters.Zookeeper.connectString);
        // 创建worker临时节点
        if (this.coordinator.createNode(CreateMode.EPHEMERAL, Parameters.Zookeeper.workerPath, Constants.WorkerStatus.INIT)) {
            System.out.println("创建临时节点成功");
            CacheUtil.workerState = Constants.WorkerStatus.INIT;
        }
        else {
            throw new RuntimeException("创建节点失败，该节点已在zookeeper存在");
        }

    }

    private void closeWorker() {
        this.coordinator.closeCoordinator();
    }

//    private void localMergeSort() throws IOException {
//        MergeSort localMergeSort = new MergeSort();
//        localMergeSort.memorySort();
//        localMergeSort.mergeSort();
//    }

    private void localSort() throws IOException {
        MemorySort localMemorySort = new MemorySort();
        localMemorySort.memorySort();
    }


    public void run() {
        // worker启动
        openWorker();

        // 初始化
        // 初始化数据较少，用内存排序
        try {
//            localMergeSort();
            localSort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        System.out.println("本地排序完成");
        // 状态修改为已经排序
        this.coordinator.setNode(Parameters.Zookeeper.workerPath, Constants.WorkerStatus.HAS_SORT);


//        try {
//            Thread.sleep(Long.MAX_VALUE);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        // worker关闭
//        closeWorker();
    }

}
