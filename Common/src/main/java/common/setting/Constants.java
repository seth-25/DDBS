package common.setting;

public class Constants {
    public static class FileTransferStep {
        public static final int FILE_REQUEST = 100;  // 客户端向服务端请求发送文件
        public static final int FILE_RESPONSE = 101;  // 服务端回复同意客户端的请求
        public static final int FILE_DATA = 102;  // 客户端发送数据
    }

    public static class FileStatus {
        public static final int BEGIN = 110;    //开始
        public static final int CENTER = 111;   //中间
        public static final int END = 112;      //结尾
        public static final int COMPLETE = 113; //完成
    }

//    public static class FileType {
//        public static final int SAX_STATISTIC = 120;
//    }

    public static class WorkerStatus {
        public static final String INIT = "init"; // 初始化
        public static final String HAS_SORT = "has sort"; // 已经本地归并排序
        public static final String HAS_SENT_SAX_STATISTIC = "has sent sax statistic"; // 已经将sax值的统计发给master
        public static final String HAS_SENT_SAX = "has sent sax"; // 已经将sax发给worker
        public static final String HAS_PUT_SAX = "has put sax"; // 已经将sax存入leveldb
        public static final String HAS_PUT_TS = "has put ts"; // 已经将ts存入本机

        public static final String RUNNING = "running"; // 运行中

        public static final String CHANGE_VERSION = "change version"; // 改变版本

        public static final String BALANCE = "in balance";
    }

    public static class MsgType {
        // init
        public static final int SEND_SAX_STATISTIC = 1; // Master向Worker请求发送sax统计
        public static final int SAX_STATISTIC = 2; // Worker向Master请求发送的sax统计
        public static final int SAX_STATISTIC_FINISH = 3; // Master告诉Worker已收到Worker发送的sax统计
        public static final int SAX_RANGES = 4; // Master向Worker请求发送sax
        public static final int TS_RANGES = 5; // Master向Worker发送ts范围
        // insert
        public static final int SEND_SAX = 6; // Worker向Worker发送sax
        public static final int SEND_TS = 7; // Worker向Worker发送ts
        public static final int SEND_TS_FINISH = 8; // Worker向Worker最后发送ts

        public static final int INSERT_TS = 9;   // Client向Worker发送TS
        public static final int INSERT_TS_FINISH = 10;   // Client发完所有TS
        // version
        public static final int SEND_VERSION = 11; // worker发送改变的版本
        public static final int DELETE_IN_VERSION = 12; // worker删除内存版本
        public static final int DELETE_OUT_VERSION = 13; // worker删除外存版本

        public static final int FINISH = 99;   // 操作结束
    }

}