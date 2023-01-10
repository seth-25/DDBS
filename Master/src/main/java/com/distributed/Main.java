package com.distributed;

import com.distributed.domain.Parameters;
import com.distributed.master.Master;
import com.distributed.master.instruct_netty_server.InstructServer;

public class Main {
    public static void main(String[] args) {
        Thread master_thread = new Master();
        master_thread.start();

        Thread instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();

//        FileServer fileServer = new FileServer(Parameters.FileNettyServer.port);
//        fileServer.start();
    }
}
