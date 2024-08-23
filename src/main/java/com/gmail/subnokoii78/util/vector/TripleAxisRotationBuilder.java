package com.gmail.subnokoii78.util.vector;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public final class TripleAxisRotationBuilder implements VectorBuilder<TripleAxisRotationBuilder, Float> {
    private float yaw, pitch, roll;

    public TripleAxisRotationBuilder(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public TripleAxisRotationBuilder() {
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;
    }

    @Override
    public boolean equals(@NotNull TripleAxisRotationBuilder other) {
        return yaw == other.yaw
            && pitch == other.pitch
            && roll == other.roll;
    }

    public float yaw() {
        return yaw;
    }

    public TripleAxisRotationBuilder yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float pitch() {
        return pitch;
    }

    public TripleAxisRotationBuilder pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float roll() {
        return roll;
    }

    public TripleAxisRotationBuilder roll(float roll) {
        this.roll = roll;
        return this;
    }

    /**
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Override
    public @NotNull TripleAxisRotationBuilder calculate(@NotNull UnaryOperator<Float> operator) {
        yaw = operator.apply(yaw);
        pitch = operator.apply(pitch);
        roll = operator.apply(roll);
        return this;
    }

    /**
     * 引数に渡されたベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Override
    public @NotNull TripleAxisRotationBuilder calculate(@NotNull TripleAxisRotationBuilder other, @NotNull BiFunction<Float, Float, Float> operator) {
        yaw = operator.apply(yaw, other.yaw);
        pitch = operator.apply(pitch, other.pitch);
        roll = operator.apply(roll, other.roll);
        return this;
    }

    public @NotNull TripleAxisRotationBuilder add(@NotNull TripleAxisRotationBuilder other) {
        calculate(other, Float::sum);
        return this;
    }

    public @NotNull TripleAxisRotationBuilder subtract(@NotNull TripleAxisRotationBuilder other) {
        return add(other.copy().invert());
    }

    public @NotNull TripleAxisRotationBuilder scale(@NotNull Float scalar) {
        return calculate(component -> component * scalar);
    }

    public @NotNull TripleAxisRotationBuilder invert() {
        return scale(-1f);
    }

    @Override
    public @NotNull String format(@NotNull String format) {
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
    public @NotNull String toString() {
        return format("($x, $y, $z)");
    }

    @Override
    public @NotNull TripleAxisRotationBuilder copy() {
        return new TripleAxisRotationBuilder(yaw, pitch, roll);
    }

    public LocalAxisProviderE getLocalAxisProviderE() {
        return new LocalAxisProviderE(this);
    }

    public DualAxisRotationBuilder getRotation2d() {
        return new DualAxisRotationBuilder(yaw, pitch);
    }

    public Vector3Builder getDirection3d() {
        return getRotation2d().getDirection3d();
    }

    private void rotateQuaternion(Quaternionf quaternion, Vector3Builder axis, float angle) {
        final Vector3Builder normalized = axis.copy().normalize();
        quaternion.rotateAxis(
            (float) (angle * Math.PI / 180),
            (float) normalized.x(),
            (float) normalized.y(),
            (float) normalized.z()
        );
    }

    public Quaternionf getQuaternion4d() {
        final var quaternion = new Quaternionf(0f, 0f, 0f, 1f);
        final var axisProvider = new DualAxisRotationBuilder(yaw, pitch).getDirection3d().getLocalAxisProvider();
        rotateQuaternion(quaternion, axisProvider.getZ(), roll);
        rotateQuaternion(quaternion, axisProvider.getX(), pitch);
        rotateQuaternion(quaternion, new Vector3Builder(0, 1, 0), -(yaw + 90));
        return quaternion;
    }

    public static TripleAxisRotationBuilder from(DualAxisRotationBuilder other) {
        return new TripleAxisRotationBuilder(other.yaw(), other.pitch(), 0);
    }

    public static final class LocalAxisProviderE extends Vector3Builder.LocalAxisProvider {
        private final double[][] matrix;

        private LocalAxisProviderE(TripleAxisRotationBuilder rotation) {
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

        public @NotNull Vector3Builder getX() {
            return rotate(super.getX());
        }

        public @NotNull Vector3Builder getY() {
            return rotate(super.getY());
        }

        public @NotNull Vector3Builder getZ() {
            return super.getZ();
        }
    }
}
