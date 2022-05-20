package ru.geekbrains.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class FileDivide {
    private static final int PART_SIZE = 1_048_576;

    public void divide(Path path, BiConsumer<byte[], Integer> filePartConsumer) {
        byte[] filePart = new byte[PART_SIZE];
        int len = 0;
        try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
            while ((len = fileInputStream.read(filePart)) != -1) {
                filePartConsumer.accept(filePart, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
