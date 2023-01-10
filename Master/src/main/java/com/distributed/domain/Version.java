package com.distributed.domain;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.gson.Gson;
import javafx.util.Pair;

import java.util.HashMap;

public class Version {
    private HashMap<String, Pair<Integer, Integer>> workerVersions; // key是hostName，value是内存和外存版本
    private int Ref = 0;
    private RTree<String, Rectangle> rTree;

    public void updateVersion(String hostName, Pair<Integer, Integer> version) {
        workerVersions.put(hostName, version);
    }

    public void updateVersion(String hostName, Integer outVer) {
        Pair<Integer, Integer> oldVersion = workerVersions.get(hostName);
        Pair<Integer, Integer> newVersion = new Pair<>(oldVersion.getKey(), outVer);
        workerVersions.put(hostName, newVersion);
    }

    public HashMap<String, Pair<Integer, Integer>> getWorkerVersions() {
        return workerVersions;
    }

    public void addRef() {
        Ref ++ ;
    }
    public void unRef() {
        Ref -- ;
    }

    public int getRef() {
        return Ref;
    }

    public RTree<String, Rectangle> getrTree() {
        return rTree;
    }

    public void setrTree(RTree<String, Rectangle> rTree) {
        this.rTree = rTree;
    }

    public Version deepCopy() {
        // 使用Gson序列化进行深拷贝
        Gson gson = new Gson();
        Version copyVersion = gson.fromJson(gson.toJson(this), Version.class);
        return copyVersion;
    }

}
