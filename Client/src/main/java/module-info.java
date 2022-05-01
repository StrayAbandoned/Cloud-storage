module ru.geekbrains.storage.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.transport;
    requires io.netty.codec;
    requires ServiceMsg;
    requires java.logging;


    opens ru.geekbrains.storage.client to javafx.fxml;
    exports ru.geekbrains.storage.client;
}