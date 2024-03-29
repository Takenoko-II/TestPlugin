package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;

public class VectorBuilder {
    private final double[] components;

    private final VectorBuilderDimensionSize vectorDimensionSize;

    public VectorBuilder(int dimensionSize) {
        if (dimensionSize <= 0) {
            components = new double[0];
            vectorDimensionSize = new VectorBuilderDimensionSize(0);
        }
        else {
            components = new double[dimensionSize];
            vectorDimensionSize = new VectorBuilderDimensionSize(dimensionSize);
        }
    }

    public VectorBuilder(double x, double y) {
        components = new double[]{ x, y };
        vectorDimensionSize = new VectorBuilderDimensionSize(2);
    }

    public VectorBuilder(double x, double y, double z) {
        components = new double[]{ x, y, z };
        vectorDimensionSize = new VectorBuilderDimensionSize(3);
    }

    public double getComponent(int index) {
        return components[index];
    }

    public double[] getAllComponents() {
        return components;
    }

    public VectorBuilder setComponent(int index, double component) throws VectorDimensionSizeMismatchException {
        if (components.length -1 < index) {
            throw new VectorDimensionSizeMismatchException();
        }
        components[index] = component;

        return this;
    }

    public VectorBuilder setAllComponents(double[] componentsList) throws VectorDimensionSizeMismatchException {
        if (componentsList.length != components.length) {
            throw new VectorDimensionSizeMismatchException();
        }

        System.arraycopy(componentsList, 0, components, 0, componentsList.length);

        return this;
    }

    public boolean is(VectorBuilder vectorBuilder) {
        if (components.length != vectorBuilder.components.length) {
            return false;
        }

        for (int i = 0; i < components.length; i++) {
            if (components[i] != vectorBuilder.getComponent(i)) {
                return false;
            }
        }

        return true;
    }

    public VectorBuilderDimensionSize getDimensionSize() {
        return vectorDimensionSize;
    }

    public double dot(VectorBuilder vectorBuilder) throws VectorDimensionSizeMismatchException {
        if (!vectorDimensionSize.match(vectorBuilder)) {
            throw new VectorDimensionSizeMismatchException();
        }

        double summaryOfMultiple = 0d;
        for (int i = 0; i < components.length; i++) {
            summaryOfMultiple += components[i] * vectorBuilder.getComponent(i);
        }

        return summaryOfMultiple;
    }

    public double getLength() {
        try {
            return Math.sqrt(dot(this));
        } catch (VectorDimensionSizeMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    public VectorBuilder setLength(double length) {
        double previousLength = getLength();

        for (int i = 0; i < components.length; i++) {
            components[i] = components[i] / previousLength * length;
        }

        return this;
    }

    public VectorBuilder normalized() {
        return setLength(1d);
    }

    public VectorBuilder add(VectorBuilder vectorBuilder) throws VectorDimensionSizeMismatchException {
        if (!vectorDimensionSize.match(vectorBuilder)) {
            throw new VectorDimensionSizeMismatchException();
        }

        for (int i = 0; i < components.length; i++) {
            components[i] += vectorBuilder.getComponent(i);
        }

        return this;
    }

    public VectorBuilder subtract(VectorBuilder vectorBuilder) throws VectorDimensionSizeMismatchException {
        if (!vectorDimensionSize.match(vectorBuilder)) {
            throw new VectorDimensionSizeMismatchException();
        }

        for (int i = 0; i < components.length; i++) {
            components[i] -= vectorBuilder.getComponent(i);
        }

        return this;
    }

    public VectorBuilder multiplyByScalar(double scalar) {
        for (int i = 0; i < components.length; i++) {
            components[i] *= scalar;
        }

        return this;
    }

    public VectorBuilder inverted() {
        return multiplyByScalar(-1d);
    }

    public VectorBuilder getRotation() throws VectorUnexpectedDimensionSizeException {
        if (vectorDimensionSize.getValue() != 3) {
            throw new VectorUnexpectedDimensionSizeException();
        }

        return new VectorBuilder(
                -Math.asin(components[1] / getLength()) * 180d / Math.PI,
                -Math.atan2(components[0] / getLength(), components[2]/ getLength()) * 180d / Math.PI
        );
    }

    public Location toLocation(World world) throws VectorUnexpectedDimensionSizeException {
        if (vectorDimensionSize.getValue() != 3) {
            throw new VectorUnexpectedDimensionSizeException();
        }

        return new Location(world, components[0], components[1], components[2]);
    }

    public static VectorBuilder getVector3FromRotation(VectorBuilder vectorBuilder) throws VectorUnexpectedDimensionSizeException {
        if (vectorBuilder.vectorDimensionSize.getValue() != 2) {
            throw new VectorUnexpectedDimensionSizeException();
        }

        final double x = vectorBuilder.components[0];
        final double y = vectorBuilder.components[1];

        return new VectorBuilder(
                -Math.sin(x * Math.PI / 180) * Math.cos(y * Math.PI / 180),
                -Math.sin(y * Math.PI / 180),
                Math.cos(x * Math.PI / 180) * Math.cos(y * Math.PI / 180)
        );
    }
}
