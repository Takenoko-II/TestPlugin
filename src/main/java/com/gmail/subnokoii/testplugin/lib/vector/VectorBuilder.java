package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface VectorBuilder {
    /**
     * 特定のインデックスの成分の値を返します。
     * @param index インデックス
     * @return 成分の値
     */
    double getComponent(int index);

    /**
     * 全成分の値を返します。
     * @return 全成分
     */
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

    static Location merge(Vector3Builder vector3, RotationBuilder vector2, World world) {
        return new Location(world, vector3.x(), vector3.y(), vector3.z(), vector2.yaw(), vector2.pitch());
    }

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
