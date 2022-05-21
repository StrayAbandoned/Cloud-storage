package ru.geekbrains.storage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ClientApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApp.class.getResource("start-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("cloud-storage");
        stage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("styles.css").toExternalForm());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}