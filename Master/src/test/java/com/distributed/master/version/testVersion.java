package com.distributed.master.version;

import com.distributed.domain.Version;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import javafx.util.Pair;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class testVersion {
    @Test
    public void testCopy() {
        Version version = new Version();
        version.getWorkerVersions().put("test", new Pair<>(1, 1));
        Version version1 = version.deepCopy();

        System.out.println(version.getWorkerVersions());
        System.out.println(version1.getWorkerVersions());
        version.getWorkerVersions().put("test", new Pair<>(2, 2));

        System.out.println(version.getWorkerVersions());
        System.out.println(version1.getWorkerVersions());
    }


    @Test
    public void testThread() throws InterruptedException {
        final int taskCount = 100;    // 任务总数
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(100);
        CountDownLatch countDownLatch = new CountDownLatch(taskCount);
//        TreeMap<String, ArrayList<Sax>> workerSaxes = new TreeMap<>();
        for (int i = 0; i < 100; i ++ ) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    VersionAction.updateCurVersion(new Version());
                    countDownLatch.countDown();
                }
            };
            newFixedThreadPool.execute(runnable);
        }
        countDownLatch.await(); //  等待所有线程结束
    }

    @Test
    public void rTreeTest() {
        RTree<String, Rectangle> tree = RTree.create();

        tree = tree.add("r1", Geometries.rectangle(3, 8, 25, 32));
        tree = tree.add("r3", Geometries.rectangle(4, 23, 12, 29));
        tree = tree.add("r16", Geometries.rectangle(7, 5, 13, 13));

        Iterable<Entry<String, Rectangle>> results = tree.search(Geometries.rectangle(7,5,13,13)).toBlocking().toIterable();
        for (Entry<String, Rectangle> result : results) {
            System.out.println(result);
        }
        System.out.println();
        results = tree.search(Geometries.rectangle(7,5,13,7)).toBlocking().toIterable();
        for (Entry<String, Rectangle> result : results) {
            System.out.println(result);
            System.out.println(result.value());
        }

    }

    @Test
    public void rTreeTest1() {

        long l = 0x7fffffffffffffffL;
        System.out.println("long value: " + l);
        double d = (double) l;
        System.out.println("double value: " + d);


        RTree<String, Rectangle> tree = RTree.create();
//        byte[] left = {0, 0, 0, 0, 0, 0, 0, 0};
//        byte[] right = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
//        SaxData saxLeft = new SaxData(left, 8);
//        SaxData saxRight = new SaxData(right, 8);

        tree = tree.add("r1", Geometries.rectangle(3, 8, 25, 32));
        tree = tree.add("r3", Geometries.rectangle(4, 23, 12, 29));
        tree = tree.add("r16", Geometries.rectangle(7, 5, 13, 13));

        Iterable<Entry<String, Rectangle>> results = tree.search(Geometries.rectangle(7,5,13,13)).toBlocking().toIterable();
        for (Entry<String, Rectangle> result : results) {
            System.out.println(result);
        }
        System.out.println();
        results = tree.search(Geometries.rectangle(7,5,13,7)).toBlocking().toIterable();
        for (Entry<String, Rectangle> result : results) {
            System.out.println(result);
        }

    }

//    public static void main(String[] args) {
////        Thread master_thread = new Master();
////        master_thread.start();
//
////        Version version = new Version();
////        version.setrTree(RTree.create());
////        VersionAction.updateCurVersion(version);
//
//
//        HashMap<Integer, Integer> mapOut = new HashMap<>();
//        mapOut.put(0, 0);
//        CacheUtil.workerOutVerRef.put("Ubuntu002", mapOut);
//
//        HashMap<Integer, Integer> mapIn = new HashMap<>();
//        mapIn.put(0, 0);
//        CacheUtil.workerInVerRef.put("Ubuntu002", mapIn);
//
//    }
}
