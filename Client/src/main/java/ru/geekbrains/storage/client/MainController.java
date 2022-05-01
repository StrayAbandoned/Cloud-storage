package ru.geekbrains.storage.client;

import javafx.application.Platform;
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
import javafx.util.Callback;
import ru.geekbrains.storage.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    private final Path start = Paths.get(System.getProperty("user.dir"));



    private Path root, remoteRoot;
    private RegController regController;

    private boolean isAuthenticated;
    private RegRequest authData;
    private Network network;
    private Stage stage, regStage;
    private List<FileInfo> files,remFiles;

    @FXML
    private Button uploadbtn, downloadbtn;
    @FXML
    private ListView<FileInfo> localfiles, remotefiles;
    @FXML
    private TextField localpath, remotepath;
    @FXML
    private Button btnlog;
    @FXML
    private Button btnreg;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private Label info;
    @FXML
    private VBox loginbox;
    @FXML
    private VBox clientside;
    @FXML
    private VBox serverside;
    @FXML
    private ContextMenu context = new ContextMenu();
    @FXML
    private MenuItem copy;
    @FXML
    private MenuItem delete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        network = new Network();
        ClientService.setMainController(this);

        Platform.runLater(() -> {
            stage = (Stage) btnreg.getScene().getWindow();
            files = showFiles(start);
            localfiles.getItems().addAll(files);
            localfiles.setCellFactory(x -> new FileListCell());
            goToDirectory(start);

            stage.setOnCloseRequest(windowEvent -> {
                Platform.exit();
                network.close();
            });
        });
        setAuthenticated(false);
    }


    //Блок, связанный с регистрацией

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader1 = new FXMLLoader(ClientApp.class.getResource("registration.fxml"));
            Parent root = fxmlLoader1.load();
            regStage = new Stage();
            regStage.setTitle("Registration");
            regStage.setScene(new Scene(root, 300, 300));
            regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.initStyle(StageStyle.UTILITY);
            regController = fxmlLoader1.getController();
            regController.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void tryToReg(RegRequest regAuth) {
        network.sendFiles(regAuth);

    }

    public void registration(ActionEvent actionEvent) {
        if (regStage == null) {
            createRegWindow();
            ClientService.setRegController(regController);
        }
        regStage.show();

    }

    //блок, связанный с аутентификацией

    public void login(ActionEvent actionEvent) {
        String login = this.login.getText().trim();
        String password = this.password.getText().trim();
        if (login.isBlank() || password.isBlank()) {
            info.setText("Fill in all the fields!");
            return;
        }
        tryToAuth(new AuthRequest(login, password));

    }

    private void tryToAuth(AuthRequest authRequest) {
        network.sendFiles(authRequest);
    }


    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
        loginbox.setVisible(!isAuthenticated);
        loginbox.setManaged(!isAuthenticated);
        clientside.setVisible(isAuthenticated);
        clientside.setManaged(isAuthenticated);
        serverside.setVisible(isAuthenticated);
        serverside.setManaged(isAuthenticated);
    }

    public void failAuth(String s) {
        info.setText(s);
    }

    //блок, связанный с файловой системой


    public List<FileInfo> showFiles(Path rootPath) {
        List<FileInfo> out = new CopyOnWriteArrayList<>();
        try {
            List<Path> paths = null;
            paths = Files.list(rootPath).collect(Collectors.toList());
            for (Path p : paths) {
                out.add(new FileInfo(p));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;

    }

    public void goToDirectory(Path path) {
        root = path;
        localpath.setText(root.toAbsolutePath().toString());
        localfiles.getItems().clear();
        localfiles.getItems().add(new FileInfo(FileInfo.back, -2L));
        localfiles.getItems().addAll(showFiles(root));
        localfiles.getItems().sort(new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if (o1.getFileSize() == -2L) return -1;
                if ((int) Math.signum(o1.getFileSize()) == (int) Math.signum(o2.getFileSize())) {
                    return o1.getFileName().compareTo(o2.getFileName());
                }
                return (int) (o1.getFileSize() - o2.getFileSize());
            }
        });
    }
    public void goToRemoteDirectory(Path path) {
        remoteRoot = path;
     //   ClientService.setPath(String.valueOf(path));
        remotepath.setText(path.toAbsolutePath().toString());
        remotefiles.getItems().clear();
        remotefiles.getItems().add(new FileInfo(FileInfo.back, -2L));
        remotefiles.getItems().addAll(showFiles(remoteRoot));
        remotefiles.getItems().sort((o1, o2) -> {
            if (o1.getFileSize() == -2L) return -1;
            if ((int) Math.signum(o1.getFileSize()) == (int) Math.signum(o2.getFileSize())) {
                return o1.getFileName().compareTo(o2.getFileName());
            }
            return (int) (o1.getFileSize() - o2.getFileSize());
        });
    }

    public void filesClicked(MouseEvent mouseEvent) {
        FileInfo fileInfo = localfiles.getSelectionModel().getSelectedItem();

//        copy.setOnAction(actionEvent -> fileInfo.copyItem());
//        delete.setOnAction(actionEvent -> fileInfo.deleteItem());
//        localfiles.setContextMenu(context);

        if (mouseEvent.getClickCount() == 2) {

            if (fileInfo != null) {
                if (fileInfo.isDirectory()) {
                    Path pathTo = root.resolve(fileInfo.getFileName());
                    goToDirectory(pathTo);
                }
                if (fileInfo.isBack()) {
                    if (root.getParent() != null) {
                        Path pathTo = root.getParent();
                        goToDirectory(pathTo);
                    }

                }
            }
        }
    }

    public void showRemoteFiles(List<FileInfo> remFiles, Path path){
        this.remoteRoot = path;
        this.remFiles = remFiles;
        remotefiles.getItems().addAll(remFiles);
        remotefiles.setCellFactory(x -> new FileListCell());
        goToRemoteDirectory(path);
    }


    public void copy(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }

    public void sendFile(ActionEvent actionEvent) throws FileNotFoundException {
        FileInfo fileinfo = localfiles.getSelectionModel().getSelectedItem();
        if(fileinfo!=null&&!fileinfo.isDirectory()){
            File file = new File(String.valueOf(root),fileinfo.getFileName());
            network.sendFiles(new UploadRequest(file));
        }

    }

    public void getFile(ActionEvent actionEvent) {
    }

    public void filesClickedRemote(MouseEvent mouseEvent) {
        FileInfo fileInfo = remotefiles.getSelectionModel().getSelectedItem();

//        copy.setOnAction(actionEvent -> fileInfo.copyItem());
//        delete.setOnAction(actionEvent -> fileInfo.deleteItem());
//        localfiles.setContextMenu(context);

        if (mouseEvent.getClickCount() == 2) {
//            if (fileInfo != null) {
//                if (fileInfo.isDirectory()) {
//                    ClientService.setPath(String.valueOf(remoteRoot.resolve(fileInfo.getFileName())));
//                    network.sendFiles(new GetFilesRequest(ClientService.getPath()));
//                }
//                if (fileInfo.isBack()) {
//                    if (remoteRoot.getParent() != null && remoteRoot.getNameCount()>4) {
//                        ClientService.setPath(String.valueOf(remoteRoot.getParent()));
//                        network.sendFiles(new GetFilesRequest(ClientService.getPath()));
//                    }
//
//                }
//            }

            if (fileInfo != null) {
                if (fileInfo.isDirectory()) {
                    Path pathTo = remoteRoot.resolve(fileInfo.getFileName());
                    goToRemoteDirectory(pathTo);
                }
                if (fileInfo.isBack()) {
                    if (remoteRoot.getParent() != null && remoteRoot.getNameCount()>5) {
                        Path pathTo = remoteRoot.getParent();
                        goToRemoteDirectory(pathTo);
                    }

                }
            }
        }
    }
}