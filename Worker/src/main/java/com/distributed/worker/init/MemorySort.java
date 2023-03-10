package com.distributed.worker.init;

import common.setting.Parameters;
import common.domain.Sax;
import common.domain.TimeSeries;
import com.distributed.util.*;
import com.distributed.util.FileChannelReader;
import com.distributed.util.FileUtil;
import common.util.SaxUtil;
import common.util.TsUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MemorySort {
    private ArrayList<Sax> saxes = new ArrayList<>();
    private long file_offset = 0;

    private void addCnt(String saxStr) {
        if (CacheUtil.cntInitSaxes.containsKey(saxStr)) { // 统计+1
            CacheUtil.cntInitSaxes.put(saxStr, CacheUtil.cntInitSaxes.get(saxStr) + 1);
        }
        else {
            CacheUtil.cntInitSaxes.put(saxStr, 1L);
        }
    }

//    private ArrayList<Sax> createSaxes(byte[] arrays) {
//        int cntByte = 0;
//        ArrayList<Sax> saxes = new ArrayList<>();
//        byte[] saxByte = new byte[Parameters.MemorySort.readSize]; // 一个sax的byte形式
//        for (byte array: arrays) {
//            saxByte[cntByte ++ ] = array;
//            if (cntByte == Parameters.saxSize) {
//                cntByte = 0;
//                Sax sax = new Sax(saxByte, Parameters.saxDataSize, Parameters.saxPointerSize);
//                saxes.add(sax);
//                saxByte = new byte[Parameters.MemorySort.readSize];
//                addCnt(sax.toString()); // 统计这个sax的值出现几次
//            }
//        }
//        return saxes;
//    }


    public static float bytesToFloat(byte[] bytes) {
        int f = 0;
        for (int i = 0; i < 4; i ++ ) {
            f <<= 8;
            f |= (bytes[4 - 1 - i] & 0xff);  // 小端
        }
        return Float.intBitsToFloat(f);
    }


    private ArrayList<Sax> createTsAndSax(byte[] arrays) {
        int array_offset = 0;
        while (array_offset < arrays.length) {
            byte[] tsData = new byte[Parameters.timeSeriesDataSize];
            byte[] timeStamp = new byte[Parameters.timeStampSize];
            System.arraycopy(arrays, array_offset, tsData, 0, Parameters.timeSeriesDataSize);
            System.arraycopy(arrays, array_offset + Parameters.timeSeriesDataSize, timeStamp, 0, Parameters.timeStampSize);
            array_offset += Parameters.tsSize;
            file_offset += Parameters.tsSize;
            TimeSeries timeSeries = new TimeSeries(tsData, timeStamp);
            CacheUtil.initTs.add(timeSeries);   // 初始化时ts较小,先放内存


            // ts转化sax接口
            byte[] saxData = new byte[Parameters.saxDataSize];
            DBUtil.dataBase.saxt_from_ts(tsData, saxData);
            Sax sax = new Sax(saxData, (byte) TsUtil.computeHash(timeSeries), SaxUtil.createPointerOffset(file_offset), timeStamp);
            saxes.add(sax);
            addCnt(sax.toString()); // 统计这个sax的值出现几次
        }
        return saxes;
    }

    // 排序好的sax写到内存里
    private void writeSax(ArrayList<Sax> saxes) {
        CacheUtil.initSaxes = saxes;
    }

    // 排序好的sax写到文件里
//    private void writeSaxFile(String fileName, ArrayList<Sax> saxes, String path) throws IOException {
//        byte[] content = new byte[saxes.size() * Parameters.saxSize];
//        for (int i = 0; i < saxes.size(); i ++ ) {
//            byte[] saxByte = saxes.get(i).getSax(); // 一个sax的byte形式
//            System.arraycopy(saxByte, 0, content, i * Parameters.saxSize, Parameters.saxSize);
//        }
//        FileChannelWriter writer = new FileChannelWriter(path + fileName, Parameters.MemorySort.writeSize);
//        writer.write(content);
//        writer.close();
//    }
    public void memorySort() throws IOException {
        ArrayList<File> files = FileUtil.getAllFile(Parameters.MemorySort.inputFolderPath);
        int cntFile = 0;
        ArrayList<Sax> saxes = new ArrayList<>();
        for (File file: files) {
            FileChannelReader reader = new FileChannelReader(file.getPath(), Parameters.MemorySort.readSize);
            file_offset = 0;
            while (reader.read() != -1) {
                byte[] arrays = reader.getArray();
                saxes.addAll(createTsAndSax(arrays));
            }
            reader.close();
        }

        saxes.sort(Sax::compareTo);  // 内存排序
        writeSax(saxes);
//        writeSaxFile(String.valueOf(cntFile), saxes, Parameters.MemorySort.memorySortPath); // 写入文件
    }
}
