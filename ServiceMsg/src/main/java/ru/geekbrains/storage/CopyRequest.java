package ru.geekbrains.storage;

public class CopyRequest implements BasicRequest{
    RequestType type;
    String name;

    public CopyRequest(String name, RequestType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public RequestType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
