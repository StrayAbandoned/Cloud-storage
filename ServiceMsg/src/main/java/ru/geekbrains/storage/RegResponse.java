package ru.geekbrains.storage;

public class RegResponse implements BasicResponse{
    private ResponseType type;

    public RegResponse(ResponseType type){
        this.type = type;
    }
    @Override
    public ResponseType getType() {
        return type;
    }
}
