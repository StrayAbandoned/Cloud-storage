package ru.geekbrains.storage;


public class RegRequest implements BasicRequest {
    private String login;
    private String password;
    private RequestType type;

    public RegRequest(String login, String password) {
        this.login = login;
        this.password = password;
        setType(RequestType.REG);
    }

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

    @Override
    public RequestType getType() {
        return RequestType.REG;
    }
}
