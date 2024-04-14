package com.gmail.subnokoii.testplugin.lib.vector;

public class DimensionSizeMismatchException extends Exception {
    public DimensionSizeMismatchException() {
        super("Dimension sizes of vectors do not match");
    }
}
