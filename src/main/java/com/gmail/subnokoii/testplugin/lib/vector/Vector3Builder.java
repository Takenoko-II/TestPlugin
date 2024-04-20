package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Vector3Builder implements VectorBuilder {
    private final double[] components;

    private final VectorBuilderDimensionSize dimensionSize;

    public Vector3Builder() {
        components = new double[]{0d, 0d, 0d};
        dimensionSize = new VectorBuilderDimensionSize(3);
    }

    public Vector3Builder(double x, double y, double z) {
        components = new double[]{x, y, z};
        dimensionSize = new VectorBuilderDimensionSize(3);
    }

    public Vector3Builder(double[] allComponents) throws UnexpectedDimensionSizeException {
        if (allComponents.length != 3) {
            throw new UnexpectedDimensionSizeException();
        }

        double[] newArray = new double[3];
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

    public Vector3Builder setComponent(int index, double component) throws DimensionSizeMismatchException {
        if (index > 2) {
            throw new DimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    public Vector3Builder setAllComponents(double[] allComponents) throws DimensionSizeMismatchException {
        if (allComponents.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        System.arraycopy(allComponents, 0, components, 0, allComponents.length);

        return this;
    }

    public double x() {
        return components[0];
    }

    public void x(double value) {
        components[0] = value;
    }

    public double y() {
        return components[1];
    }

    public void y(double value) {
        components[1] = value;
    }

    public double z() {
        return components[2];
    }

    public void z(double value) {
        components[2] = value;
    }

    public VectorBuilderDimensionSize getDimensionSize() {
        return dimensionSize;
    }

    public double dot(Vector3Builder vector3) {
        double summaryOfMultiple = 0d;
        for (int i = 0; i < 3; i++) {
            summaryOfMultiple += components[i] * vector3.components[i];
        }

        return summaryOfMultiple;
    }

    public Vector3Builder cross(Vector3Builder vector3) {
        final double x1 = components[0];
        final double y1 = components[1];
        final double z1 = components[2];
        final double x2 = vector3.components[0];
        final double y2 = vector3.components[1];
        final double z2 = vector3.components[2];

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

    public double getAngleBetween(Vector3Builder vector3) {

        double p = this.dot(vector3) / (getLength() * vector3.getLength());

        return Math.acos(p) * 180 / Math.PI;
    }

    public Vector3Builder normalized() {
        return setLength(1d);
    }

    public Vector3Builder add(Vector3Builder vector3) {

        for (int i = 0; i < 3; i++) {
            components[i] += vector3.components[i];
        }

        return this;
    }

    public Vector3Builder subtract(Vector3Builder vector3) {
        return copy().add(vector3.copy().inverted());
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

    public Vector3Builder directionTo(Vector3Builder vector3) {
        return vector3.copy()
        .subtract(this)
        .normalized();
    }

    public double distanceWith(Vector3Builder vector3) {
        double summaryOfSquare = 0d;

        for (int i = 0; i < 3; i++) {
            final double a = components[i];
            final double b = vector3.getComponent(i);
            summaryOfSquare += (a - b) * (a - b);
        }

        return Math.sqrt(summaryOfSquare);
    }

    public Vector3Builder projection(Vector3Builder vector3) {
        return vector3.copy().multiplyByScalar(
            vector3.getLength() * getLength() / vector3.getLength() * vector3.getLength()
        );
    }

    public Vector3Builder rejection(Vector3Builder vector3) {
        return subtract(projection(vector3));
    }

    public Vector3Builder copy() {
        return new Vector3Builder(components[0], components[1], components[2]);
    }

    public Vector3LocalAxes getLocalAxes() {
        return new Vector3LocalAxes(this);
    }

    public Location toLocation(World world) {
        return new Location(world, components[0], components[1], components[2]);
    }

    public Vector<Double> toVector() {
        final Vector<Double> vec = new Vector<>();
        vec.add(components[0]);
        vec.add(components[1]);
        vec.add(components[2]);

        return vec;
    }

    public RotationBuilder getRotation2d() {
        return new RotationBuilder(
            -Math.asin(components[1] / getLength()) * 180d / Math.PI,
            -Math.atan2(components[0] / getLength(), components[2]/ getLength()) * 180d / Math.PI
        );
    }

    public static Vector3Builder from(Vector<Double> vector) {
        return new Vector3Builder(vector.get(0), vector.get(1), vector.get(2));
    }

    public static Vector3Builder from(Location location) {
        return new Vector3Builder(location.x(), location.y(), location.z());
    }

    public static Vector3Builder from(double[] array) throws DimensionSizeMismatchException {
        if (array.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(array[0], array[1], array[2]);
    }

    public static Vector3Builder from(List<Double> list) throws DimensionSizeMismatchException {
        if (list.size() != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(list.get(0), list.get(1), list.get(2));
    }
}
