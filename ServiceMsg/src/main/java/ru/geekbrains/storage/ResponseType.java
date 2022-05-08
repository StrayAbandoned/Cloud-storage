package ru.geekbrains.storage;

import java.io.Serializable;

public enum ResponseType implements Serializable {
    REG_OK,
    REG_NO,
    AUTH_OK,
    AUTH_NO,
    GET_FILES,
    UPLOAD_OK,
    UPLOAD_NO, PATH, NEW_FOLDER, DOWNLOAD, CHANGE_OK, CHANGE_NO;
}
