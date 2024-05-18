package com.gmail.subnokoii.testplugin.lib.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public class TextFileUtils {
    public static List<String> read(String path) {
        try {
            return Files.readAllLines(Path.of(path), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            return null;
        }
    }

    public static String read(String path, int line) {
        final List<String> texts = read(path);

        if (texts == null) return null;

        return texts.get(line - 1);
    }

    public static void overwrite(String path, List<String> texts) {
        try {
            Files.write(Path.of(path), texts, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("ファイルの書き込みに失敗しました");
        }
    }

    public static void overwrite(String path, String text, int line) {
        final List<String> texts = read(path);

        if (texts == null) return;

        texts.set(line, text);

        overwrite(path, texts);
    }

    public static void write(String path, String text) {
        final List<String> texts = read(path);

        if (texts == null) return;

        texts.add(text);

        overwrite(path, texts);
    }

    public static void write(String path, String text, int line) {
        final List<String> texts = read(path);

        if (texts == null) return;

        texts.add(line, text);

        overwrite(path, texts);
    }

    public static void erase(String path, int line) {
        final List<String> texts = read(path);

        if (texts == null) return;

        texts.remove(line);

        overwrite(path, texts);
    }

    public static void erase(String path) {
        overwrite(path, List.of());
    }

    public static void delete(String path) {
        try {
            Files.delete(Path.of(path));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getSize(String path) {
        try {
            return Files.size(Path.of(path));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getAll(String path) {
        try (final Stream<Path> list = Files.list(Path.of(path))) {
            return list
            .map(Path::toString)
            .toArray(String[]::new);
        }
        catch (IOException e) {
            return new String[0];
        }
    }
}
