package com.distributed.master.file_netty_server;

import com.distributed.domain.Parameters;

public class FileServerTest {
    public static void main(String[] args) {
        FileServer fileServer = new FileServer(Parameters.FileNettyServer.port);
        fileServer.start();

    }
}
