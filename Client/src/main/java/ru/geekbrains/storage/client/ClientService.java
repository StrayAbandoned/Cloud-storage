package ru.geekbrains.storage.client;

public class ClientService {
    private static RegController regController;
    private static MainController mainController;
    private static Network network;

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
}
