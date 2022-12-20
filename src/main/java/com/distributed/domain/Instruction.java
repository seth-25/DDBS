package com.distributed.domain;

public class Instruction {
    private String instruction;
    private String hostName;

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getHostName() {
        return hostName;
    }
}
