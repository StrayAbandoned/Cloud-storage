package ru.geekbrains.storage;

import java.io.File;
import java.util.List;

public class GetFilesRequest implements BasicRequest{
    private RequestType type = RequestType.GET_FILES;
    String path;

    public GetFilesRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public RequestType getType() {
        return type;
    }
}
