package com.distributed.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;


public class FileChannelWriter {
    private FileOutputStream fileOut;
    private ByteBuffer byteBuf;
    private long fileLength;
    private int arraySize; // Buffer大小（字节）

    public FileChannelWriter(String fileName, int arraySize) throws IOException {
        File file = new File(fileName);
        File folder = file.getParentFile();
        if (!folder.exists()) {
            boolean flag = folder.mkdir();
        }
        this.fileOut = new FileOutputStream(fileName);
        this.fileLength = fileOut.getChannel().size();
        this.arraySize = arraySize;
        this.byteBuf = ByteBuffer.allocate(arraySize);
    }

    public void write(byte[] content) throws IOException {
        FileChannel fileChannel = fileOut.getChannel();
//        byteBuf = ByteBuffer.wrap(context);
//        fileChannel.write(byteBuf);
        byte[] bufContext = new byte[arraySize];
        int cnt = 0;
        for (byte b : content) {    // content可能比byteBuf大，一次铐arraySize个字节给byteBuf
            bufContext[cnt ++ ] = b;
            if (cnt >= arraySize) {
                cnt = 0;
                byteBuf.put(bufContext);
                byteBuf.flip();
                while (byteBuf.hasRemaining()) {
                    fileChannel.write(byteBuf);
                }
                byteBuf.clear();
            }
        }
        // 剩余部分
        byte[] resBufContext = Arrays.copyOf(bufContext, content.length % arraySize);
        if (resBufContext.length > 0) {
            byteBuf.put(resBufContext);
            byteBuf.flip();
            while (byteBuf.hasRemaining()) {
                fileChannel.write(byteBuf);
            }
            byteBuf.clear();
        }
    }

    public void close() throws IOException {
        fileOut.close();
    }


    public long getFileLength() {
        return fileLength;
    }

    public static void main(String[] args) throws IOException {
        FileChannelWriter writer = new FileChannelWriter("./2.txt", 65536);
        long start = System.nanoTime();

        writer.write("1000234".getBytes());

        long end = System.nanoTime();
        writer.close();
        System.out.println("ChannelFileWriter: " + (end - start));
    }
}
