package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.function.TiFunction;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * ヨー角・ピッチ角・ロール角による回転を表現するクラス
 */
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

    @Destructive
    public TripleAxisRotationBuilder yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float pitch() {
        return pitch;
    }

    @Destructive
    public TripleAxisRotationBuilder pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float roll() {
        return roll;
    }

    @Destructive
    public TripleAxisRotationBuilder roll(float roll) {
        this.roll = roll;
        return this;
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder calculate(@NotNull UnaryOperator<Float> operator) {
        yaw = operator.apply(yaw);
        pitch = operator.apply(pitch);
        roll = operator.apply(roll);
        return this;
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder calculate(@NotNull TripleAxisRotationBuilder other, @NotNull BiFunction<Float, Float, Float> operator) {
        yaw = operator.apply(yaw, other.yaw);
        pitch = operator.apply(pitch, other.pitch);
        roll = operator.apply(roll, other.roll);
        return this;
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder calculate(@NotNull TripleAxisRotationBuilder other1, @NotNull TripleAxisRotationBuilder other2, @NotNull TiFunction<Float, Float, Float, Float> operator) {
        this.yaw = operator.apply(yaw, other1.yaw, other2.yaw);
        this.pitch = operator.apply(pitch, other1.pitch, other2.pitch);
        this.roll = operator.apply(roll, other1.roll, other2.roll);
        return this;
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder add(@NotNull TripleAxisRotationBuilder other) {
        calculate(other, Float::sum);
        return this;
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder subtract(@NotNull TripleAxisRotationBuilder other) {
        return add(other.copy().invert());
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder scale(@NotNull Float scalar) {
        return calculate(component -> component * scalar);
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder invert() {
        final var rot = getDirection3d().invert().getRotation2d();
        yaw(rot.yaw());
        pitch(rot.pitch());
        return this;
    }

    @Override
    @Destructive
    public @NotNull TripleAxisRotationBuilder clamp(@NotNull TripleAxisRotationBuilder min, @NotNull TripleAxisRotationBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    @Override
    public @NotNull String format(@NotNull String format) {
        final String yaw = String.format("%.2f", this.yaw);
        final String pitch = String.format("%.2f", this.pitch);
        final String roll = String.format("%.2f", this.roll);

        return format
            .replaceAll("\\$x", yaw)
            .replaceAll("\\$y", pitch)
            .replaceAll("\\$z", roll)
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

    @Override
    public boolean isZero() {
        return equals(new TripleAxisRotationBuilder());
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
