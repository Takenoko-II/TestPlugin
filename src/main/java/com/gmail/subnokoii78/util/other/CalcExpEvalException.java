package com.gmail.subnokoii78.util.other;

import org.jetbrains.annotations.NotNull;

public final class CalcExpEvalException extends RuntimeException {
    CalcExpEvalException(@NotNull String message) {
        super(message);
    }

    CalcExpEvalException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
