package com.gmail.subnokoii.testplugin.lib.vector;

public interface VectorBuilder {
    double getComponent(int index);

    double[] getAllComponents();

    VectorBuilder setComponent(int index, double component) throws DimensionSizeMismatchException;

    VectorBuilder setAllComponents(double... allComponents) throws DimensionSizeMismatchException;

    default boolean is(VectorBuilder vector) {
        if (this.getAllComponents().length != vector.getAllComponents().length) {
            return false;
        }

        return this.getComponent(0) == vector.getComponent(0)
        && this.getComponent(1) == vector.getComponent(1)
        && this.getComponent(2) == vector.getComponent(2);
    }

    DimensionSize getDimensionSize();

    VectorBuilder copy();

    final class DimensionSize {
        private final int size;

        public DimensionSize(int size) {
            this.size = size;
        }

        public int getValue() {
            return size;
        }

        public boolean match(VectorBuilder vector) {
            return vector.getAllComponents().length == size;
        }
    }
}
