package ru.geekbrains.storage;


import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class FileInfo implements Serializable{
    public enum FileType{
        FILE("F"), DIRECTORY("D");
        FileType(String name) {
            this.name = name;
        }
        private String name;
        public String getName(){
            return name;
        }
    }

    private String fileName;
    private FileType type;
    private long size;
    private LocalDate lastModified;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDate getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDate lastModified) {
        this.lastModified = lastModified;
    }

    public FileInfo(Path path){
        try {
            this.fileName = path.getFileName().toString();
            this.type = Files.isDirectory(path)? FileType.DIRECTORY: FileType.FILE;
            this.size = this.type == FileType.DIRECTORY? -1L: Files.size(path);
            this.lastModified = LocalDate.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}