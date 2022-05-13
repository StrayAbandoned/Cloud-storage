package ru.geekbrains.storage.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.geekbrains.storage.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable, Controller {


    private RegController regController;
    private SettingController settingController;
    private Network network;
    private Stage stage, regStage, settingStage;

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
    private VBox localPanel;
    @FXML
    private VBox remotePanel;
    public MenuItem settings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        network = new Network();
        ClientService.setMainController(this);

        Platform.runLater(() -> {
            stage = (Stage) btnreg.getScene().getWindow();
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

    public void sendRegistrationRequest(RegRequest regAuth) {
        network.sendFiles(regAuth);
    }

    public void registrationButtonClicked(ActionEvent actionEvent) {
        if (regStage == null) {
            createRegWindow();
            ClientService.setRegController(regController);
        }
        regStage.show();
    }

    //блок, связанный с аутентификацией

    public void loginButtonClicked(ActionEvent actionEvent) {
        String login = this.login.getText().trim();
        String password = this.password.getText().trim();
        if (login.isBlank() || password.isBlank()) {
            info.setText("Fill in all the fields!");
            return;
        }
        sendAuthenticationRequest(new AuthRequest(login, password));
    }

    private void sendAuthenticationRequest(AuthRequest authRequest) {
        network.sendFiles(authRequest);
    }


    public void setAuthenticated(boolean authenticated) {
        loginbox.setVisible(!authenticated);
        loginbox.setManaged(!authenticated);
        localPanel.setVisible(authenticated);
        localPanel.setManaged(authenticated);
        remotePanel.setVisible(authenticated);
        remotePanel.setManaged(authenticated);
        settings.setDisable(!authenticated);

    }


    public void backToAuthentication(ActionEvent actionEvent) {
        setAuthenticated(false);
        network.sendFiles(new LogOutRequest());
    }

    public void openSettings(ActionEvent actionEvent) {
        if (settingStage == null) {
            createSettingWindow();
            ClientService.setSettingController(settingController);
        }
        settingStage.show();

    }


    private void createSettingWindow() {
        try {
            FXMLLoader fxmlLoader3 = new FXMLLoader(ClientApp.class.getResource("settings.fxml"));
            Parent root = fxmlLoader3.load();
            settingStage = new Stage();
            settingStage.setTitle("Settings");
            settingStage.setScene(new Scene(root, 600, 300));
            settingStage.initModality(Modality.APPLICATION_MODAL);
            settingStage.initStyle(StageStyle.UTILITY);
            settingController = fxmlLoader3.getController();
            settingController.setController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getSettingStage() {
        return settingStage;
    }
    public void sendChangePasswordRequest(ChangePasswordRequest changePasswordRequest) {
        network.sendFiles(changePasswordRequest);
    }
}
