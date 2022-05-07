package ru.geekbrains.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ru.geekbrains.storage.RenameRequest;

public class RenameController {

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
            String oldName = ClientService.getRemoteController().getOldName();
            if (ClientService.getRemoteController().getResolution() != null) {
                ClientService.getNetwork().sendFiles(new RenameRequest(oldName, name+ClientService.getRemoteController().getResolution()));
            } else {
                ClientService.getNetwork().sendFiles(new RenameRequest(oldName, name));
            }
            ClientService.getRemoteController().setResolution(null);
            ClientService.getRemoteController().getRenameStage().close();
        } else {
            if (ClientService.getLocalController().getResolution() == null) {
                ClientService.getLocalController().renameFolder(name);
            } else {
                ClientService.getLocalController().renameFile(name);
            }

        }
    }
}
