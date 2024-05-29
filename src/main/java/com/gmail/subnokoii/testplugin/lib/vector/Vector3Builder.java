package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class Vector3Builder implements VectorBuilder {
    private final double[] components;

    private final DimensionSize dimensionSize = new DimensionSize(3);

    public Vector3Builder() {
        components = new double[]{0d, 0d, 0d};
    }

    public Vector3Builder(double x, double y, double z) {
        components = new double[]{x, y, z};
    }

    public Vector3Builder(double... allComponents) throws DimensionSizeMismatchException {
        if (allComponents.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        components = new double[3];
        System.arraycopy(allComponents, 0, components, 0, allComponents.length);
    }

    public double getComponent(int index) {
        if (index > components.length - 1) {
            throw new DimensionSizeMismatchException();
        }

        return components[index];
    }

    public double[] getAllComponents() {
        return components.clone();
    }

    public Vector3Builder setComponent(int index, double component) {
        if (index > 2) {
            throw new DimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    public Vector3Builder setAllComponents(double[] allComponents) {
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

    public DimensionSize getDimensionSize() {
        return dimensionSize;
    }

    @Override
    public Vector3Builder calc(UnaryOperator<Double> operator) {
        return (Vector3Builder) VectorBuilder.super.calc(operator);
    }

    public Vector3Builder calc(Vector3Builder other, BiFunction<Double, Double, Double> operator) {
        return (Vector3Builder) VectorBuilder.super.calc(other, operator);
    }

    public double dot(Vector3Builder vector3) {
        final double x1 = x();
        final double y1 = y();
        final double z1 = z();
        final double x2 = vector3.x();
        final double y2 = vector3.y();
        final double z2 = vector3.z();

        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    public Vector3Builder cross(Vector3Builder vector3) {
        final double x1 = x();
        final double y1 = y();
        final double z1 = z();
        final double x2 = vector3.x();
        final double y2 = vector3.y();
        final double z2 = vector3.z();

        return new Vector3Builder(
            y1 * z2 - z1 * y2,
            z1 * x2 - x1 * z2,
            x1 * y2 - y1 * x2
        );
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    public Vector3Builder length(double length) {
        final double previous = length();

        return calc(component -> component / previous * length);
    }

    public double getAngleBetween(Vector3Builder vector3) {
        double p = this.dot(vector3) / (length() * vector3.length());

        return Math.acos(p) * 180 / Math.PI;
    }

    public Vector3Builder normalized() {
        return length(1d);
    }

    public Vector3Builder add(Vector3Builder vector3) {
        return calc(vector3, Double::sum);
    }

    public Vector3Builder add(double x, double y, double z) {
        return add(new Vector3Builder(x, y, z));
    }

    public Vector3Builder subtract(Vector3Builder vector3) {
        return add(vector3.inverted());
    }

    public Vector3Builder subtract(double x, double y, double z) {
        return subtract(new Vector3Builder(x, y, z));
    }

    public Vector3Builder scale(double scalar) {
        return calc(component -> component * scalar);
    }

    public Vector3Builder fill(double value) {
        return calc((component) -> value);
    }

    public Vector3Builder inverted() {
        return scale(-1d);
    }

    public Vector3Builder directionTo(Vector3Builder vector3) {
        return vector3.copy()
        .subtract(this)
        .normalized();
    }

    public double getDistanceBetween(Vector3Builder vector3) {
        double summaryOfSquare = 0d;

        for (int i = 0; i < 3; i++) {
            final double a = components[i];
            final double b = vector3.getComponent(i);
            summaryOfSquare += (a - b) * (a - b);
        }

        return Math.sqrt(summaryOfSquare);
    }

    public Vector3Builder projection(Vector3Builder vector3) {
        return vector3.copy().scale(
            vector3.length() * length() / vector3.length() * vector3.length()
        );
    }

    public Vector3Builder rejection(Vector3Builder vector3) {
        return subtract(projection(vector3));
    }

    public Vector3Builder lerp(Vector3Builder end, float t) {
        final BiFunction<Double, Double, Double> linear = (Double a, Double b) -> (1 - t) * a + t * b;

        return new Vector3Builder(
            linear.apply(x(), end.x()),
            linear.apply(y(), end.y()),
            linear.apply(z(), end.z())
        );
    }

    public Vector3Builder slerp(Vector3Builder end, float s) {
        final double angle = this.getAngleBetween(end);
        final double radian = angle * Math.PI / 180;

        final double p1 = Math.sin(radian * (1 - s)) / Math.sin(radian);
        final double p2 = Math.sin(radian * s) / Math.sin(radian);

        final Vector3Builder q1 = this.copy().scale(p1);
        final Vector3Builder q2 = end.copy().scale(p2);

        return q1.add(q2);
    }

    public String toString(String format) {
        final String x = String.format("%.2f", x());
        final String y = String.format("%.2f", y());
        final String z = String.format("%.2f", z());

        return format
        .replaceAll("\\$x", x)
        .replaceAll("\\$y", y)
        .replaceAll("\\$z", z)
        .replaceFirst("\\$c", x)
        .replaceFirst("\\$c", y)
        .replaceFirst("\\$c", z)
        .replaceAll("\\$c", "");
    }

    @Override
    public String toString() {
        return toString("($x, $y, $z)");
    }

    public Vector3Builder copy() {
        return new Vector3Builder(x(), y(), z());
    }

    public LocalAxes getLocalAxes() {
        return new LocalAxes(this);
    }

    public Location toLocation(Location location) {
        return new Location(location.getWorld(), x(), y(), z(), location.getYaw(), location.getPitch());
    }

    public Location toLocation(RotationBuilder rotation, World world) {
        return new Location(world, x(), y(), z(), rotation.yaw(), rotation.pitch());
    }

    public Location toLocation(World world) {
        return new Location(world, x(), y(), z(), 0, 0);
    }

    public Vector<Double> toVector() {
        final Vector<Double> vec = new Vector<>();
        vec.add(x());
        vec.add(y());
        vec.add(z());

        return vec;
    }

    public RotationBuilder getRotation2d() {
        return new RotationBuilder(
            -Math.asin(y() / length()) * 180d / Math.PI,
            -Math.atan2(x() / length(), z() / length()) * 180d / Math.PI
        );
    }

    public org.bukkit.util.Vector toBukkitVector() {
        return new org.bukkit.util.Vector(x(), y(), z());
    }

    public static Vector3Builder from(Vector<Double> vector) {
        if (vector.size() != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(vector.get(0), vector.get(1), vector.get(2));
    }

    public static Vector3Builder from(org.bukkit.util.Vector vector) {
        return new Vector3Builder(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector3Builder from(Location location) {
        return new Vector3Builder(location.x(), location.y(), location.z());
    }

    public static Vector3Builder from(Entity entity) {
        return Vector3Builder.from(entity.getLocation());
    }

    public static Vector3Builder from(double[] array) {
        if (array.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(array[0], array[1], array[2]);
    }

    public static Vector3Builder from(List<Double> list) {
        if (list.size() != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(list.get(0), list.get(1), list.get(2));
    }

    public static Vector3Builder from(Block block, BlockFace blockFace) {
        return Vector3Builder.from(block.getLocation())
        .add(new Vector3Builder(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()));
    }

    public static Vector3Builder min(Vector3Builder a, Vector3Builder b) {
        return a.copy().calc(b, Math::min);
    }

    public static Vector3Builder max(Vector3Builder a, Vector3Builder b) {
        return a.copy().calc(b, Math::max);
    }

    public static final class LocalAxes {
        private final Vector3Builder x;
        private final Vector3Builder y;
        private final Vector3Builder z;

        public LocalAxes(Vector3Builder forward) {
            z = forward.copy().normalized();

            x = new Vector3Builder(z.z(), 0, -z.x()).normalized();

            y = z.cross(x);
        }

        public Vector3Builder getX() {
            return x.copy();
        }

        public Vector3Builder getY() {
            return y.copy();
        }

        public Vector3Builder getZ() {
            return z.copy();
        }
    }
}
