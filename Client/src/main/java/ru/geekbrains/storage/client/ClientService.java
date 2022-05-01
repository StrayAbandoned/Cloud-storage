package ru.geekbrains.storage.client;

import java.nio.file.Path;

public class ClientService {
    private static RegController regController;
    private static MainController mainController;
    private static Network network;
    private static String path;

    public static RegController getRegController() {
        return regController;
    }

    public static void setRegController(RegController regController) {
        ClientService.regController = regController;
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static void setMainController(MainController mainController) {
        ClientService.mainController = mainController;
    }

    public static Network getNetwork() {
        return network;
    }

    public static void setNetwork(Network network) {
        ClientService.network = network;
    }

    public static void setPath(String path) {
        ClientService.path = path;
    }

    public static String getPath() {
        return path;
    }
}
