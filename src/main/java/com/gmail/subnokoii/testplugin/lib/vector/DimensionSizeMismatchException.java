package com.gmail.subnokoii.testplugin.lib.vector;

public class DimensionSizeMismatchException extends IllegalArgumentException {
    public DimensionSizeMismatchException() {
        super("Dimension sizes of vectors do not match");
    }
}
