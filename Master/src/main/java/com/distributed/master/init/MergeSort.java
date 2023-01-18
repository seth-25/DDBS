//package com.distributed.master.init;
//
//import com.distributed.domain.Parameters;
//import com.distributed.domain.SaxData;
//import com.distributed.util.FileChannelReader;
//import com.distributed.util.FileChannelWriter;
//import com.distributed.util.FileUtil;
//
//import java.io.File;
//import java.io.IOException;
//import java.math.BigInteger;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//public class MergeSort {
//
//
//    private Map<FileChannelReader, byte[]> resByteMap = new HashMap<>(); // 记录reader上次没读完的数据
//    // 将1个buffer内的数据转化成多个Sax
//    public ArrayList<SaxData> createSaxes(byte[] arrays, FileChannelReader reader, int readSize) {
//        if (readSize < Parameters.saxDataSize * 2) {
//            throw new RuntimeException("read channel的buffer太小了，调大Parameters.MergeSort.readSize");
//        }
//        byte[] newArrays;
//        byte[] resByte = resByteMap.get(reader);
//        if (resByte != null) {  // 上一次没读完剩下的部分
//            newArrays = new byte[resByte.length + arrays.length];
//            System.arraycopy(resByte, 0, newArrays, 0, resByte.length);
//            System.arraycopy(arrays, 0, newArrays, resByte.length, arrays.length);
//        }
//        else {
//            newArrays = new byte[arrays.length];
//            System.arraycopy(arrays, 0, newArrays, 0, arrays.length);
//        }
//
//
//        String line = new String(newArrays, StandardCharsets.UTF_8);
//        String[] strSaxes = line.split(",");
//
//        ArrayList<SaxData> saxes = new ArrayList<>();
//
//        if (newArrays[newArrays.length - 1] == ",".getBytes()[0]) { // 这次结尾刚好有完整的sax统计
//            for (String strSax : strSaxes) {
//                String[] str = strSax.split("=");
//                saxes.add(new SaxData(str[0].getBytes(), Parameters.saxDataSize, Long.parseLong(str[1])));
//            }
//            resByteMap.put(reader, null);
//        }
//        else {
//            for (int i = 0; i < strSaxes.length - 1; i ++ ) {   // 不完整，跳过最后一个，留到下次
//                String[] str = strSaxes[i].split("=");
//                System.out.println("str " + Arrays.toString(str));
//                saxes.add(new SaxData(str[0].getBytes(), Parameters.saxDataSize, Long.parseLong(str[1])));
//            }
//            resByteMap.put(reader, strSaxes[strSaxes.length - 1].getBytes());
//        }
//
//        return saxes;
//    }
//
//    // 合并相同的sax统计(saxData)，并排序
//    public BigInteger mergeSort() throws IOException {
//        ArrayList<File> files = FileUtil.getAllFile(Parameters.MergeSort.saxStatisticFolder);
//        int K = files.size();  // k路归并
//        ArrayList<FileChannelReader> readers = new ArrayList<>();
//        for (File file: files) {
//            FileChannelReader reader = new FileChannelReader(file.getPath(), Parameters.MergeSort.readSize / K); // 每路取readSize的1/k的数据
//            readers.add(reader);
//        }
//
//        ArrayList<ArrayList<SaxData>> k_saxes = new ArrayList<>();
//        int[] index = new int[K];
//        for (FileChannelReader reader: readers) { // 取出k路数据
//            if (reader.read() != -1) {
//                byte[] arrays = reader.getArray();
//                k_saxes.add(createSaxes(arrays, reader, Parameters.MergeSort.readSize / K));
//            }
//            else {
//                throw new IOException(); // memorySortPath文件夹下没有可读取的内容
//            }
//        }
//
//        int numSaxStatisticFile = 0;
//        LinkedHashMap<String, Long> saxStatisticPerFile = new LinkedHashMap<>(); // 统计各种值的sax的个数
//        BigInteger totalSax = BigInteger.ZERO; // 总计sax总共有多少个
//        boolean readAllFile = false;
//        while(!readAllFile) {
//            SaxData min_saxData = null;
//            int min_saxData_k = 0;
//
//            readAllFile = true;
//            // todo 败者树优化 O(k) -> O(log(k))
//            for (int k = 0; k < k_saxes.size(); k ++ ) {
//                if (k_saxes.get(k) == null) {
//                    continue;
//                }
//                readAllFile = false;
//                SaxData saxData = k_saxes.get(k).get(index[k]);
//                if (min_saxData == null || min_saxData.compareTo(saxData) > 0) {
//                    min_saxData = saxData;
//                    min_saxData_k = k;
//                }
//            }
//            if (min_saxData != null) {
//                String str_min_sax = min_saxData.toString();
//                totalSax = totalSax.add(new BigInteger(String.valueOf(min_saxData.getCnt())));
//
//                if (saxStatisticPerFile.containsKey(str_min_sax)) { // 非首次出现，合并统计
//                    saxStatisticPerFile.put(str_min_sax, saxStatisticPerFile.get(str_min_sax) + min_saxData.getCnt());
//                }
//                else {  // 首次出现
//                    saxStatisticPerFile.put(str_min_sax, min_saxData.getCnt());
//                }
//                if (saxStatisticPerFile.size() > Parameters.MergeSort.numSaxCntPerFile) { // 写入sax统计文件
//                    saxStatisticPerFile.remove(str_min_sax);
//
//                    writeSaxStatisticFile(String.valueOf(numSaxStatisticFile), saxStatisticPerFile, Parameters.MergeSort.mergeSortSaxStatisticFolder);
//                    numSaxStatisticFile ++;
//                    saxStatisticPerFile = new LinkedHashMap<>();
//                    saxStatisticPerFile.put(str_min_sax,min_saxData.getCnt());  // 超过numSaxCntPerFile的那个saxData下次再写入，先暂存
//                }
//
//                index[min_saxData_k] ++ ;
//                if (index[min_saxData_k] >= k_saxes.get(min_saxData_k).size()) { // 判断第k路是否到末尾了
//                    index[min_saxData_k] = 0;
//                    FileChannelReader reader = readers.get(min_saxData_k); // 第k路读入下一组数据
//                    if (reader.read() != -1) {
//                        byte[] arrays = reader.getArray();
//                        k_saxes.set(min_saxData_k, createSaxes(arrays, reader, Parameters.MergeSort.readSize / K));
//                    }
//                    else {
//                        k_saxes.set(min_saxData_k, null);
//                    }
//                }
//            }
//        }
//        if (saxStatisticPerFile.size() > 0) { // 剩余部分写入文件
//            writeSaxStatisticFile(String.valueOf(numSaxStatisticFile), saxStatisticPerFile, Parameters.MergeSort.mergeSortSaxStatisticFolder);
//        }
//        return totalSax;
//    }
//
//    private void writeSaxStatisticFile(String fileName, HashMap<String, Long> cntSaxes, String path) throws IOException {
//        ArrayList<Byte> contentList = new ArrayList<>();
//        for (Map.Entry<String, Long> entry: cntSaxes.entrySet()) {
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
//        FileChannelWriter writer = new FileChannelWriter(path + fileName, Parameters.MergeSort.writeSize);
//        writer.write(content);
//        writer.close();
//    }
//}
