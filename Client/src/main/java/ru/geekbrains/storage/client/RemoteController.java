package ru.geekbrains.storage.client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.geekbrains.storage.*;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;


public class RemoteController implements Initializable, Controller {
    @FXML
    private Button download;
    @FXML
    private ContextMenu context;
    @FXML
    private TableView<FileInfo> remotefiles;
    @FXML
    private TextField remotePath;

    private Network network;

    private String oldName, resolution;
    private Stage  nameStage, renameStage;
    private RenameController renameController;
    private NameController nameController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ClientService.setRemoteController(this);
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(120);
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");

                    } else {
                        String text = String.format("%,d bytes", item);
                        if (item == -1) {
                            text = "";
                        }
                        setText(text);

                    }
                }
            };
        });

        TableColumn<FileInfo, String> fileDateColumn = new TableColumn<>("Last Modified");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        fileDateColumn.setPrefWidth(120);

        remotefiles.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        remotefiles.getSortOrder().add(fileTypeColumn);
        remotefiles.setContextMenu(context);

        showRemoteFiles(new CopyOnWriteArrayList<FileInfo>(), null);

    }


    @FXML
    private void back(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        network.sendFiles(new PathRequest("BACK"));
    }

    @FXML
    private void downloadFile(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        FileInfo fileinfo = remotefiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null && !fileinfo.isDirectory()){
            network.sendFiles(new DownloadRequest(fileinfo.getFileName()));
        }
    }

    public void showRemoteFiles(List<FileInfo> files, String path) {
        remotePath.setText(path);
        remotefiles.getItems().clear();
        remotefiles.getItems().addAll(files);
        remotefiles.sort();
    }

    @FXML
    private void copy(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        FileInfo fileinfo = remotefiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null) {
            ClientService.setServerMarker(true);
            if (fileinfo.isDirectory()) {
                network.sendFiles(new CopyRequest(fileinfo.getFileName(), RequestType.COPY_DIRECTORY));
                ClientService.getLogger().info("COPY_DIR");
            } else {
                network.sendFiles(new CopyRequest(fileinfo.getFileName(), RequestType.COPY_FILE));
                ClientService.getLogger().info("COPY_File");
            }
        }
    }


    @FXML
    private void paste(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        network.sendFiles(new PasteRequest());
    }

    @FXML
    private void delete(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        FileInfo fileinfo = remotefiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null) {
            network.sendFiles(new DeleteRequest(fileinfo.getFileName()));
        }
    }

    @FXML
    public void rename(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        FileInfo fileinfo = remotefiles.getSelectionModel().getSelectedItem();
        oldName = fileinfo.getFileName();
        ClientService.setServerMarker(true);
        if (fileinfo != null) {
            if (fileinfo.isDirectory()) {
                resolution = null;
            } else {
                resolution = fileinfo.getFileName().substring(fileinfo.getFileName().lastIndexOf('.'));
            }
            if (renameStage == null) {
                createRenameWindow();
                ClientService.setRenameController(renameController);
            }
            renameStage.show();
        }
    }

    private void createRenameWindow() {
        try {
            FXMLLoader fxmlLoader3 = new FXMLLoader(ClientApp.class.getResource("rename.fxml"));
            Parent root = fxmlLoader3.load();
            renameStage = new Stage();
            renameStage.setTitle("Enter the name of file or directory");
            renameStage.setScene(new Scene(root, 400, 100));
            renameStage.initModality(Modality.APPLICATION_MODAL);
            renameStage.initStyle(StageStyle.UTILITY);
            renameController = fxmlLoader3.getController();
            renameController.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createNewDirectory(ActionEvent actionEvent) {
        network = ClientService.getNetwork();
        ClientService.setServerMarker(true);
        if (nameStage == null) {
            createNameWindow();
            ClientService.setNameController(nameController);
        }
        nameStage.show();
    }

    private void createNameWindow() {
        try {
            FXMLLoader fxmlLoader2 = new FXMLLoader(ClientApp.class.getResource("name.fxml"));
            Parent root = fxmlLoader2.load();
            nameStage = new Stage();
            nameStage.setTitle("Enter the name of new directory");
            nameStage.setScene(new Scene(root, 400, 100));
            nameStage.initModality(Modality.APPLICATION_MODAL);
            nameStage.initStyle(StageStyle.UTILITY);
            nameController = fxmlLoader2.getController();
            nameController.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDirectory(MouseEvent mouseEvent) {
        FileInfo fileInfo = remotefiles.getSelectionModel().getSelectedItem();
        ClientService.getLocalController().getUpload().setDisable(true);
        network = ClientService.getNetwork();
        if(fileInfo!=null&&!fileInfo.isDirectory()){
            download.setDisable(false);
        } else download.setDisable(true);
        if (mouseEvent.getClickCount() == 2 && fileInfo != null) {
            if (fileInfo.isDirectory()) {
                network.sendFiles(new PathRequest(fileInfo.getFileName()));
            }

        }
    }

    public String getOldName() {
        return oldName;
    }


    public String getResolution() {
        return resolution;
    }

    public Stage getNameStage() {
        return nameStage;
    }

    public Stage getRenameStage() {
        return renameStage;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Button getDownload() {
        return download;
    }
}
