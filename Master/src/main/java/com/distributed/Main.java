package com.distributed;

import common.setting.Parameters;
import com.distributed.master.Master;
import com.distributed.master.instruct_netty_server.InstructServer;

public class Main {
    public static void main(String[] args) {
        Master master = new Master();
        Thread masterThread = new Thread(master);
        masterThread.start();

        InstructServer instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        Thread instructServerThread = new Thread(instructServer);
        instructServerThread.start();

//        FileServer fileServer = new FileServer(Parameters.FileNettyServer.port);
//        fileServer.start();
    }
}
