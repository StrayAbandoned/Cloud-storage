package ru.geekbrains.storage;

public class DownloadRequest implements BasicRequest{
    RequestType type = RequestType.DOWNLOAD;
    String name;

    public DownloadRequest(String name) {
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
