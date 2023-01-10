package com.distributed.util;

import com.distributed.domain.*;

public class FileMsgUtil {

//    public static MyMessage buildInstruction(String instruction, String hostName) {
//
//        Instruction ins = new Instruction();
//        ins.setInstruction(instruction);
//        ins.setHostName(hostName);
//
//        MyMessage myMessage = new MyMessage();
//        myMessage.setType(Constants.TransferType.INSTRUCT);
//        myMessage.setDataObject(ins);
//
//        return myMessage;
//    }

    public static FileMessage buildFileRequest(String filePath, String fileName, String fileType, long fileSize) {

        FileData fileData = new FileData(filePath, fileName, fileType, fileSize, Constants.FileStatus.BEGIN);

        FileMessage fileMessage = new FileMessage();
        fileMessage.setStep(Constants.TransferStep.FILE_REQUEST);
        fileMessage.setDataObject(fileData);

        return fileMessage;
    }

    public static FileMessage buildFileResponse(FileInfo fileInfo) {

        FileMessage fileMessage = new FileMessage();
        fileMessage.setStep(Constants.TransferStep.FILE_RESPONSE);
        fileMessage.setDataObject(fileInfo);

        return fileMessage;
    }

}
