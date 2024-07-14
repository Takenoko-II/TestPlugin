package com.gmail.subnokoii78.util.datacontainer;

public class InvalidContainerKeyException extends RuntimeException {
    public InvalidContainerKeyException(String key) {
        super("無効なキーの形式です: '" + key + "'");
    }

    public InvalidContainerKeyException() {
        super("無効なキーの形式です");
    }
}
