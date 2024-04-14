package com.gmail.subnokoii.testplugin.lib.vector;

public class VectorBuilderDimensionSize {
    private final int size;

    public VectorBuilderDimensionSize(int dimensionSize) {
        size = dimensionSize;
    }

    public int getValue() {
        return size;
    }

    public boolean match(VectorBuilder vector) {
        return vector.getAllComponents().length == size;
    }
}
