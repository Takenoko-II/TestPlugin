package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class RotationBuilder implements VectorBuilder {
    private final double[] components;

    private final DimensionSize dimensionSize = new DimensionSize(2);

    public RotationBuilder() {
        components = new double[]{0d, 0d};
    }

    public RotationBuilder(double x, double y) {
        components = new double[]{x, y};
    }

    public RotationBuilder(double... allComponents) {
        if (allComponents.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        final double[] newArray = new double[2];
        System.arraycopy(allComponents, 0, newArray, 0, allComponents.length);

        components = newArray;
    }

    public double getComponent(int index) {
        return components[index];
    }

    public double[] getAllComponents() {
        return components.clone();
    }

    public RotationBuilder setComponent(int index, double component) {
        if (index > 1) {
            throw new DimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    public RotationBuilder setAllComponents(double[] componentsList) {
        if (componentsList.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        System.arraycopy(componentsList, 0, components, 0, componentsList.length);

        return this;
    }

    public float yaw() {
        return (float) components[0];
    }

    public RotationBuilder yaw(float value) {
        components[0] = value;

        return this;
    }

    public float pitch() {
        return (float) components[1];
    }

    public RotationBuilder pitch(float value) {
        components[1] = value;

        return this;
    }

    public DimensionSize getDimensionSize() {
        return dimensionSize;
    }

    @Override
    public RotationBuilder calc(UnaryOperator<Double> operator) {
        return (RotationBuilder) VectorBuilder.super.calc(operator);
    }

    public RotationBuilder calc(RotationBuilder other, BiFunction<Double, Double, Double> operator) {
        return (RotationBuilder) VectorBuilder.super.calc(other, operator);
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

    public RotationBuilder scale(double scalar) {
        return calc(component -> component * scalar);
    }

    public RotationBuilder inverted() {
        return scale(-1d);
    }

    public RotationBuilder fill(double value) {
        return calc(component -> value);
    }

    public RotationBuilder add(RotationBuilder addend) {
        return calc(addend, Double::sum);
    }

    public RotationBuilder subtract(RotationBuilder subtrahend) {
        return add(subtrahend.copy().inverted());
    }

    public String toString(String format) {
        final String yaw = String.format("%.2f", yaw());
        final String pitch = String.format("%.2f", pitch());

        return format
        .replaceAll("\\$x", yaw)
        .replaceAll("\\$y", pitch)
        .replaceFirst("\\$c", yaw)
        .replaceFirst("\\$c", pitch)
        .replaceAll("\\$c", "");
    }

    @Override
    public String toString() {
        return toString("($x, $y)");
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

    public static RotationBuilder from(Vector<Double> vector) {
        if (vector.size() != 2) {
            throw new DimensionSizeMismatchException();
        }

        return new RotationBuilder(vector.get(0), vector.get(1));
    }

    public static RotationBuilder from(Location location) {
        return new RotationBuilder(location.getYaw(), location.getPitch());
    }

    public static RotationBuilder from(Entity entity) {
        return RotationBuilder.from(entity.getLocation());
    }

    public static RotationBuilder from(double[] array) {
        if (array.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        return new RotationBuilder(array[0], array[1]);
    }

    public static RotationBuilder from(List<Double> list) {
        if (list.size() != 2) {
            throw new DimensionSizeMismatchException();
        }

        return new RotationBuilder(list.get(0), list.get(1));
    }
}
