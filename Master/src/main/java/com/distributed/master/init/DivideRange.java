package com.distributed.master.init;

import com.distributed.domain.Parameters;
import javafx.util.Pair;

import java.util.*;


public class DivideRange {

    // 计算相似度
    private int calculateSimilarity(String a, String b) {
        assert a.length() == b.length();
        assert a.length() % Parameters.saxSimSize == 0;
        int d = 0;
        for (int i = 0; i < a.length(); i ++ ) {
            if (a.charAt(i) == b.charAt(i)) {
                if (i % Parameters.saxSimSize == 0) {
                    d ++ ;
                }
            }
            else
                break;
        }
        return d;
    }

    private byte[] addOne(byte[] bytes) {
        byte[] res = new byte[bytes.length];
        int index = bytes.length - 1;
        while(index > 0 && bytes[index] == (byte)0xff) {
            res[index] = (byte) (bytes[index] + 1);
            index --;
        }
        res[index] = (byte) (bytes[index] + 1);
        System.arraycopy(bytes, 0, res, 0, index);
        return res;
    }

    public ArrayList<Pair<byte[], byte[]>> divide(TreeMap<String, Long> sortSaxes) {
        long totalCnt = 0;
        for(Map.Entry<String,Long> entry: sortSaxes.entrySet()){
            totalCnt += entry.getValue();
        };

        long numSaxPerRanges = totalCnt / Parameters.numWorkerInit;
        ArrayList<Pair<byte[], byte[]>> rangePairs = new ArrayList<>();

        long cnt = 0, num = 0;
        Set<Map.Entry<String, Long>> entries = sortSaxes.entrySet();
        byte[] leftInterval = new byte[Parameters.saxDataSize];
        Arrays.fill(leftInterval, (byte) 0x00); // 第一台机器左区间最小

        for(Map.Entry<String,Long> entry: entries){
            cnt += entry.getValue();
            if (cnt >= numSaxPerRanges) {
                num ++ ;
                byte[] rightInterval = entry.getKey().getBytes();
                if (num >= Parameters.numWorkerInit) {  // 最后一台机器右区间最大
                    Arrays.fill(rightInterval, (byte) 0xff);
                }
                rangePairs.add(new Pair<>(leftInterval, rightInterval));
                cnt = cnt - numSaxPerRanges;
                leftInterval = addOne(rightInterval);   // 下个区间左端点比上个区间右端点大1
            }
        };
//        // 从相似度高往低开始枚举
//        for (int d = Parameters.saxDataSize / Parameters.saxSimSize; d > 0; d -- ) {
//            Set<Map.Entry<String, Long>> entries = sortSaxes.entrySet();
//            String leftInterval = entries.stream().findFirst().get().getKey();
//            for(Map.Entry<String,Long> entry: entries){
//                String rightInterval = entry.getKey();
//                int sim = calculateSimilarity(leftInterval, rightInterval);
//                if (sim >= d) {
//
//                }
//                else {
//                    leftInterval = rightInterval;
//                }
//            };
//        }
        return rangePairs;
    }
}
