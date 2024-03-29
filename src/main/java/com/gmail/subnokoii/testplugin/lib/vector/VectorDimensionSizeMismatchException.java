package com.gmail.subnokoii.testplugin.lib.vector;

public class VectorDimensionSizeMismatchException extends Exception {
    public VectorDimensionSizeMismatchException() {
        super("Dimension sizes of vectors do not match");
    }
}
