package ru.geekbrains.storage;

public class AuthRequest implements BasicRequest{
    private String login;
    private String password;
    private RequestType type;

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

    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
        setType(RequestType.AUTH);
    }

    @Override
    public RequestType getType() {
        return type;
    }
}
