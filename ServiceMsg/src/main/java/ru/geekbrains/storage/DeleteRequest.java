package ru.geekbrains.storage;

public class DeleteRequest implements BasicRequest{

    RequestType type = RequestType.DELETE;
    String fileName;

    public DeleteRequest(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public RequestType getType() {
        return type;
    }
}
