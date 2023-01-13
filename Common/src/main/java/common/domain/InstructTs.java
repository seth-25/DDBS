package common.domain;

public class InstructTs{
    private int instruction;
    private Object dataObject;

    public InstructTs(int instruction) {
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
}

