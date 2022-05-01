package ru.geekbrains.storage.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import ru.geekbrains.storage.FileInfo;

public class FileListCell extends ListCell<FileInfo> {
    private FXMLLoader fxmlLoader;

    @Override
    public void updateSelected(boolean selected){
        super.updateSelected(selected);
    }

    @Override
    protected void updateItem(FileInfo item, boolean empty){
        super.updateItem(item, empty);
        if (item == null || empty) {
            setText(null);
        } else {
            String fileName = String.format("%-20s", item.getFileName());
            String fileSize = String.format("%10s bytes", item.getFileSize());
            if (item.getFileSize() == -1L || item.getFileSize() == -2L) {
                fileSize = " ";
            }
            String text = String.format("%s|%s", fileName, fileSize);
            setText(text);
        }
    }

}
