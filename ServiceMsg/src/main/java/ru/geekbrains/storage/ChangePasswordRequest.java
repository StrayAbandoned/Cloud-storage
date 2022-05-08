package ru.geekbrains.storage;

public class ChangePasswordRequest implements BasicRequest{
    private String login;
    private String password;
    private RequestType type = RequestType.CHANGE_PASSWORD;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public ChangePasswordRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public RequestType getType() {
        return type;
    }
}
