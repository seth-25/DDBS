package com.distributed.client.util;

import common.setting.Constants;
import common.setting.Parameters;
import common.util.TsUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

public class TsUtilTest {

    @Test public void myTest() {
        Integer integer = 25;
        System.out.println(integer.byteValue());
        byte[] t = new byte[0];
        ByteBuf out = Unpooled.buffer(100);
        out.writeBytes(t);
        System.out.println(out.readableBytes());
        out.readBytes(t);
    }
    @Test
    public void testByteSpeed() {
        int num = 1000000;
//        ArrayList<TimeSeries> tsList = new ArrayList<>();
        ByteBuf tsList = Unpooled.buffer(num * Parameters.tsSize);
        tsList.writeByte(Constants.InstructionType.SEND_TS);
        long t = 0, allT1 = 0, allT2 = 0;

        for (int i = 0; i < num; i ++ ) {
            byte[] ts = new byte[Parameters.tsSize];
            t = System.currentTimeMillis();
            tsList.writeBytes(ts);
            allT1 += System.currentTimeMillis() - t;
        }


        byte[] tsList2 = new byte[num * Parameters.tsSize];
        byte[] tsListCopy = new byte[num * Parameters.tsSize];
        for (int i = 0; i < num; i ++ ) {
            byte[] ts = new byte[Parameters.tsSize];
            t = System.currentTimeMillis();
            System.arraycopy(ts, 0, tsList2, i * Parameters.tsSize, Parameters.tsSize);
            allT2 += System.currentTimeMillis() - t;
        }

        t = System.currentTimeMillis();
        System.arraycopy(tsList2, 0, tsListCopy, 0, num * Parameters.tsSize);
        allT2 += System.currentTimeMillis() - t;
        System.out.println(allT1 + " " + allT2);
    }

    @Test
    public void testByteSpeed2() {
        int num = 1000000;
//        ArrayList<TimeSeries> tsList = new ArrayList<>();
        ByteBuf tsList = Unpooled.buffer(num * Parameters.tsSize);
        ByteBuf tsListCopy = Unpooled.buffer(num * Parameters.tsSize);
        tsList.writeByte(Constants.InstructionType.SEND_TS);
        long t = 0, allT1 = 0, allT2 = 0;

        for (int i = 0; i < num; i ++ ) {
            byte[] ts = new byte[Parameters.tsSize];
            t = System.currentTimeMillis();
            tsList.writeBytes(ts);
            allT1 += System.currentTimeMillis() - t;
        }
        t = System.currentTimeMillis();
//        tsListCopy.writeBytes(tsList, 0, num*Parameters.tsSize);
        allT1 += System.currentTimeMillis() - t;

        int cnt = 0;
        byte[] tsList2 = new byte[num * Parameters.tsSize];
        ByteBuf tsList2Copy = Unpooled.buffer(num * Parameters.tsSize);
        for (int i = 0; i < num; i ++ ) {
            byte[] ts = new byte[Parameters.tsSize];
            t = System.currentTimeMillis();
            System.arraycopy(ts, 0, tsList2, i * Parameters.tsSize, Parameters.tsSize);
            cnt ++;
            allT2 += System.currentTimeMillis() - t;
        }

        t = System.currentTimeMillis();
        tsList2Copy.writeBytes(tsList2);
        allT2 += System.currentTimeMillis() - t;
        System.out.println(allT1 + " " + allT2);
    }

    @Test
    public void testLongByteSpeed() {
        int num = 10000000;
//        ArrayList<TimeSeries> tsList = new ArrayList<>();
        ByteBuf tsList = Unpooled.buffer(num * 8);
        long t, writeT1 = 0, writeT2 = 0;


        for (int i = 0; i < num; i ++ ) {
            t = System.currentTimeMillis();
            tsList.writeLong(i);
            writeT1 += System.currentTimeMillis() - t;
        }
        byte[] tsList2 = new byte[num * 8];
        for (int i = 0; i < num; i ++ ) {
            t = System.currentTimeMillis();
            byte[] tl = TsUtil.longToBytes(i);
            System.arraycopy(tl, 0, tsList2, i * 8, 8);
            writeT2 += System.currentTimeMillis() - t;
        }
        System.out.println(writeT1 + " " + writeT2);
        ////////////////////////////////////////////////////////////////////////

        long readT1 = 0, readT2 = 0;
        for (int i = 0; i < num; i ++ ) {
            t = System.currentTimeMillis();
            long tl1 = tsList.readLong();
//            long tl1 = tsList.getLong(i * 8);
            writeT1 += System.currentTimeMillis() - t;
//            System.out.println(tl1);
        }
        for (int i = 0; i < num; i ++ ) {
            t = System.currentTimeMillis();
            byte[] tl = new byte[8];
            System.arraycopy(tsList2, i * 8, tl ,0, 8);
            long tl2 = TsUtil.bytesToLong(tl);
            writeT2 += System.currentTimeMillis() - t;
        }
        System.out.println(writeT1 + " " + writeT2);
    }


    public static void longPrint(long a){//将 int 按位从左到右打印
        int count = 0;
        for(int i = 63; i >= 0; i --){
            System.out.print((a >> i) & 1);
            count ++;
            if(count == 4){//每四位为一组，用空格分开
                System.out.print(" ");
                count = 0;
            }
        }
        System.out.println();
    }
    public static void bytePrint(byte a){//将 byte 按位从左到右打印
        int count = 0;
        for(int i = 7; i >= 0; i --){
            System.out.print((a >> i) & 1);
            count ++;
            if(count == 4){ //每四位为一组，用空格分开
                System.out.print(" ");
                count = 0;
            }
        }
        System.out.println();
    }

    @Test
    public void longAndByte() {
        long timeStamp = new Date().getTime()/1000;
        byte[] b = TsUtil.longToBytes(timeStamp);
        System.out.println(timeStamp);
        longPrint(timeStamp);
        System.out.println(Arrays.toString(b));
        for (int i = 0; i < Parameters.timeStampSize; i ++ ) {
            bytePrint(b[i]);
        }
        System.out.println(TsUtil.bytesToLong(b));
    }
}
