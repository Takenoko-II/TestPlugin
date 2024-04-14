package com.gmail.subnokoii.testplugin.lib.vector;

import java.util.List;

public class RotationBuilder implements VectorBuilder {
    private final double[] components;

    private final VectorBuilderDimensionSize dimensionSize;

    public RotationBuilder() {
        components = new double[3];
        dimensionSize = new VectorBuilderDimensionSize(2);
    }

    public RotationBuilder(double x, double y) {
        components = new double[]{ x, y };
        dimensionSize = new VectorBuilderDimensionSize(2);
    }

    public RotationBuilder(double[] allComponents) throws UnexpectedDimensionSizeException {
        if (allComponents.length != 2) {
            throw new UnexpectedDimensionSizeException();
        }

        double[] newArray = new double[2];
        System.arraycopy(allComponents, 0, newArray, 0, allComponents.length);

        components = newArray;
        dimensionSize = new VectorBuilderDimensionSize(3);
    }

    public double getComponent(int index) {
        return components[index];
    }

    public double[] getAllComponents() {
        return components;
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

    public double yaw() {
        return components[0];
    }

    public void yaw(double value) {
        components[0] = value;
    }

    public double pitch() {
        return components[1];
    }

    public void pitch(double value) {
        components[1] = value;
    }

    public VectorBuilderDimensionSize getDimensionSize() {
        return dimensionSize;
    }

    public Vector3Builder getDirection3d() {
        final double yaw = components[0];
        final double pitch = components[1];

        final double x = -Math.sin(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);
        final double y = Math.sin(pitch * Math.PI / 180);
        final double z = Math.cos(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);

        return new Vector3Builder(x, y, z);
    }

    public double getAngleBetween(RotationBuilder rotation) {
        return getDirection3d().getAngleBetween(rotation.getDirection3d());
    }

    public RotationBuilder add(RotationBuilder addend) {
        return getDirection3d().add(addend.getDirection3d()).getRotation2d();
    }

    public RotationBuilder subtract(RotationBuilder subtrahend) {
        return getDirection3d().subtract(subtrahend.getDirection3d()).getRotation2d();
    }

    public RotationBuilder copy() {
        return new RotationBuilder(components[0], components[1]);
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
