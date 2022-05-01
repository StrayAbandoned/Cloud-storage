package ru.geekbrains.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class UploadRequest implements BasicRequest{
    RequestType type = RequestType.UPLOAD;
    File file;
    String filename;
    byte [] data;
    String remPath;

    public UploadRequest(File file, String remPath) {
        this.remPath =remPath;
        this.file=file;
        this.filename = file.getName();
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public RequestType getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public File getFile() {
        return file;
    }

    public String getRemPath() {
        return remPath;
    }
}
