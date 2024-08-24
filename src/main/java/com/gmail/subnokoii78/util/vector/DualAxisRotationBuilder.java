package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.function.TiFunction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * ヨー角・ピッチ角による回転を表現するクラス
 */
public class DualAxisRotationBuilder implements VectorBuilder<DualAxisRotationBuilder, Float> {
    private float yaw, pitch;

    /**
     * 三次元零回転を作成します。
     */
    public DualAxisRotationBuilder() {}

    /**
     * 三次元回転を作成します。
     * @param yaw X成分
     * @param pitch Y成分
     */
    public DualAxisRotationBuilder(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public boolean equals(@NotNull DualAxisRotationBuilder other) {
        return yaw == other.yaw
            && pitch == other.pitch;
    }

    /**
     * この回転のX成分(横回転)の値を返します。
     * @return X成分の値
     */
    public float yaw() {
        return yaw;
    }

    /**
     * この回転のX成分(横回転)の値を変更します。
     * @param value 新しい値
     * @return this
     */
    @Destructive
    public @NotNull DualAxisRotationBuilder yaw(float value) {
        yaw = value;
        return this;
    }

    /**
     * この回転のY成分(縦回転)の値を返します。
     * @return Y成分の値
     */
    public float pitch() {
        return pitch;
    }

    /**
     * この回転のY成分(縦回転)の値を変更します。
     * @param value 新しい値
     * @return this
     */
    @Destructive
    public @NotNull DualAxisRotationBuilder pitch(float value) {
        pitch = value;
        return this;
    }

    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder calculate(@NotNull UnaryOperator<Float> operator) {
        yaw = operator.apply(yaw);
        pitch = operator.apply(pitch);
        return this;
    }

    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder calculate(@NotNull DualAxisRotationBuilder other, @NotNull BiFunction<Float, Float, Float> operator) {
        yaw = operator.apply(yaw, other.yaw);
        pitch = operator.apply(pitch, other.pitch);
        return this;
    }

    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder calculate(@NotNull DualAxisRotationBuilder other1, @NotNull DualAxisRotationBuilder other2, @NotNull TiFunction<Float, Float, Float, Float> operator) {
        this.yaw = operator.apply(yaw, other1.yaw, other2.yaw);
        this.pitch = operator.apply(pitch, other1.pitch, other2.pitch);
        return this;
    }

    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder add(@NotNull DualAxisRotationBuilder addend) {
        return calculate(addend, Float::sum);
    }

    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder subtract(@NotNull DualAxisRotationBuilder subtrahend) {
        return add(subtrahend.copy().invert());
    }

    /**
     * この回転を実数倍します。
     * @param scalar 倍率
     * @return this
     */
    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder scale(@NotNull Float scalar) {
        return calculate(component -> component * scalar);
    }

    /**
     * この回転を逆向きにします。
     * @return this
     */
    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder invert() {
        final var rot = getDirection3d().invert().getRotation2d();
        yaw(rot.yaw);
        pitch(rot.pitch);
        return this;
    }

    @Override
    @Destructive
    public @NotNull DualAxisRotationBuilder clamp(@NotNull DualAxisRotationBuilder min, @NotNull DualAxisRotationBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    @Override
    public @NotNull String format(@NotNull String format) {
        final String yawStr = String.format("%.2f", yaw());
        final String pitchStr = String.format("%.2f", pitch());

        return format
        .replaceAll("\\$x", yawStr)
        .replaceAll("\\$y", pitchStr)
        .replaceFirst("\\$c", yawStr)
        .replaceFirst("\\$c", pitchStr)
        .replaceAll("\\$c", "");
    }

    @Override
    public @NotNull String toString() {
        return format("($x, $y)");
    }

    @Override
    public @NotNull DualAxisRotationBuilder copy() {
        return new DualAxisRotationBuilder(yaw, pitch);
    }

    @Override
    public boolean isZero() {
        return equals(new DualAxisRotationBuilder());
    }

    /**
     * この回転と別の回転がなす角の大きさを求めます。
     * @param rotation 別の回転
     * @return 角の大きさ(度)
     */
    public double getAngleBetween(DualAxisRotationBuilder rotation) {
        return getDirection3d().getAngleBetween(rotation.getDirection3d());
    }

    /**
     * この回転を単位ベクトルに変換します。
     * @return 単位ベクトル
     */
    public Vector3Builder getDirection3d() {
        final double x = -Math.sin(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);
        final double y = -Math.sin(pitch * Math.PI / 180);
        final double z = Math.cos(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);

        return new Vector3Builder(x, y, z);
    }

    public Location toLocation(Location location) {
        return new Location(location.getWorld(), location.x(), location.y(), location.z(), yaw, pitch);
    }

    public Location toLocation(Vector3Builder coordinate, World world) {
        return new Location(world, coordinate.x(), coordinate.y(), coordinate.z(), yaw, pitch);
    }

    public static DualAxisRotationBuilder from(Location location) {
        return new DualAxisRotationBuilder(location.getYaw(), location.getPitch());
    }

    public static DualAxisRotationBuilder from(Entity entity) {
        return DualAxisRotationBuilder.from(entity.getLocation());
    }
}
