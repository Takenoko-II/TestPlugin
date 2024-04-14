package com.gmail.subnokoii.testplugin.lib.vector;

public class UnexpectedDimensionSizeException extends Exception {
    public UnexpectedDimensionSizeException() {
        super("Unexpected dimension size vector passed");
    }
}
