package ru.geekbrains.storage;

import java.nio.file.Path;

public class ChangePasswordResponse implements BasicResponse {
    private ResponseType type;

    public ChangePasswordResponse(ResponseType type) {
        this.type = type;
    }


    @Override
    public ResponseType getType() {
        return type;
    }
}
