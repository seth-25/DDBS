package com.distributed.client.util;

import common.setting.Parameters;
import common.util.TsUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

public class TsUtilTest {
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
