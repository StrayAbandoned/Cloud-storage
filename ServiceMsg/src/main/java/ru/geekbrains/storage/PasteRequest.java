package ru.geekbrains.storage;

public class PasteRequest implements BasicRequest{
    RequestType type = RequestType.PASTE;


    @Override
    public RequestType getType() {
        return type;
    }


}


