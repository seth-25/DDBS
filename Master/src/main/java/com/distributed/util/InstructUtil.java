package com.distributed.util;

import com.distributed.domain.InstructInit;
import com.distributed.domain.InstructRun;

public class InstructUtil {
    public static InstructInit buildInstructInit(String instruction, Object obj) {

        InstructInit ins = new InstructInit(instruction);
        ins.setDataObject(obj);
        return ins;
    }

    public static InstructRun buildInstructRun(String instruction, Object obj) {

        InstructRun ins = new InstructRun(instruction);
        ins.setDataObject(obj);
        return ins;
    }
}
