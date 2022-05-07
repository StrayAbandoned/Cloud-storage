package ru.geekbrains.storage;

import java.nio.file.Path;
import java.util.List;

public class GetFilesResponse implements BasicResponse{
    private ResponseType type;
    private List<FileInfo> files;
    private String path;

    public GetFilesResponse(List<FileInfo> files) {
        this.files = files;
        setType(ResponseType.GET_FILES);
    }

    public GetFilesResponse(List<FileInfo> files, String path) {
        this.files = files;
        this.path = path;
        setType(ResponseType.GET_FILES);
    }

    @Override
    public ResponseType getType() {
        return type;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public String getPath() {
        return path;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }
}
