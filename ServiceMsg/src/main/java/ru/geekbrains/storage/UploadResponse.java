package ru.geekbrains.storage;

public class UploadResponse implements BasicResponse{
    ResponseType type;

    public UploadResponse(ResponseType type) {
        this.type = type;
    }

    @Override
    public ResponseType getType() {
        return type;
    }
}
