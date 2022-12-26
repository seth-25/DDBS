package com.distributed.domain.protocol;


public interface Command {

    Byte init = 1;
    Byte run = 2;
    Byte balance = 3;

}
