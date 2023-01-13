package common.domain;

import common.domain.protocol.Command;
import common.domain.protocol.Packet;

public class InstructRun extends Packet {
    private int instruction;
    private Object dataObject;

    public InstructRun(int instruction) {
        this.instruction = instruction;
    }

    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }

    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }

    public int getInstruction() {
        return instruction;
    }

    public Object getDataObject() {
        return dataObject;
    }

    @Override
    public Byte getCommand() {
        return Command.run;
    }
}
