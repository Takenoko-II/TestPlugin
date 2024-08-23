package com.gmail.subnokoii78.util.other;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record TupleT<T>(T left, T right) {
    public void forEach(@NotNull Consumer<T> consumer) {
        consumer.accept(left);
        consumer.accept(right);
    }
}
