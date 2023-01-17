package common.domain;

public class MsgInsert {
    private int type;    // 指令类型
    private int length; // 消息多长
    private byte[] data;

    public MsgInsert(int type, int length, byte[] data) {
        this.type = type;
        this.length = length;
        this.data = data;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public int getLength() {
        return length;
    }

}
