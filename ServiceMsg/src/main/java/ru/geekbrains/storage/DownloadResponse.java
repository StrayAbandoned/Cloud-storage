package ru.geekbrains.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DownloadResponse implements BasicResponse{

    ResponseType type = ResponseType.DOWNLOAD;
    File file;
    String filename;
    byte [] data;

    public DownloadResponse(File file) {
        this.file = file;
        this.filename = file.getName();
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseType getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }
}
