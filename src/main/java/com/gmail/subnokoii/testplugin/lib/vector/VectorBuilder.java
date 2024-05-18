package com.gmail.subnokoii.testplugin.lib.vector;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface VectorBuilder {
    double getComponent(int index);

    double[] getAllComponents();

    VectorBuilder setComponent(int index, double component);

    VectorBuilder setAllComponents(double... allComponents);

    default boolean equals(VectorBuilder vector) {
        for (int i = 0; i < getAllComponents().length; i++) {
            if (getComponent(i) != vector.getComponent(i)) return false;
        }

        return true;
    }

    default VectorBuilder calc(UnaryOperator<Double> operator) {
        for (int i = 0; i < getAllComponents().length; i++) {
            setComponent(i, operator.apply(getComponent(i)));
        }

        return this;
    }

    default VectorBuilder calc(VectorBuilder other, BiFunction<Double, Double, Double> operator) {
        for (int i = 0; i < getAllComponents().length; i++) {
            setComponent(i, operator.apply(getComponent(i), other.getComponent(i)));
        }

        return this;
    }

    String toString();

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
