package com.distributed.domain;

import com.distributed.domain.protocol.Command;
import com.distributed.domain.protocol.Packet;

public class InstructTs{
    private String instruction;
    private Object dataObject;

    public InstructTs(String instruction) {
        this.instruction = instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }

    public String getInstruction() {
        return instruction;
    }

    public Object getDataObject() {
        return dataObject;
    }
}
