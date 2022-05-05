package ru.geekbrains.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.geekbrains.storage.NewFolderRequest;
import ru.geekbrains.storage.RenameRequest;
import ru.geekbrains.storage.RequestType;

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
        }
        if (ClientService.getServerMarker()) {
            ClientService.setServerMarker(false);
            String oldName = ClientService.getMainController().getOldName();
            if (ClientService.getMainController().getResolution() != null) {
                ClientService.getNetwork().sendFiles(new RenameRequest(oldName, name+ClientService.getMainController().getResolution()));
            } else {
                ClientService.getNetwork().sendFiles(new RenameRequest(oldName, name));
            }
            ClientService.getMainController().setResolution(null);
            ClientService.getMainController().getRenameStage().close();
        } else {
            if (ClientService.getMainController().getResolution() == null) {
                ClientService.getMainController().renameFolder(name);
            } else {
                ClientService.getMainController().renameFile(name);
            }

        }
    }
}
