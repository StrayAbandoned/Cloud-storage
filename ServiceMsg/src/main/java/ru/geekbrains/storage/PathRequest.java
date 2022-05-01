package ru.geekbrains.storage;

public class PathRequest implements BasicRequest{
    RequestType type = RequestType.PATH;

    @Override
    public RequestType getType() {
        return type;
    }
}
