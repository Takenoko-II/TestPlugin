package com.gmail.subnokoii78.testplugin.util.itemstack.components;

public final class InvalidComponentTypeException extends RuntimeException {
    InvalidComponentTypeException(String message) {
        super("このコンポーネントは無効です: " + message);
    }

    InvalidComponentTypeException() {
        super("このコンポーネントは無効です");
    }
}
