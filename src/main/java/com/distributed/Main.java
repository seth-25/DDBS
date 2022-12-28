package com.distributed;

import com.distributed.domain.Parameters;
import com.distributed.util.CoordinatorUtil;
import com.distributed.util.DBUtil;
import com.distributed.worker.Worker;
import com.distributed.worker.file_netty_server.FileServer;
import com.distributed.worker.instruct_netty_client.InstructClient;
import com.distributed.worker.instruct_netty_server.InstructServer;


public class Main {
    public static void main(String[] args) {
        DBUtil.dataBase.open("./db_data");
        DBUtil.dataBase.init(new byte[Parameters.Init.numTs * Parameters.saxSize], Parameters.Init.numTs);
        Thread worker_thread = new Worker(CoordinatorUtil.coordinator);
        worker_thread.start();

        Thread instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();

//        Thread fileNettyServer = new FileServer(Parameters.FileNettyServer.port);
//        fileNettyServer.start();
    }
}
