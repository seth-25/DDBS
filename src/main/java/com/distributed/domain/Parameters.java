package com.distributed.domain;

public class Parameters {

    public static String hostName = "Ubuntu002"; // 本机的hostname
    public static int numThread = 4;

    public static final int saxSize = 24; // 一条sax大小多少字节
    public static final int dataSize = 16; // sax中数据大小
    public static final int pointerSize = saxSize - dataSize; // sax中指针的大小

    public static class MergeSort {
        public static final String inputFolderPath = "./data/"; // sax存放位置
        public static final String memorySortPath = "./memory_sort_data/"; // 内存排序后的sax存放位置
        public static final String mergeSortPath = "./merge_sort_data/"; // 归并排序后的sax存放位置
        public static final String countSaxPath = "./count_sax/"; // 统计sax值的个数存放位置
    }
    public static class MemorySort {


        public static final int readSize = saxSize * 100000; // 读取文件时一次读取字节数
        public static final int writeSize = saxSize * 100000; // 写文件时一次写出字节数
        public static final String inputFolderPath = "./data/"; // sax存放位置
        public static final String memorySortPath = "./memory_sort_data/"; // 内存排序后的sax存放位置

    }
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
        public static final String workerPath = workerFolder + hostName;
    }

}
