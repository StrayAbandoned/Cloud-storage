package ru.geekbrains.storage.client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;

import ru.geekbrains.storage.ChangePasswordRequest;


public class SettingController implements Controller{

    @FXML
    private TextField result;
    @FXML
    private PasswordField passOne;
    @FXML
    private PasswordField passTwo;
    private MainController controller;


    public void setController(MainController controller) {
        this.controller = controller;
    }
    public void confirm(ActionEvent actionEvent) {

        result.clear();
        String password = passOne.getText().trim();
        String confirmPassword = passTwo.getText().trim();
        if (password.isBlank()||confirmPassword.isBlank()||!password.equals(confirmPassword)) {
            System.out.println(password.isBlank());
            System.out.println(confirmPassword.isBlank());
            System.out.println(!password.equals(confirmPassword));
            Alert alert = new Alert(Alert.AlertType.WARNING, "Passwords should be the same", ButtonType.OK);
            result.setText("Passwords should be the same");
        } else{
            passOne.clear();
            passTwo.clear();
            controller.sendChangePasswordRequest(new ChangePasswordRequest(ClientService.getLogin(), password));
        }


    }

    public TextField getResult() {
        return result;
    }
}
