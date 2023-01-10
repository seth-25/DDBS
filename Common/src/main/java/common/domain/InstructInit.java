package common.domain;

import common.domain.protocol.Command;
import common.domain.protocol.Packet;

public class InstructInit extends Packet {
    private String instruction;
    private Object dataObject;

    public InstructInit(String instruction) {
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

    @Override
    public Byte getCommand() {
        return Command.init;
    }
}
