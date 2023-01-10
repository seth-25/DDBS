package com.distributed.domain;

public class Constants {
    public static class TransferType {
        public static final int INSTRUCT = 0;
        public static final int FILE = 1;
    }

    public static class TransferStep {
        public static final int FILE_REQUEST = 10;  // 客户端向服务端请求发送文件
        public static final int FILE_RESPONSE = 11;  // 服务端回复同意客户端的请求
        public static final int FILE_DATA = 12;  // 客户端发送数据
    }

    public static class FileStatus {
        public static final int BEGIN = 0;    //开始
        public static final int CENTER = 1;   //中间
        public static final int END = 2;      //结尾
        public static final int COMPLETE = 3; //完成
    }

    public static class FileType {
        public static final String SAX_STATISTIC = "sax statistic";
    }

    public static class WorkerStatus {
        public static final String HAS_SORT = "has sort"; // 已经本地归并排序
        public static final String HAS_SENT_SAX_STATISTIC = "has sent sax statistic"; // 已经将sax值的统计发给master

        public static final String CHANGE_VERSION = "change version"; // 改变版本
    }

    public static class InstructionType {
        public static final String SEND_SAX_STATISTIC = "send sax statistic"; // Master向Worker请求发送sax
        public static final String SAX_STATISTIC = "sax statistic"; // Master向Worker请求发送sax
        public static final String SAX_RANGES = "sax ranges"; // Master向Worker发送sax范围
        public static final String TS_RANGES = "ts ranges"; // Master向Worker发送ts范围

        public static final String SEND_VERSION = "send version"; // worker发送改变的版本
        public static final String DELETE_IN_VERSION = "delete in version"; // worker删除内存版本
        public static final String DELETE_OUT_VERSION = "delete out version"; // worker删除外存版本
        public static final String FINISH = "finish"; // 操作结束
    }
}
