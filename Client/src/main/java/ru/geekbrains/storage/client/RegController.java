package ru.geekbrains.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.geekbrains.storage.RegRequest;


public class RegController {
    @FXML
    private PasswordField password;
    @FXML
    private TextField login;
    @FXML
    private TextArea textArea;
    @FXML
    private PasswordField passConfirm;

    private MainController controller;

    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void registration(ActionEvent actionEvent) {
        textArea.clear();
        String login = this.login.getText().trim();
        String password = this.password.getText().trim();
        String passConfirm = this.passConfirm.getText().trim();
        if (login.isBlank()||password.isBlank()||passConfirm.isBlank()) {
            textArea.appendText("Fill in all the fields!");
            return;
        }
        if (!login.isBlank()&&password.equals(passConfirm)){
            controller.sendRegistrationRequest(new RegRequest(login, password));
        } else if (!password.equals(passConfirm)){
            textArea.appendText("Your password must be the same!");
        }
    }


    public void regInfo(String s){
       textArea.appendText(s);
    }

}
