package ru.geekbrains.storage.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.geekbrains.storage.CopyRequest;
import ru.geekbrains.storage.NewFolderRequest;
import ru.geekbrains.storage.RegRequest;
import ru.geekbrains.storage.RequestType;

import java.nio.file.Path;

public class NameController {
    private MainController controller;
    @FXML
    public TextField nameOfFile;
    public void setController(MainController controller) {
        this.controller = controller;
    }
    public void confirm(ActionEvent actionEvent) {
        String name = nameOfFile.getText().trim();
        nameOfFile.clear();
        if (name.isBlank()) {
            return;
        }
        if (ClientService.getServerMarker()) {
            ClientService.setServerMarker(false);
            ClientService.getNetwork().sendFiles(new NewFolderRequest(name));
            ClientService.getMainController().getNameStage().close();


        } else{
            ClientService.getMainController().createFolder(name);
        }


    }
}
