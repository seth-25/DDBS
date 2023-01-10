package com.distributed.worker;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Coordinator {
    private CuratorFramework coordinator;
    private InterProcessMutex lock;

    public CuratorFramework getCoordinator() {
        return this.coordinator;
    }

    public void openCoordinator(String connectString) {

        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(3000, 3);
        CuratorFramework coordinator = CuratorFrameworkFactory.builder().connectString(connectString).connectionTimeoutMs(2000).sessionTimeoutMs(2000).retryPolicy(policy).build();

        // 启动Coordinator
        coordinator.start();
        if (coordinator.getState().equals(CuratorFrameworkState.STARTED))
            System.out.println("连接zookeeper成功");

        this.coordinator = coordinator;
    }

    public void closeCoordinator() {
        this.coordinator.close();
    }

    // 获取锁
    public void getLock(String path) {
        this.lock = new InterProcessMutex(this.coordinator, path);
        try {
            System.out.println("开始获取锁");
            lock.acquire();
            System.out.println("获取锁成功");
        } catch (Exception e) {
            throw new RuntimeException("获取锁失败\n" + e);
        }
    }

    // 释放锁
    public void releaseLock() {
        try {
            this.lock.release();
        } catch (Exception e) {
            throw new RuntimeException("释放锁失败\n" + e);
        }
    }

    // 创建节点
    public boolean createNode(CreateMode createMode, String path, String content) {
        try {
            if (coordinator.checkExists().forPath(path) == null) { // 不存在该节点才创建
                try {
                    this.coordinator.create().withMode(createMode).forPath(path, content.getBytes());
                    return true;
                } catch (Exception e) {
                    throw new RuntimeException("创建节点失败\n" + e);
                }
            }
            else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("判断是否存在节点失败\n" + e);
        }
    }

    // 修改节点
    public void setNode(String path, String content) {
        try {
            this.coordinator.setData().withVersion(-1).forPath(path, content.getBytes());
        } catch (Exception e) {
            System.out.println("修改节点失败");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
