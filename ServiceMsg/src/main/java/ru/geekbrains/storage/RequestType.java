package ru.geekbrains.storage;

import java.io.Serializable;

public enum RequestType implements Serializable {
    REG,
    AUTH,
    PATH,
    GET_FILES,
    UPLOAD,
    COPY_FILE,
    COPY_DIRECTORY,
    NEW_REMOTE_FOLDER,
    PASTE,
    DELETE,
    RENAME,
    DOWNLOAD,
    CHANGE_PASSWORD;
}
