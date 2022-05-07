package ru.geekbrains.storage;

public class RenameRequest implements BasicRequest{
    RequestType type = RequestType.RENAME;
    String oldName;
    String newName;

    public RenameRequest(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    @Override
    public RequestType getType() {
        return type;
    }
}
