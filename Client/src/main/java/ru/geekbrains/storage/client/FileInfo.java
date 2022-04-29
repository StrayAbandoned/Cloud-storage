package ru.geekbrains.storage.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo {
    public final static String back = "...BACK...";
    private String fileName;
    private long fileSize;

    public FileInfo(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public FileInfo(Path path) throws IOException {
        this.fileName = path.getFileName().toString();
        if (Files.isDirectory(path)) {
            this.fileSize = -1L;
        } else {
            this.fileSize = Files.size(path);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDirectory() {
        return this.fileSize == -1;
    }

    public boolean isBack() {
        return this.fileSize == -2;
    }

}
