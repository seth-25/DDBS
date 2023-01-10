package common.domain;

public class FileInfo {
    private int status;       //Constants.FileStatus
    private String filePath = null; //文件路径
    private String fileType = null;
    private long readPosition = -1; //读取位置

    public FileInfo(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getReadPosition() {
        return readPosition;
    }

    public void setReadPosition(long readPosition) {
        this.readPosition = readPosition;
    }
}