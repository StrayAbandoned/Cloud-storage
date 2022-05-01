package ru.geekbrains.storage;

import java.io.Serializable;

public enum RequestType implements Serializable {
    REG,
    AUTH,
    PATH,
    GET_FILES,
    UPLOAD;
}
