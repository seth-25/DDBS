package com.distributed;

import com.distributed.domain.Parameters;
import com.distributed.util.CoordinatorUtil;
import com.distributed.worker.Worker;
import com.distributed.worker.file_netty_server.FileServer;
import com.distributed.worker.instruct_netty_client.InstructClient;
import com.distributed.worker.instruct_netty_server.InstructServer;


public class Main {
    public static void main(String[] args) {
        Thread worker_thread = new Worker(CoordinatorUtil.coordinator);
        worker_thread.start();

        Thread instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();

//        Thread fileNettyServer = new FileServer(Parameters.FileNettyServer.port);
//        fileNettyServer.start();
    }
}
