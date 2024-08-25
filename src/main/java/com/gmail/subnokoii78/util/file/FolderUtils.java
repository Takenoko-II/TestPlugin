package com.gmail.subnokoii78.util.file;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FolderUtils {
    private FolderUtils() {}

    public static void create(@NotNull String path) {
        final Path dir = Path.of(path);

        if (Files.exists(dir)) return;

        try {
            Files.createDirectory(dir);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exist(@NotNull String path) {
        return Files.exists(Path.of(path));
    }
}
