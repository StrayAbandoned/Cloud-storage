package ru.geekbrains.storage;

public class LogOutRequest implements BasicRequest{

    private RequestType type = RequestType.LOGOUT;


    @Override
    public RequestType getType() {
        return type;
    }
}
