package com.distributed.worker.sort;

import com.distributed.domain.Parameters;
import com.distributed.domain.Sax;
import com.distributed.util.CacheUtil;
import com.distributed.util.FileChannelReader;
import com.distributed.util.FileChannelWriter;
import com.distributed.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MemorySort {

    private void addCnt(String saxStr) {
        if (CacheUtil.cntInitSaxes.containsKey(saxStr)) { // 统计+1
            CacheUtil.cntInitSaxes.put(saxStr, CacheUtil.cntInitSaxes.get(saxStr) + 1);
        }
        else {
            CacheUtil.cntInitSaxes.put(saxStr, 1L);
        }
    }

    private ArrayList<Sax> createSaxes(byte[] arrays) {
        int cntByte = 0;
        ArrayList<Sax> saxes = new ArrayList<>();
        byte[] saxByte = new byte[Parameters.MemorySort.readSize]; // 一个sax的byte形式
        for (byte array: arrays) {
            saxByte[cntByte ++ ] = array;
            if (cntByte == Parameters.saxSize) {
                cntByte = 0;
                Sax sax = new Sax(saxByte, Parameters.dataSize, Parameters.pointerSize);
                saxes.add(sax);
                saxByte = new byte[Parameters.MemorySort.readSize];

                addCnt(sax.toString()); // 统计这个sax的值出现几次
            }
        }
        return saxes;
    }

    // 排序好的sax写到内存里
    private void writeSax(ArrayList<Sax> saxes) {
        CacheUtil.initSaxes = saxes;
    }

    // 排序好的sax写到文件里
    private void writeSaxFile(String fileName, ArrayList<Sax> saxes, String path) throws IOException {
        byte[] content = new byte[saxes.size() * Parameters.saxSize];
        for (int i = 0; i < saxes.size(); i ++ ) {
            byte[] saxByte = saxes.get(i).getSax(); // 一个sax的byte形式
            System.arraycopy(saxByte, 0, content, i * Parameters.saxSize, Parameters.saxSize);
        }
        FileChannelWriter writer = new FileChannelWriter(path + fileName, Parameters.MemorySort.writeSize);
        writer.write(content);
        writer.close();
    }
    public void memorySort() throws IOException {
        ArrayList<File> files = FileUtil.getAllFile(Parameters.MemorySort.inputFolderPath);
        int cntFile = 0;
        ArrayList<Sax> saxes = new ArrayList<>();
        for (File file: files) {
            FileChannelReader reader = new FileChannelReader(file.getPath(), Parameters.MemorySort.readSize);
            while (reader.read() != -1) {
                byte[] arrays = reader.getArray();
                saxes.addAll(createSaxes(arrays));
            }
            reader.close();
        }
        saxes.sort(Sax::compareTo);  // 内存排序
        writeSax(saxes);
//        writeSaxFile(String.valueOf(cntFile), saxes, Parameters.MemorySort.memorySortPath); // 写入文件
    }
}
