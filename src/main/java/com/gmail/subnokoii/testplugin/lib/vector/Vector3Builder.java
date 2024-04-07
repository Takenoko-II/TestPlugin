package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;

public class Vector3Builder {
    private final double[] components;

    private final VectorBuilderDimensionSize vectorDimensionSize;

    public Vector3Builder() {
        components = new double[3];
        vectorDimensionSize = new VectorBuilderDimensionSize(3);
    }

    public Vector3Builder(double x, double y, double z) {
        components = new double[]{ x, y, z };
        vectorDimensionSize = new VectorBuilderDimensionSize(3);
    }

    public Vector3Builder(double[] componentsList) throws VectorUnexpectedDimensionSizeException {
        if (componentsList.length != 3) {
            throw new VectorUnexpectedDimensionSizeException();
        }

        double[] newArray = new double[3];
        System.arraycopy(componentsList, 0, newArray, 0, componentsList.length);

        components = newArray;
        vectorDimensionSize = new VectorBuilderDimensionSize(3);
    }

    public double getComponent(int index) {
        return components[index];
    }

    public double[] getAllComponents() {
        return components;
    }

    public Vector3Builder setComponent(int index, double component) throws VectorDimensionSizeMismatchException {
        if (index > 2) {
            throw new VectorDimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    public Vector3Builder setAllComponents(double[] componentsList) throws VectorDimensionSizeMismatchException {
        if (componentsList.length != 3) {
            throw new VectorDimensionSizeMismatchException();
        }

        System.arraycopy(componentsList, 0, components, 0, componentsList.length);

        return this;
    }

    public boolean is(Vector3Builder vectorBuilder) {
        if (components.length != 3) {
            return false;
        }

        return components[0] == vectorBuilder.components[0]
            && components[1] == vectorBuilder.components[1]
            && components[2] == vectorBuilder.components[2];
    }

    public VectorBuilderDimensionSize getDimensionSize() {
        return vectorDimensionSize;
    }

    public double dot(Vector3Builder vectorBuilder) {
        double summaryOfMultiple = 0d;
        for (int i = 0; i < 3; i++) {
            summaryOfMultiple += components[i] * vectorBuilder.components[i];
        }

        return summaryOfMultiple;
    }

    public Vector3Builder cross(Vector3Builder vectorBuilder) {
        final double x1 = components[0];
        final double y1 = components[1];
        final double z1 = components[2];
        final double x2 = vectorBuilder.components[0];
        final double y2 = vectorBuilder.components[1];
        final double z2 = vectorBuilder.components[2];

        return new Vector3Builder(
            y1 * z2 - z1 * y2,
            z1 * x2 - x1 * z2,
            x1 * y2 - y1 * x2
        );
    }

    public double getLength() {
        return Math.sqrt(dot(this));
    }

    public Vector3Builder setLength(double length) {
        double previousLength = getLength();

        for (int i = 0; i < 3; i++) {
            components[i] = components[i] / previousLength * length;
        }

        return this;
    }

    public double getAngleBetween(Vector3Builder vectorBuilder) {

        double p = this.dot(vectorBuilder) / (getLength() * vectorBuilder.getLength());

        return Math.acos(p) * 180 / Math.PI;
    }

    public Vector3Builder normalized() {
        return setLength(1d);
    }

    public Vector3Builder add(Vector3Builder vectorBuilder) {

        for (int i = 0; i < 3; i++) {
            components[i] += vectorBuilder.components[i];
        }

        return this;
    }

    public Vector3Builder subtract(Vector3Builder vectorBuilder) {
        return copy().add(vectorBuilder.copy().inverted());
    }

    public Vector3Builder multiplyByScalar(double scalar) {
        for (int i = 0; i < 3; i++) {
            components[i] *= scalar;
        }

        return this;
    }

    public Vector3Builder fill(double value) {
        Arrays.fill(components, value);

        return this;
    }

    public Vector3Builder inverted() {
        return multiplyByScalar(-1d);
    }

    public Vector3Builder directionTo(Vector3Builder vectorBuilder) {
        return vectorBuilder.copy()
        .subtract(this)
        .normalized();
    }

    public double distanceWith(Vector3Builder vectorBuilder) {
        double summaryOfSquare = 0d;

        for (int i = 0; i < 3; i++) {
            final double a = components[i];
            final double b = vectorBuilder.getComponent(i);
            summaryOfSquare += (a - b) * (a - b);
        }

        return Math.sqrt(summaryOfSquare);
    }

    public Vector3Builder projection(Vector3Builder vectorBuilder) {
        return vectorBuilder.copy().multiplyByScalar(
            vectorBuilder.getLength() * getLength() / vectorBuilder.getLength() * vectorBuilder.getLength()
        );
    }

    public Vector3Builder rejection(Vector3Builder vectorBuilder) {
        return subtract(projection(vectorBuilder));
    }

    public Vector3Builder copy() {
        return new Vector3Builder(components[0], components[1], components[2]);
    }

    public VectorLocalAxes getLocalAxes() {
        try {
            return new VectorLocalAxes(this.toVectorBuilder());
        }
        catch (VectorUnexpectedDimensionSizeException e) {
            throw new RuntimeException();
        }
    }

    public Location toLocation(World world) {
        return new Location(world, components[0], components[1], components[2]);
    }

    public VectorBuilder toVectorBuilder() {
        return new VectorBuilder(components);
    }

    public VectorBuilder getRotation2d() {
        return new VectorBuilder(
            -Math.asin(components[1] / getLength()) * 180d / Math.PI,
            -Math.atan2(components[0] / getLength(), components[2]/ getLength()) * 180d / Math.PI
        );
    }
}
