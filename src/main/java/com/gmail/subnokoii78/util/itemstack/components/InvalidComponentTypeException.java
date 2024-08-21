package com.gmail.subnokoii78.util.itemstack.components;

public final class InvalidComponentTypeException extends RuntimeException {
    InvalidComponentTypeException() {
        super("このコンポーネントは無効です");
    }

    InvalidComponentTypeException(String message) {
        super("このコンポーネントは無効です: " + message);
    }

    InvalidComponentTypeException(Throwable cause) {
        super("このコンポーネントは無効です", cause);
    }

    InvalidComponentTypeException(String message, Throwable cause) {
        super("このコンポーネントは無効です: " + message, cause);
    }
}
