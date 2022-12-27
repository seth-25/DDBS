package com.distributed.worker.ts_netty_server;


import com.distributed.domain.Constants;
import com.distributed.domain.Parameters;
import com.distributed.util.CacheUtil;
import com.distributed.util.DBUtil;
import com.distributed.worker.instruct_netty_server.InstructServer;
import javafx.util.Pair;

public class TsServerTest {
    public static void main(String[] args) {
        DBUtil.dataBase.open("./db_data");
        CacheUtil.workerState = Constants.WorkerStatus.RUNNING;
        CacheUtil.timeStampRanges.put("Ubuntu001", new Pair<>(0, 127));
        CacheUtil.timeStampRanges.put("Ubuntu002", new Pair<>(128, 255));
        TsServer tsServer = new TsServer(Parameters.TsNettyServer.port);
        tsServer.start();
        InstructServer instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();
    }
}
