package common.domain.protocol;


public interface Command {

    Byte init = 1;
    Byte run = 2;
    Byte adjust = 3;

}
