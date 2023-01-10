package com.distributed.worker.init;//package com.distributed.worker.sort;
//
//import com.distributed.util.FileChannelReader;
//import com.distributed.util.FileChannelWriter;
//import common.domain.Sax;
//import com.distributed.util.FileUtil;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public class MergeSort {
//
//    private int saxSize = 24; // 一条sax大小多少字节
//    private int dataSize = 16; // sax中数据大小
//    private int pointerSize = saxSize - dataSize; // sax中指针的大小
//    // memory sort
//    private int readSize = saxSize * 4096; // 读取文件时一次读取字节数
//    private int writeSize = saxSize * 4096; // 写文件时一次写出字节数
//    private int numSaxPerFile = 40000; // 一个归并段存多少个sax，也是内存排序的sax个数
//    private int numSaxCntPerFile = 3;
//
//
//    private String inputFolderPath = "./data/"; // sax存放位置
//    private String memorySortPath = "./memory_sort_data/"; // 内存排序后的sax存放位置
//    private String mergeSortPath = "./merge_sort_data/"; // 归并排序后的sax存放位置
//    private String countSaxPath = "./count_sax/"; // 统计sax值的个数存放位置
//
//    public MergeSort() {
//
//    }
//
//
//
//    // 将1个buffer内的数据转化成多个Sax
//    public ArrayList<Sax> createSaxes(byte[] arrays) {
//        int cntByte = 0;
//        ArrayList<Sax> saxes = new ArrayList<>();
//        byte[] saxByte = new byte[saxSize]; // 一个sax的byte形式
//        for (byte array: arrays) {
//            saxByte[cntByte ++ ] = array;
//            if (cntByte == saxSize) {
//                cntByte = 0;
//                saxes.add(new Sax(saxByte, dataSize, pointerSize));
//                saxByte = new byte[saxSize];
//            }
//        }
//        return saxes;
//    }
//
//    private void writeSaxFile(String fileName, ArrayList<Sax> saxes, String path) throws IOException {
//        byte[] content = new byte[saxes.size() * saxSize];
//        for (int i = 0; i < saxes.size(); i ++ ) {
//            byte[] saxByte = saxes.get(i).getSax(); // 一个sax的byte形式
//            System.arraycopy(saxByte, 0, content, i * saxSize, saxSize);
//        }
//        FileChannelWriter writer = new FileChannelWriter(path + fileName, writeSize);
//        writer.write(content);
//        writer.close();
//    }
//
//    // 将一定数量的sax排好序，写到一个文件中
//    public void memorySort() throws IOException {
//
//        ArrayList<File> files = FileUtil.getAllFile(inputFolderPath);
//        int cntFile = 0;
////        Sax[] saxesPerFile = new Sax[numSaxPerFile];
//        ArrayList<Sax> saxesPerFile = new ArrayList<>();
//
//        for (File file: files) {
//            FileChannelReader reader = new FileChannelReader(file.getPath(), readSize);
//            while (reader.read() != -1) {
//                byte[] arrays = reader.getArray();
//                ArrayList<Sax> saxes = createSaxes(arrays);
//                for (Sax sax: saxes) { // 遍历buffer内的sax，存到数组里，直到sax的个数达到一定个数(numSaxPerFile)时，排序并写入文件。
//                    saxesPerFile.add(sax);
//                    if (saxesPerFile.size() >= numSaxPerFile) {
//                        saxesPerFile.sort(Sax::compareTo); // 内存排序
//                        writeSaxFile(String.valueOf(cntFile), saxesPerFile, memorySortPath); // 写入文件
//                        cntFile ++;
//                        saxesPerFile = new ArrayList<>();
//                    }
//                }
//            }
//            reader.close();
//        }
//
//        // 剩余部分
//        if (saxesPerFile.size() > 0) {
//            saxesPerFile.sort(Sax::compareTo);  // 内存排序
//            writeSaxFile(String.valueOf(cntFile), saxesPerFile, memorySortPath); // 写入文件
//        }
//
//    }
//
//    public void mergeSort() throws IOException {
//        ArrayList<File> files = FileUtil.getAllFile(memorySortPath);
//        int K = files.size();  // k路归并
//        ArrayList<FileChannelReader> readers = new ArrayList<>();
//        for (File file: files) {
//            FileChannelReader reader = new FileChannelReader(file.getPath(), readSize / K); // 每路取readSize的1/k的数据
//            readers.add(reader);
//        }
//
//
//        ArrayList<ArrayList<Sax>> k_saxes = new ArrayList<>();
//        int[] index = new int[K];
//        for (FileChannelReader reader: readers) { // 取出k路数据
//            if (reader.read() != -1) {
//                byte[] arrays = reader.getArray();
//                k_saxes.add(createSaxes(arrays));
//            }
//            else {
//                throw new IOException(); // memorySortPath文件夹下没有可读取的内容
//            }
//        }
//
//        int numSaxFile = 0, numSaxCntFile = 0;
//        ArrayList<Sax> saxesPerFile = new ArrayList<>();  // 保存排序后的sax
//        LinkedHashMap<String, Long> cntSaxesPerFile = new LinkedHashMap<>(); // 统计各种值的sax的个数
//        boolean readAllFile = false;
//        while(!readAllFile) {
//            Sax min_sax = null;
//            int min_sax_k = 0;
//
//            readAllFile = true;
//            // todo 败者树优化 O(k) -> O(log(k))
//            for (int k = 0; k < k_saxes.size(); k ++ ) {
//                if (k_saxes.get(k) == null) {
//                    continue;
//                }
//                readAllFile = false;
//                Sax sax = k_saxes.get(k).get(index[k]);
//                if (min_sax == null || min_sax.compareTo(sax) > 0) {
//                    min_sax = sax;
//                    min_sax_k = k;
//                }
//            }
//            if (min_sax != null) {
//
//                saxesPerFile.add(min_sax);
//                if (saxesPerFile.size() >= numSaxPerFile) { // 写入sax排序文件
//                    writeSaxFile(String.valueOf(numSaxFile), saxesPerFile, mergeSortPath);
//                    numSaxFile ++;
//                    saxesPerFile = new ArrayList<>();
//                }
//
//                String str_min_sax = min_sax.toString();
//                if (cntSaxesPerFile.containsKey(str_min_sax)) { // 统计+1
//                    cntSaxesPerFile.put(str_min_sax, cntSaxesPerFile.get(str_min_sax) + 1);
//                }
//                else {
//                    cntSaxesPerFile.put(str_min_sax, 1L);
//                }
//                if (cntSaxesPerFile.size() > numSaxCntPerFile) { // 写入sax统计文件
//                    cntSaxesPerFile.remove(str_min_sax);
//                    writeCntSaxFile(String.valueOf(numSaxCntFile), cntSaxesPerFile, countSaxPath);
//                    numSaxCntFile ++;
//                    cntSaxesPerFile = new LinkedHashMap<>();
//                    cntSaxesPerFile.put(str_min_sax, 1L);
//                }
//
//                index[min_sax_k] ++ ;
//                if (index[min_sax_k] >= k_saxes.get(min_sax_k).size()) { // 判断第k路是否到末尾了
//                    index[min_sax_k] = 0;
//                    FileChannelReader reader = readers.get(min_sax_k); // 第k路读入下一组数据
//                    if (reader.read() != -1) {
//                        byte[] arrays = reader.getArray();
//                        k_saxes.set(min_sax_k, createSaxes(arrays));
//                    }
//                    else {
//                        k_saxes.set(min_sax_k, null);
//                    }
//                }
//            }
//        }
//        if (saxesPerFile.size() > 0) { // 剩余部分写入文件
//            writeSaxFile(String.valueOf(numSaxFile), saxesPerFile, mergeSortPath);
//        }
//        if (cntSaxesPerFile.size() > 0) { // 剩余部分写入文件
//            writeCntSaxFile(String.valueOf(numSaxCntFile), cntSaxesPerFile, countSaxPath);
//        }
//    }
//
//    private void writeCntSaxFile(String fileName, HashMap<String, Long> cntSaxes, String path) throws IOException {
//        ArrayList<Byte> contentList = new ArrayList<>();
//        Set<Map.Entry<String, Long>> entries = cntSaxes.entrySet();
//        for (Map.Entry<String, Long> entry: entries) {
//            String str = entry.getKey(); // 一个sax的byte形式
//            str += "=";
//            str += entry.getValue();
//            str += ",";
//            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
//            for (byte b : bytes) {
//                contentList.add(b);
//            }
//        }
//        byte[] content = new byte[contentList.size()];
//        for (int i = 0; i < contentList.size(); i ++ ) {
//            content[i] = contentList.get(i);
//        }
//        FileChannelWriter writer = new FileChannelWriter(path + fileName, writeSize);
//        writer.write(content);
//        writer.close();
//    }
//}
