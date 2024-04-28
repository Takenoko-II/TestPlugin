package com.gmail.subnokoii.testplugin.lib.vector;

public class VectorDimensionSize {
    private final int size;

    public VectorDimensionSize(int dimensionSize) {
        size = dimensionSize;
    }

    public int getValue() {
        return size;
    }

    public boolean match(VectorBuilder vector) {
        return vector.getAllComponents().length == size;
    }
}
