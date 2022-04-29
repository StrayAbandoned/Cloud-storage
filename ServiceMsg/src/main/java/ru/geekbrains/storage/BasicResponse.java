package ru.geekbrains.storage;

import ru.geekbrains.storage.RequestType;

import java.io.Serializable;

public interface BasicResponse extends Serializable {
    ResponseType getType();
}
