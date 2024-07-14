package com.gmail.subnokoii78.util.vector;

public class DimensionSizeMismatchException extends IllegalArgumentException {
    public DimensionSizeMismatchException() {
        super("引数に渡された値が期待される次元のサイズと一致していません");
    }
}
