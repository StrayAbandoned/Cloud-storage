package ru.geekbrains.storage;

import java.io.Serializable;

public interface BasicRequest extends Serializable {

    RequestType getType();
}
