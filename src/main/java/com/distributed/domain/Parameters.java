package com.distributed.domain;

public class Parameters {

    public static String hostName = "Ubuntu002"; // 本机的hostname
    public static int numThread = 4;

    public static class MergeSort {
        public static final String inputFolderPath = "./data/"; // sax存放位置
        public static final String memorySortPath = "./memory_sort_data/"; // 内存排序后的sax存放位置
        public static final String mergeSortPath = "./merge_sort_data/"; // 归并排序后的sax存放位置
        public static final String countSaxPath = "./count_sax/"; // 统计sax值的个数存放位置
    }
    public static class NettyServer {
        public static final int port = 6666;
    }

    public static class NettyClient {
        public static final int port = 6666;
    }

    public static class Zookeeper {
        public static final String connectString = "Ubuntu001:2181,Ubuntu002:2181,Ubuntu003:2181";
        public static final String workerFolder = "/workers/";
        public static final String workerPath = workerFolder + hostName;
    }

}
