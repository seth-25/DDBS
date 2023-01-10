package com.distributed.worker.instruct_netty_server;

import common.setting.Parameters;
import org.junit.Test;

public class InstructServerTest {
    @Test
    public void testInstruction() throws InterruptedException {
        //启动服务端
        InstructServer instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();
    }

    public static void main(String[] args) {
        //启动服务端
        InstructServer instructServer = new InstructServer(Parameters.InstructNettyServer.port);
        instructServer.start();
    }
}
