package com.gmail.subnokoii78.util.vector;

import org.joml.Quaternionf;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class EntireAxisRotationHandler implements VectorBuilder {
    private final Quaternionf quaternion = new Quaternionf(0f, 0f, 0f, 1f);

    private float yaw = 0, pitch = 0, roll = 0;

    public EntireAxisRotationHandler(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        update();
    }

    public EntireAxisRotationHandler() {}

    @Override
    public double getComponent(int index) {
        return switch (index) {
            case 0 -> yaw;
            case 1 -> pitch;
            case 2 -> roll;
            default -> throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public double[] getAllComponents() {
        return new double[]{yaw, pitch, roll};
    }

    @Override
    public EntireAxisRotationHandler setComponent(int index, double component) {
        switch (index) {
            case 0 -> yaw = (float) component;
            case 1 -> pitch = (float) component;
            case 2 -> roll = (float) component;
            default -> throw new IndexOutOfBoundsException();
        }
        return this;
    }

    @Override
    public EntireAxisRotationHandler setAllComponents(double... components) {
        if (components.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        yaw = (float) components[0];
        pitch = (float) components[1];
        roll = (float) components[2];

        return this;
    }

    public String toString(String format) {
        final String yaw = String.format("%.2f", this.yaw);
        final String pitch = String.format("%.2f", this.pitch);
        final String roll = String.format("%.2f", this.roll);

        return format
            .replaceAll("\\$x", yaw)
            .replaceAll("\\$y", pitch)
            .replaceAll("\\$y", roll)
            .replaceFirst("\\$c", yaw)
            .replaceFirst("\\$c", pitch)
            .replaceFirst("\\$c", roll)
            .replaceAll("\\$c", "");
    }

    @Override
    public String toString() {
        return toString("($x, $y, $z)");
    }

    @Override
    public DimensionSize getDimensionSize() {
        return new DimensionSize(3);
    }

    @Override
    public EntireAxisRotationHandler copy() {
        return new EntireAxisRotationHandler(yaw, pitch, roll);
    }

    /**
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Override
    public EntireAxisRotationHandler calc(UnaryOperator<Double> operator) {
        for (int i = 0; i < getAllComponents().length; i++) {
            setComponent(i, operator.apply(getComponent(i)));
        }

        return this;
    }

    /**
     * 引数に渡されたベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    public EntireAxisRotationHandler calc(EntireAxisRotationHandler other, BiFunction<Double, Double, Double> operator) {
        for (int i = 0; i < getAllComponents().length; i++) {
            setComponent(i, operator.apply(getComponent(i), other.getComponent(i)));
        }

        return this;
    }

    private void rotateQuaternion(Vector3Builder axis, float angle) {
        final Vector3Builder normalized = axis.copy().normalized();

        quaternion.rotateAxis(
            (float) (angle * Math.PI / 180),
            (float) normalized.x(),
            (float) normalized.y(),
            (float) normalized.z()
        );
    }

    private void update() {
        final Vector3Builder.LocalAxes axes = new DualAxisRotationHandler(yaw, pitch).getDirection3d().getLocalAxes();
        quaternion.x = 0;
        quaternion.y = 0;
        quaternion.z = 0;
        quaternion.w = 1;

        rotateQuaternion(axes.getZ(), roll);
        rotateQuaternion(axes.getX(), pitch);
        rotateQuaternion(new Vector3Builder(0, 1, 0), -(yaw + 90));
    }

    public EntireAxisRotationHandler add(EntireAxisRotationHandler rotation) {
        this.yaw += rotation.yaw;
        this.pitch += rotation.pitch;
        this.roll += rotation.roll;
        update();
        return this;
    }

    public EntireAxisRotationHandler subtract(EntireAxisRotationHandler rotation) {
        return add(rotation.copy().inverted());
    }

    public EntireAxisRotationHandler inverted() {
        this.yaw *= -1;
        this.pitch *= -1;
        this.roll *= -1;
        return this;
    }

    public LocalAxesE getLocalAxesE() {
        return new LocalAxesE(this);
    }

    public DualAxisRotationHandler getRotation2d() {
        return new DualAxisRotationHandler(yaw, pitch);
    }

    public Vector3Builder getDirection3d() {
        return getRotation2d().getDirection3d();
    }

    public Quaternionf getQuaternion4d() {
        return new Quaternionf(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public static EntireAxisRotationHandler from(DualAxisRotationHandler rotation) {
        return new EntireAxisRotationHandler(rotation.yaw(), rotation.pitch(), 0);
    }

    public static final class LocalAxesE extends Vector3Builder.LocalAxes {
        private final double[][] matrix;

        private LocalAxesE(EntireAxisRotationHandler rotation) {
            super(rotation.getDirection3d());

            final double radian = rotation.roll * Math.PI / 180;
            final double sin = Math.sin(radian);
            final double cos = Math.cos(radian);
            final Vector3Builder axis = super.getZ();
            final double x = axis.x();
            final double y = axis.y();
            final double z = axis.z();

            this.matrix = new double[][]{
                new double[]{cos + x * x * (1 - cos), x * y * (1 - cos) - z * sin, x * z * (1 - cos) + y * sin},
                new double[]{y * x * (1 - cos) + z * sin, cos + y * y * (1 - cos), y * z * (1 - cos) - x * sin},
                new double[]{z * x * (1 - cos) - y * sin, z * y * (1 - cos) + x * sin, cos + z * z * (1 - cos)}
            };
        }

        private Vector3Builder rotate(Vector3Builder vector3) {
            final double x = matrix[0][0] * vector3.x() + matrix[0][1] * vector3.y() + matrix[0][2] * vector3.z();
            final double y = matrix[1][0] * vector3.x() + matrix[1][1] * vector3.y() + matrix[1][2] * vector3.z();
            final double z = matrix[2][0] * vector3.x() + matrix[2][1] * vector3.y() + matrix[2][2] * vector3.z();
            return new Vector3Builder(x, y, z);
        }

        public Vector3Builder getX() {
            return rotate(super.getX());
        }

        public Vector3Builder getY() {
            return rotate(super.getY());
        }

        public Vector3Builder getZ() {
            return super.getZ();
        }
    }
}
