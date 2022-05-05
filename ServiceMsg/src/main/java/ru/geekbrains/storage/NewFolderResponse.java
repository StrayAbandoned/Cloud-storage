package ru.geekbrains.storage;

public class NewFolderResponse implements BasicResponse{
    ResponseType type = ResponseType.NEW_FOLDER;

    @Override
    public ResponseType getType() {
        return type;
    }
}
