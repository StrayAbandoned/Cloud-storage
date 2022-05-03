package ru.geekbrains.storage.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.geekbrains.storage.RegRequest;

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
        } else {
            ClientService.getMainController().createFolder(name);
        }
    }
}
