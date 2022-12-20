package com.distributed;

import com.distributed.domain.Parameters;
import com.distributed.util.CoordinatorUtil;
import com.distributed.worker.Coordinator;
import com.distributed.worker.Worker;
import com.distributed.worker.netty_server.MyNettyServer;


public class Main {
    public static void main(String[] args) {
        Thread worker_thread = new Worker(CoordinatorUtil.coordinator);
        worker_thread.start();

        Thread myNettyServer = new MyNettyServer(Parameters.NettyServer.port);
        myNettyServer.start();
    }
}
