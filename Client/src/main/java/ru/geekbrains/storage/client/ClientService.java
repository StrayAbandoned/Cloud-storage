package ru.geekbrains.storage.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ClientService {

    private static RegController regController;
    private static MainController mainController;
    private static NameController nameController;
    private static Network network;
    private static String path;
    private static LogManager logManager = LogManager.getLogManager();
    private static Logger logger = Logger.getLogger(ClientService.class.getName());
    private static RenameController renameController;
    private static boolean serverMarker;

    static {

        try {
            logManager.readConfiguration(new FileInputStream("Client/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static Logger getLogger() {
        return logger;
    }

    public static void setNameController(NameController nameController) {
        ClientService.nameController = nameController;
    }

    public static void setrenameController(RenameController renameController) {
        ClientService.renameController = renameController;
    }

    public static boolean getServerMarker() {
        return serverMarker;
    }

    public static void setServerMarker(boolean serverMarker) {
        ClientService.serverMarker = serverMarker;
    }
}
