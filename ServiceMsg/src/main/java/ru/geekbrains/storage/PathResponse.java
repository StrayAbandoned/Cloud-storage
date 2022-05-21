package ru.geekbrains.storage;

public class PathResponse implements BasicResponse{
    ResponseType type = ResponseType.PATH;
    String path;

    public PathResponse(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public ResponseType getType() {
        return type;
    }
}
