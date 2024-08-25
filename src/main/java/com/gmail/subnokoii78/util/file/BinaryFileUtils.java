package com.gmail.subnokoii78.util.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinaryFileUtils {
    /**
     * 指定のパスのテキストファイルを行ごとに読み取ります。
     * @param path サーバーフォルダからの相対パス
     * @return 行ごとの文字列もしくはnull
     */
    public static byte[] read(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        }
        catch (IOException e) {
            return null;
        }
    }

    public static void overwrite(String path, byte[] bytes) {
        try {
            Files.write(Path.of(path), bytes);
        }
        catch (IOException e) {
            throw new RuntimeException("ファイルの書き込みに失敗しました");
        }
    }

    public static void erase(String path) {
        overwrite(path, new byte[0]);
    }

    public static void create(String path) {
        final Path filePath = Path.of(path);

        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void delete(String path) {
        if (TextFileUtils.exist(path)) {
            try {
                Files.delete(Path.of(path));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
