package com.gmail.subnokoii.testplugin.lib.vector;

public class UnexpectedDimensionSizeException extends IllegalArgumentException {
    public UnexpectedDimensionSizeException() {
        super("Unexpected dimension size vector passed");
    }
}
