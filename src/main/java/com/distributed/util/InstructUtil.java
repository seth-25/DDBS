package com.distributed.util;

import com.distributed.domain.InstructInit;

public class InstructUtil {
    public static InstructInit buildInstruction(String instruction, Object obj) {

        InstructInit ins = new InstructInit(instruction);
        ins.setDataObject(obj);
        return ins;
    }
}
