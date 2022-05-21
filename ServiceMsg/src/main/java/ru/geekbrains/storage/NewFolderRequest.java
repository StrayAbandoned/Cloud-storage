package ru.geekbrains.storage;

public class NewFolderRequest implements BasicRequest{
    RequestType type = RequestType.NEW_REMOTE_FOLDER;
    String name;

    public NewFolderRequest(String name) {
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
