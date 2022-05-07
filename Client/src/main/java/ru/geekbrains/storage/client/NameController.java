package ru.geekbrains.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.geekbrains.storage.NewFolderRequest;

public class NameController {
    private Controller controller;
    @FXML
    public TextField nameOfFile;
    public void setController(Controller controller) {
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
            ClientService.getRemoteController().getNameStage().close();


        } else{
            ClientService.getLocalController().createFolder(name);
        }

    }
}
