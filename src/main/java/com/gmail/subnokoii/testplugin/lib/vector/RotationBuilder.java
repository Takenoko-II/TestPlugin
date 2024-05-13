package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class RotationBuilder implements VectorBuilder {
    private final double[] components;

    private final DimensionSize dimensionSize;

    public RotationBuilder() {
        components = new double[3];
        dimensionSize = new DimensionSize(2);
    }

    public RotationBuilder(double x, double y) {
        components = new double[]{ x, y };
        dimensionSize = new DimensionSize(2);
    }

    public RotationBuilder(double... allComponents) throws UnexpectedDimensionSizeException {
        if (allComponents.length != 2) {
            throw new UnexpectedDimensionSizeException();
        }

        double[] newArray = new double[2];
        System.arraycopy(allComponents, 0, newArray, 0, allComponents.length);

        components = newArray;
        dimensionSize = new DimensionSize(3);
    }

    public double getComponent(int index) {
        return components[index];
    }

    public double[] getAllComponents() {
        return components.clone();
    }

    public RotationBuilder setComponent(int index, double component) throws DimensionSizeMismatchException {
        if (index > 1) {
            throw new DimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    public RotationBuilder setAllComponents(double[] componentsList) throws DimensionSizeMismatchException {
        if (componentsList.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        System.arraycopy(componentsList, 0, components, 0, componentsList.length);

        return this;
    }

    public float yaw() {
        return (float) components[0];
    }

    public void yaw(float value) {
        components[0] = value;
    }

    public float pitch() {
        return (float) components[1];
    }

    public void pitch(float value) {
        components[1] = value;
    }

    public DimensionSize getDimensionSize() {
        return dimensionSize;
    }

    public Vector3Builder getDirection3d() {
        final double yaw = components[0];
        final double pitch = components[1];

        final double x = -Math.sin(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);
        final double y = -Math.sin(pitch * Math.PI / 180);
        final double z = Math.cos(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);

        return new Vector3Builder(x, y, z);
    }

    public double getAngleBetween(RotationBuilder rotation) {
        return getDirection3d().getAngleBetween(rotation.getDirection3d());
    }

    public RotationBuilder calculate(UnaryOperator<Double> operator) {
        for (int i = 0; i < components.length; i++) {
            components[i] = operator.apply(components[i]);
        }

        return this;
    }

    public RotationBuilder calculate(RotationBuilder other, BiFunction<Double, Double, Double> operator) {
        for (int i = 0; i < components.length; i++) {
            components[i] = operator.apply(components[i], other.components[i]);
        }

        return this;
    }

    public RotationBuilder multiplyByScalar(double scalar) {
        return calculate(component -> component * scalar);
    }

    public RotationBuilder inverted() {
        return multiplyByScalar(-1d);
    }

    public RotationBuilder fill(double value) {
        return calculate(component -> value);
    }

    public RotationBuilder add(RotationBuilder addend) {
        return calculate(addend, Double::sum);
    }

    public RotationBuilder subtract(RotationBuilder subtrahend) {
        return add(subtrahend.copy().inverted());
    }

    public RotationBuilder copy() {
        return new RotationBuilder(components[0], components[1]);
    }

    public Location toLocation(Location location) {
        return new Location(location.getWorld(), location.x(), location.y(), location.z(), yaw(), pitch());
    }

    public Location toLocation(Vector3Builder coordinate, World world) {
        return new Location(world, coordinate.x(), coordinate.y(), coordinate.z(), yaw(), pitch());
    }

    public static RotationBuilder from(Vector<Double> vector) throws DimensionSizeMismatchException {
        if (vector.size() != 2) {
            throw new DimensionSizeMismatchException();
        }

        return new RotationBuilder(vector.get(0), vector.get(1));
    }

    public static RotationBuilder from(Location location) {
        return new RotationBuilder(location.getYaw(), location.getPitch());
    }

    public static RotationBuilder from(double[] array) throws DimensionSizeMismatchException {
        if (array.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        return new RotationBuilder(array[0], array[1]);
    }

    public static RotationBuilder from(List<Double> list) throws DimensionSizeMismatchException {
        if (list.size() != 2) {
            throw new DimensionSizeMismatchException();
        }

        return new RotationBuilder(list.get(0), list.get(1));
    }
}
