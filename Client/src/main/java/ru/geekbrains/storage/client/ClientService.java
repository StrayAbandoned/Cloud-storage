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
    private static LocalController localController;
    private static RemoteController remoteController;
    private static SettingController settingController;
    private static Network network;
    private static String login;
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

    public static LocalController getLocalController() {
        return localController;
    }

    public static void setLocalController(LocalController localController) {
        ClientService.localController = localController;
    }

    public static RemoteController getRemoteController() {
        return remoteController;
    }

    public static void setRemoteController(RemoteController remoteController) {
        ClientService.remoteController = remoteController;
    }

    public static SettingController getSettingController() {
        return settingController;
    }

    public static void setSettingController(SettingController settingController) {
        ClientService.settingController = settingController;
    }

    public static String getLogin() {
        return login;
    }

    public static void setLogin(String login) {
        ClientService.login = login;
    }
}
