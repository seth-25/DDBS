package com.distributed.domain;

public class Parameters {
    public static final String hostName = "Ubuntu001"; // 本机的hostname
    public static final int numThread = 4;    // 该机器线程数
    public static final int numWorkerInit = 2;    // 初始化时worker的数量
    public static final int saxDataSize = 8; // 一条saxData有多少字节(时间序列大小)
    public static final int saxSimSize = 4;    // 计算sax相似度时，以多少字节为单位计算，需要能整除saxDataSize
    public static final int tsHash = 256;   // 时间戳哈希取余大小

    public static class FileNettyServer {
        public static final int port = 6666;
    }
    public static class FileNettyClient {
        public static final int port = 6666;
    }

    public static class InstructNettyServer {
        public static final int port = 6667;
    }

    public static class InstructNettyClient {
        public static final int port = 6667;
    }

    public static class Zookeeper {
        public static final String connectString = "Ubuntu001:2181,Ubuntu002:2181,Ubuntu003:2181";
        public static final String workerFolder = "/workers/";
    }

    public static class MergeSort {
        public static final String saxStatisticFolder = "./sax_statistic/"; // 统计sax值的个数存放位置
        public static final String mergeSortSaxStatisticFolder = "./merge_sort_sax_statistic/"; // 归并排序后的存放位置

        public static final int readSize = saxDataSize * 4096; // 读取文件时一次读取字节数
        public static final int writeSize = saxDataSize * 4096; // 写文件时一次写出字节数
        public static final int numSaxCntPerFile = 3; // 多少个saxData存一个文件

    }

    public static class Divide {

        public static final int lowerBound = 50;    // 范围内个数可允许少的个数
        public static final int upperBound = 50;    // 范围内个数可允许多的个数
    }
}
