package common.util;

import common.domain.InstructInit;
import common.domain.InstructRun;
import common.domain.InstructTs;

public class InstructUtil {
    public static InstructInit buildInstructInit(int instruction, Object obj) {

        InstructInit ins = new InstructInit(instruction);
        ins.setDataObject(obj);
        return ins;
    }

    public static InstructTs buildInstructTs(int instruction, Object obj) {

        InstructTs ins = new InstructTs(instruction);
        ins.setDataObject(obj);
        return ins;
    }

    public static InstructRun buildInstructRun(int instruction, Object obj) {

        InstructRun ins = new InstructRun(instruction);
        ins.setDataObject(obj);
        return ins;
    }
}
