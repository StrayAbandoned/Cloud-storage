package ru.geekbrains.storage;

import java.io.File;
import java.util.List;

public class GetFilesRequest implements BasicRequest{
    private RequestType type = RequestType.GET_FILES;


    @Override
    public RequestType getType() {
        return type;
    }
}
