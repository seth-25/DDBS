package com.distributed.util;

import com.distributed.domain.InstructInit;

public class InstructUtil {
    public static InstructInit buildInstruction(String instruction, String hostName) {

        InstructInit ins = new InstructInit(instruction);
        ins.setDataObject(hostName);
        return ins;
    }

    public static InstructInit buildInstruction(String instruction, String[] hostNames) {
        InstructInit ins = new InstructInit(instruction);
        ins.setDataObject(hostNames);
        return ins;
    }
}
