package com.distributed.util;

import common.domain.FileData;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FileUtil {
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

        files.sort((o1, o2) -> {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o1.getName().compareTo(o2.getName());
        });

        return files;
    }


    public static void writeFile(String fileFolder, FileData fileData) throws IOException {

        File folder = new File(fileFolder);
        if (!folder.exists()) {
            boolean flag = folder.mkdir();
        }

        File file = new File(fileFolder + "/" + fileData.getFileName());

        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");//r: 只读模式 rw:读写模式
        randomAccessFile.seek(fileData.getBeginPos());      //移动文件记录指针的位置,
        randomAccessFile.write(fileData.getBytes());        //调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
        randomAccessFile.close();
    }
    
    public static void deleteFile(String fileFolder, FileData fileData) {
        File file = new File(fileFolder + "/" + fileData.getFileName());
        if (file.exists()) { // 有以前遗留的文件，删除后覆盖
            file.delete();
        }
    }
}
