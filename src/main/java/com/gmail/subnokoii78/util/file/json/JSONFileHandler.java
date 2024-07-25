package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public final class JSONFileHandler {
    private final String path;

    /**
     * 指定パスのjsonファイルを読み取ります。
     * @param path ファイルパス
     */
    public JSONFileHandler(@NotNull String path) {
        if (!path.endsWith(".json")) {
            throw new IllegalArgumentException("ファイルの拡張子は.jsonである必要があります");
        }

        this.path = path;
    }

    private String read() {
        try {
            return String.join("", Files.readAllLines(Path.of(path)));
        }
        catch (IOException e) {
            throw new RuntimeException("ファイルを読み取れませんでした", e);
        }
    }

    private void write(String[] json) {
        try {
            Files.write(Path.of(path), Arrays.stream(json).toList(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException("ファイルの書き込みに失敗しました", e);
        }
    }

    /**
     * オブジェクトを解析します。
     * @return 解析されたjsonオブジェクト
     */
    public JSONObject readObject() {
        return new JSONParser(read()).parseObject();
    }

    /**
     * 配列を解析します。
     * @return 解析されたjson配列
     */
    public JSONArray readArray() {
        return new JSONParser(read()).parseArray();
    }

    public void writeObject(@NotNull JSONObject object) {
        write(new JSONSerializer(object).serialize().split("\n"));
    }

    public void writeArray(@NotNull JSONArray array) {
        write(new JSONSerializer(array).serialize().split("\n"));
    }
}
