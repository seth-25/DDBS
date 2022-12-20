package com.distributed.worker.netty_server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioServer extends Thread{

    static final Integer PORT = 2333;

    public void run() {
        try {
            // 1. 打开一个服务端通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 2. 绑定对应的端口号
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            // 3. 通道默认是阻塞的，需要设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            // 4. 创建选择器
            Selector selector = Selector.open();
            // 5. 将服务端通道注册到选择器上,并指定注册监听的事件为OP_ACCEPT
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NioServer启动并注册成功");
            while (true) {
                // 6. 检查选择器是否有事件(select方法返回的是选择器中的事件个数，如果不传入等待时间则一直阻塞)
                int select = selector.select(2000);
                if (select == 0) {
//                System.out.println("无事发生");
                    continue;
                }
                // 7. 获取事件集合
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    // 8. 判断事件是否是客户端连接事件SelectionKey.isAcceptable()
                    SelectionKey key = keys.next();
                    if (key.isAcceptable()) {
                        // 9. 得到客户端通道,并将通道注册到选择器上, 并指定监听事件为OP_READ
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        System.out.println("有NioClient连接了");
                        // 必须将通道设置成非阻塞状态，因为Selector选择器需要轮询监听每个通道的事件，如果有一个通道是非阻塞的话就无法继续轮询了
                        socketChannel.configureBlocking(false);
                        // 指定监听事件为OP_READ
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    // 10. 判断是否是客户端读就绪事件SelectionKey.isReadable()
                    if (key.isReadable()) {
                        // 11. 得到客户端通道,读取数据到缓冲区
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer allocate = ByteBuffer.allocate(1024);
                        int read = socketChannel.read(allocate);
                        // 如果读到数据
                        if (read > 0) {
                            System.out.println("NioClient消息："+new String(allocate.array(),0,read, StandardCharsets.UTF_8));
                            // 12. 给客户端回写数据
                            socketChannel.write(ByteBuffer.wrap("accept".getBytes(StandardCharsets.UTF_8)));
                            // 关闭通道
                            socketChannel.close();
                        }
                    }
                    // 13. 从集合中删除对应的事件, 因为防止二次处理.
                    keys.remove();
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
