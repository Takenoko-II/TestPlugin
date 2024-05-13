package com.gmail.subnokoii.testplugin.lib.vector;

public class DimensionSizeMismatchException extends IllegalArgumentException {
    public DimensionSizeMismatchException() {
        super("引数に渡された値が期待される次元のサイズと一致していません");
    }
}
