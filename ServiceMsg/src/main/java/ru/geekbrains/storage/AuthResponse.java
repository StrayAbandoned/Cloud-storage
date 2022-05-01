package ru.geekbrains.storage;

import java.nio.file.Path;

public class AuthResponse implements BasicResponse {
    private ResponseType type;
    Path path;

    public AuthResponse(ResponseType type, Path path) {
        this.path = path;
        this.type = type;
    }

    public AuthResponse(ResponseType type) {
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public ResponseType getType() {
        return type;
    }
}
