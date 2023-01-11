package common.setting;
public class Parameters {

    public static String hostName = "Ubuntu002"; // 本机的hostname
    public static int numThread = 4;


    public static final int saxDataSize = 16; // sax中数据大小
    public static final int saxPointerSize = 8; // sax中指针的大小
    public static final int saxSize = saxDataSize + saxPointerSize; // 一条sax大小多少字节

    public static final int timeSeriesDataSize = 256 * 4;   // 时间序列的大小
    public static final int timeStampSize = 8; // 时间戳大小
    public static final int tsSize = timeSeriesDataSize + timeStampSize;
    public static final int tsHash = 256;   // 时间戳哈希取余大小

    public static final String tsFolder = "./ts/";  // 存储ts的文件夹


    public static class FileNettyServer {
        public static final int port = 6666;
    }


    public static class InstructNettyServer {
        public static final int port = 6667;
    }

    public static class InstructNettyClient {
        public static final int port = 6667;
    }
    public static class TsNettyServer {
        public static final int port = 6668;
    }

    public static class Zookeeper {
        public static final String connectString = "Ubuntu001:2181,Ubuntu002:2181,Ubuntu003:2181";
        public static final String workerFolder = "/workers/";
        public static final String workerPath = workerFolder + hostName;
    }

    public static class Init {
        public static final int numTs = 1000000; // 初始化ts的个数
    }

    public static class MergeSort {
        public static final String inputFolderPath = "./data/"; // sax存放位置
        public static final String memorySortPath = "./memory_sort_data/"; // 内存排序后的sax存放位置
        public static final String mergeSortPath = "./merge_sort_data/"; // 归并排序后的sax存放位置
        public static final String countSaxPath = "./count_sax/"; // 统计sax值的个数存放位置
    }
    public static class MemorySort {
        //        public static final int readSize = tsSize * 10000; // 读取文件时一次读取字节数
        public static final int readSize = tsSize * 1000; // 读取文件时一次读取字节数
        public static final int writeSize = tsSize * 10000; // 写文件时一次写出字节数
        public static final String inputFolderPath = "./ts_init/"; // 初始化ts存放位置
        public static final String memorySortPath = "./memory_sort_data/"; // 内存排序后的sax存放位置

    }

    public static class Insert {
        public static final int batchTrans = 1000; // 一次性发送几个ts/sax
        public static final int cntTrans = 10; // 几次传输都没向该worker发送ts/sax，则需要向该worker发送
    }

}
