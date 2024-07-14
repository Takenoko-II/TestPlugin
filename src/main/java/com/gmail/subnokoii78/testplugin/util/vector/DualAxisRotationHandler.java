package com.gmail.subnokoii78.testplugin.util.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class DualAxisRotationHandler implements VectorBuilder {
    private final double[] components;

    private final DimensionSize dimensionSize = new DimensionSize(2);

    /**
     * 三次元零回転を作成します。
     */
    public DualAxisRotationHandler() {
        components = new double[]{0d, 0d};
    }

    /**
     * 三次元回転を作成します。
     * @param x X成分
     * @param y Y成分
     */
    public DualAxisRotationHandler(double x, double y) {
        components = new double[]{x, y};
    }

    /**
     * 三次元回転を作成します。
     * @param components 全成分
     */
    public DualAxisRotationHandler(double... components) {
        if (components.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        final double[] newArray = new double[2];
        System.arraycopy(components, 0, newArray, 0, components.length);

        this.components = newArray;
    }

    /**
     * 特定のインデックスの成分の値を返します。
     * @param index インデックス
     * @return 成分の値
     */
    @Override
    public double getComponent(int index) {
        return components[index];
    }

    /**
     * 全成分の値を返します。
     * @return 全成分
     */
    @Override
    public double[] getAllComponents() {
        return components.clone();
    }

    /**
     * 特定のインデックスの成分の値を変更します。
     * @param index インデックス
     * @param component 成分の値
     * @return この回転
     */
    @Override
    public DualAxisRotationHandler setComponent(int index, double component) {
        if (index > 1) {
            throw new DimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    /**
     * 全成分の値を変更します。
     * @param components 全成分の値
     * @return この回転
     */
    @Override
    public DualAxisRotationHandler setAllComponents(double... components) {
        if (components.length != 2) {
            throw new DimensionSizeMismatchException();
        }

        System.arraycopy(components, 0, this.components, 0, components.length);

        return this;
    }

    /**
     * この回転のX成分の値を返します。
     * @return X成分の値
     */
    public float yaw() {
        return (float) components[0];
    }

    /**
     * この回転のX成分の値を変更します。
     * @param value 新しい値
     */
    public DualAxisRotationHandler yaw(float value) {
        components[0] = value;

        return this;
    }

    /**
     * この回転のY成分の値を返します。
     * @return Y成分の値
     */
    public float pitch() {
        return (float) components[1];
    }

    /**
     * この回転のY成分の値を変更します。
     * @param value 新しい値
     */
    public DualAxisRotationHandler pitch(float value) {
        components[1] = value;

        return this;
    }

    /**
     * このベクトルの次元の情報を返します。
     * @return 次元に関する情報
     */
    @Override
    public DimensionSize getDimensionSize() {
        return dimensionSize;
    }

    /**
     * この回転のそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return この回転
     */
    @Override
    public DualAxisRotationHandler calc(UnaryOperator<Double> operator) {
        return (DualAxisRotationHandler) VectorBuilder.super.calc(operator);
    }

    /**
     * 引数に渡された回転とこの回転のそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return この回転
     */
    public DualAxisRotationHandler calc(DualAxisRotationHandler other, BiFunction<Double, Double, Double> operator) {
        return (DualAxisRotationHandler) VectorBuilder.super.calc(other, operator);
    }

    /**
     * この回転を単位ベクトルに変換します。
     * @return 単位ベクトル
     */
    public Vector3Builder getDirection3d() {
        final double yaw = components[0];
        final double pitch = components[1];

        final double x = -Math.sin(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);
        final double y = -Math.sin(pitch * Math.PI / 180);
        final double z = Math.cos(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);

        return new Vector3Builder(x, y, z);
    }

    /**
     * この回転と別の回転がなす角の大きさを求めます。
     * @param rotation 別の回転
     * @return 角の大きさ(度)
     */
    public double getAngleBetween(DualAxisRotationHandler rotation) {
        return getDirection3d().getAngleBetween(rotation.getDirection3d());
    }

    /**
     * この回転を実数倍します。
     * @param scalar 倍率
     * @return この回転
     */
    public DualAxisRotationHandler scale(double scalar) {
        return calc(component -> component * scalar);
    }

    /**
     * この回転を逆向きにします。
     * @return この回転
     */
    public DualAxisRotationHandler inverted() {
        return scale(-1d);
    }

    public DualAxisRotationHandler fill(double value) {
        return calc(component -> value);
    }

    public DualAxisRotationHandler add(DualAxisRotationHandler addend) {
        return calc(addend, Double::sum);
    }

    public DualAxisRotationHandler subtract(DualAxisRotationHandler subtrahend) {
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

    public DualAxisRotationHandler copy() {
        return new DualAxisRotationHandler(components[0], components[1]);
    }

    public Location toLocation(Location location) {
        return new Location(location.getWorld(), location.x(), location.y(), location.z(), yaw(), pitch());
    }

    public Location toLocation(Vector3Builder coordinate, World world) {
        return new Location(world, coordinate.x(), coordinate.y(), coordinate.z(), yaw(), pitch());
    }

    public static DualAxisRotationHandler from(Location location) {
        return new DualAxisRotationHandler(location.getYaw(), location.getPitch());
    }

    public static DualAxisRotationHandler from(Entity entity) {
        return DualAxisRotationHandler.from(entity.getLocation());
    }
}
