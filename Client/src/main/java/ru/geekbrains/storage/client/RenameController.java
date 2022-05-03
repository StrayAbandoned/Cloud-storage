package ru.geekbrains.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class RenameController {


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
            if(ClientService.getMainController().getResolution()==null){
                ClientService.getMainController().renameFolder(name);
            }
            else {
                ClientService.getMainController().renameFile(name);
            }

        }
    }
}
