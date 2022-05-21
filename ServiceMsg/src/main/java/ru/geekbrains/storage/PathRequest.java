package ru.geekbrains.storage;

public class PathRequest implements BasicRequest{
    RequestType type = RequestType.PATH;
    String name;

    public PathRequest() {
    }

    public PathRequest(String name) {
        this.name = name;
    }

    @Override
    public RequestType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
