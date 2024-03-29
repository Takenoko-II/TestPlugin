package com.gmail.subnokoii.testplugin.lib.vector;

public class VectorUnexpectedDimensionSizeException extends Exception {
    public VectorUnexpectedDimensionSizeException() {
        super("Unexpected dimension size vector passed");
    }
}
