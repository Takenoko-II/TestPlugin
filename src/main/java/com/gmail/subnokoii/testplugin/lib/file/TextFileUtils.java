package com.gmail.subnokoii.testplugin.lib.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class TextFileUtils {
    public static List<String> read(String path) {
        try { return Files.readAllLines(Path.of(path), StandardCharsets.UTF_8); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static String read(String path, int line) {
        return read(path).get(line - 1);
    }

    public static void overwrite(String path, List<String> texts) {
        try { Files.write(Path.of(path), texts, StandardCharsets.UTF_8, StandardOpenOption.WRITE); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public static void overwrite(String path, String text, int line) {
        final List<String> texts = read(path);

        texts.set(line, text);

        overwrite(path, texts);
    }

    public static void write(String path, String text) {
        final List<String> texts = read(path);

        texts.add(text);

        overwrite(path, texts);
    }

    public static void write(String path, String text, int line) {
        final List<String> texts = read(path);

        texts.add(line, text);

        overwrite(path, texts);
    }

    public static void erase(String path, int line) {
        final List<String> texts = read(path);

        texts.remove(line);

        overwrite(path, texts);
    }

    public static void erase(String path) {
        overwrite(path, new ArrayList<>());
    }

    public static void log(String text) {
        final Path logPath = Path.of("plugins/TestPlugin-Logs.txt");

        if (!Files.exists(logPath.getParent())) {
            try { Files.createDirectory(logPath.getParent()); }
            catch (IOException e) { throw new RuntimeException(e); }
        }

        if (!Files.exists(logPath)) {
            try { Files.createFile(logPath); }
            catch (IOException e) { throw new RuntimeException(); }
        }

        final String timestamp = Long.toString(System.currentTimeMillis());

        write(logPath.toString(), "[" + timestamp + "] " + text);
    }
}
