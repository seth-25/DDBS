package com.distributed.util;

import com.distributed.domain.*;

public class MsgUtil {

    public static MyMessage buildFileRequest(String filePath, String fileName, String fileType, long fileSize) {

        FileData fileData = new FileData(filePath, fileName, fileType, fileSize, Constants.FileStatus.BEGIN);

        MyMessage myMessage = new MyMessage();
        myMessage.setType(Constants.TransferType.FILE);
        myMessage.setStep(Constants.TransferStep.FILE_REQUEST);
        myMessage.setDataObject(fileData);

        return myMessage;
    }

    public static MyMessage buildFileData(FileData fileData) {
        MyMessage myMessage = new MyMessage();
        myMessage.setType(Constants.TransferType.FILE);
        myMessage.setStep(Constants.TransferStep.FILE_DATA);
        myMessage.setDataObject(fileData);

        return myMessage;
    }

    public static MyMessage buildFileResponse(FileInfo fileInfo) {

        MyMessage myMessage = new MyMessage();
        myMessage.setType(Constants.TransferType.FILE);
        myMessage.setStep(Constants.TransferStep.FILE_RESPONSE);
        myMessage.setDataObject(fileInfo);

        return myMessage;
    }
}
