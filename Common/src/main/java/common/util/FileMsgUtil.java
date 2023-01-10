package common.util;

import common.domain.FileData;
import common.domain.FileInfo;
import common.domain.FileMessage;
import common.setting.Constants;

public class FileMsgUtil {

    public static FileMessage buildFileRequest(String filePath, String fileName, String fileType, long fileSize) {

        FileData fileData = new FileData(filePath, fileName, fileType, fileSize, Constants.FileStatus.BEGIN);

        FileMessage fileMessage = new FileMessage();
        fileMessage.setStep(Constants.TransferStep.FILE_REQUEST);
        fileMessage.setDataObject(fileData);

        return fileMessage;
    }

    public static FileMessage buildFileData(FileData fileData) {
        FileMessage fileMessage = new FileMessage();
        fileMessage.setStep(Constants.TransferStep.FILE_DATA);
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
