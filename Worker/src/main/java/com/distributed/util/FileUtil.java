package com.distributed.util;

import common.setting.Constants;
import common.domain.FileData;
import common.domain.TimeSeries;
import common.util.TsUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FileUtil {
    private static final int readFileSize = 1024 * 100;

    // 获取文件夹下的所有文件
    public static ArrayList<File> getAllFile(String fileFolder) throws IOException {

        File[] childrenFiles = new File(fileFolder).listFiles();
        if (childrenFiles == null || childrenFiles.length == 0) { // 文件夹内要有文件
            throw new IOException();
        }

        ArrayList<File> files = new ArrayList<>();
        for (File childFile : childrenFiles) {
            // 如果是文件，直接添加到结果集合
            if (childFile.isFile()) {
                files.add(childFile);
            }
        }
        return files;
    }

    public static FileData readFile(String filePath, String fileType, long readPosition) throws IOException {
        File file = new File(filePath);
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");//r: 只读模式 rw:读写模式
        randomAccessFile.seek(readPosition);
        byte[] bytes = new byte[readFileSize];
        int readSize = randomAccessFile.read(bytes);
        if (readSize <= 0) {
            randomAccessFile.close();
            return new FileData(file.getAbsolutePath(), file.getName(), fileType, file.length(), Constants.FileStatus.COMPLETE);
        }
        FileData fileData = new FileData(file.getAbsolutePath(), file.getName(), fileType, file.length(), Constants.FileStatus.CENTER);
        if (readPosition == 0) {
            fileData.setStatus(Constants.FileStatus.BEGIN);
        }

        fileData.setBeginPos(readPosition);
        fileData.setEndPos(readPosition + readSize - 1);
        //去掉空字节
        if (readSize < readFileSize) {
            byte[] copy = new byte[readSize];
            System.arraycopy(bytes, 0, copy, 0, readSize);
            fileData.setBytes(copy);
            fileData.setStatus(Constants.FileStatus.END);
        } else {
            fileData.setBytes(bytes);
        }
        randomAccessFile.close();
        return fileData;
    }

    public static void writeFile(String fileFolder, FileData fileData) throws IOException {
        File file = new File(fileFolder + "/" + fileData.getFileName());

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");//r: 只读模式 rw:读写模式
        randomAccessFile.seek(fileData.getBeginPos());      //移动文件记录指针的位置,
        randomAccessFile.write(fileData.getBytes());        //调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
        randomAccessFile.close();
    }

    public static long writeFile(String fileFolder, TimeSeries timeSeries) throws IOException {
        File folder = new File(fileFolder);
        if (!folder.exists()) {
            boolean flag = folder.mkdir();
        }
        File file = new File(fileFolder + "/" + TsUtil.computeHash(timeSeries));

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");//r: 只读模式 rw:读写模式
        long pos = randomAccessFile.length();
        randomAccessFile.seek(pos);      //移动文件记录指针的位置,
        randomAccessFile.write(timeSeries.getTimeSeriesData());        //调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
        randomAccessFile.seek(pos + timeSeries.getTimeSeriesData().length);
        randomAccessFile.write(timeSeries.getTimeStamp());
        randomAccessFile.close();
        return pos;
    }

    public static void deleteFile(String fileFolder, FileData fileData) {
        File file = new File(fileFolder + "/" + fileData.getFileName());
        if (file.exists()) { // 有以前遗留的文件，删除后覆盖
            file.delete();
        }
    }
}
