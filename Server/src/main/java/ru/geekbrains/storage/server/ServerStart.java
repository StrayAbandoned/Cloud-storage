package ru.geekbrains.storage.server;

import java.io.IOException;

public class ServerStart {
    public static void main(String[] args) {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
