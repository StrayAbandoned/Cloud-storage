package ru.geekbrains.storage.client;

import javafx.application.Platform;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;
import ru.geekbrains.storage.FileInfo;
import ru.geekbrains.storage.UploadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LocalController implements Initializable, Controller {
    @FXML
    private TableView<FileInfo> localfiles;
    @FXML
    private ComboBox<String> disks;
    @FXML
    private TextField localPath;
    @FXML
    private ContextMenu context;

    private String resolution;
    private File fileForCopy;
    private File directoryForCopy;
    private Path root = Paths.get(".");
    private Stage nameStage, renameStage;
    private RenameController renameController;
    private NameController nameController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ClientService.setLocalController(this);

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


        localfiles.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        localfiles.getSortOrder().add(fileTypeColumn);
        disks.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            disks.getItems().add(p.toString());
        }
        disks.getSelectionModel().select(0);


        getListOfFiles(root);
        localfiles.setContextMenu(context);

    }

    public void getListOfFiles(Path rootPath) {

        try {
            localPath.setText(rootPath.normalize().toAbsolutePath().toString());
            root = Paths.get(localPath.getText());
            localfiles.getItems().clear();
            localfiles.getItems().addAll(Files.list(rootPath).map(FileInfo::new).collect(Collectors.toList()));
            localfiles.sort();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Unable to update filelist", ButtonType.OK);
            alert.showAndWait();
        }

    }

    public void back(ActionEvent actionEvent) {
        Path upper = Paths.get(localPath.getText()).getParent();
        if (upper != null) {
            getListOfFiles(upper);
        }

    }


    public void chooseDisk(ActionEvent actionEvent) {
        getListOfFiles(Paths.get(disks.getSelectionModel().getSelectedItem()));
    }

    public void copy(ActionEvent actionEvent) {
        FileInfo fileinfo = localfiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null) {

            if (!Files.isDirectory(new File(localPath.getText(), fileinfo.getFileName()).toPath())) {
                fileForCopy = new File(String.valueOf(root), fileinfo.getFileName());
                directoryForCopy = null;
            } else {
                directoryForCopy = new File(String.valueOf(root), fileinfo.getFileName());
                fileForCopy = null;
            }
        }
        System.out.println(directoryForCopy);
    }

    public void goToDirectory(MouseEvent mouseEvent) {
        FileInfo fileInfo = localfiles.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2) {
            if (fileInfo.getType().getName().equals("D")) {
                getListOfFiles(Paths.get(localPath.getText()).resolve(fileInfo.getFileName()));
            }
        }
    }

    public void paste(ActionEvent actionEvent) {
        if (fileForCopy != null) {
            try {
                FileUtils.copyFile(fileForCopy, new File(String.valueOf(root), fileForCopy.getName()));
                fileForCopy = null;
                getListOfFiles(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (directoryForCopy != null) {
            try {
                FileUtils.copyDirectory(directoryForCopy, new File(String.valueOf(root), directoryForCopy.getName()));
                directoryForCopy = null;
                getListOfFiles(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(ActionEvent actionEvent) {
        FileInfo fileinfo = localfiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null) {
            FileUtils.deleteQuietly(new File(String.valueOf(root), fileinfo.getFileName()));
            getListOfFiles(root);
        }
    }

    public void rename(ActionEvent actionEvent) {
        FileInfo fileinfo = localfiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null) {

            if (fileinfo.getType().getName().equals("D")) {
                resolution = null;
                if (renameStage == null) {
                    createRenameWindow();
                    ClientService.setrenameController(renameController);
                }
                renameStage.show();
            } else {
                resolution = fileinfo.getFileName().substring(fileinfo.getFileName().lastIndexOf('.'));
                if (renameStage == null) {
                    createRenameWindow();
                    ClientService.setrenameController(renameController);
                }
                renameStage.show();
            }
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

    public void createNewDirectory(ActionEvent actionEvent) {
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

    public void uploadFile(ActionEvent actionEvent) {
        Network network = ClientService.getNetwork();
        FileInfo fileinfo = localfiles.getSelectionModel().getSelectedItem();
        if (fileinfo != null && !fileinfo.getType().getName().equals("D")) {
            File file = new File(String.valueOf(root), fileinfo.getFileName());
            network.sendFiles(new UploadRequest(file));
        }
    }

    public void saveDownloadedFile(String filename, byte[] data) {
        String pathOfFile = String.format(root + "\\%s", filename);
        try (FileOutputStream fos = new FileOutputStream(pathOfFile)) {
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getListOfFiles(root);
    }

    public String getResolution() {
        return resolution;
    }

    public void renameFolder(String name) {
        renameStage.close();
        FileInfo fileInfo = localfiles.getSelectionModel().getSelectedItem();
        String path = String.valueOf(root);
        File dir = new File(path, fileInfo.getFileName());
        dir.renameTo(new File(path, name));
        getListOfFiles(root);
    }

    public void renameFile(String name) {
        renameStage.close();
        FileInfo fileInfo = localfiles.getSelectionModel().getSelectedItem();
        String path = String.valueOf(root);
        File file = new File(path, fileInfo.getFileName());
        file.renameTo(new File(path, name + resolution));
        resolution = null;
        getListOfFiles(root);
    }

    public void createFolder(String name) {
        nameStage.close();
        String path = String.valueOf(root);
        File dir = new File(path, name);
        if (dir.mkdirs()) {
            ClientService.getLogger().info("Folder created!");
        }
        getListOfFiles(root);
    }
}
