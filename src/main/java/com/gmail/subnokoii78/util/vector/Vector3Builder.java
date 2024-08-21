package com.gmail.subnokoii78.util.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class Vector3Builder implements VectorBuilder<Vector3Builder, Double> {
    private double x, y, z;

    /**
     * 三次元零ベクトルを作成します。
     */
    public Vector3Builder() {}

    /**
     * 三次元ベクトルを作成します。
     * @param x X成分
     * @param y Y成分
     * @param z Z成分
     */
    public Vector3Builder(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Vector3Builder other) {
        return x == other.x
            && y == other.y
            && z == other.z;
    }

    /**
     * このベクトルのX成分の値を返します。
     * @return X成分の値
     */
    public double x() {
        return x;
    }

    /**
     * このベクトルのX成分の値を変更します。
     * @param value 新しい値
     * @return
     */
    public Vector3Builder x(double value) {
        x = value;
        return this;
    }

    /**
     * このベクトルのY成分の値を返します。
     * @return Y成分の値
     */
    public double y() {
        return y;
    }

    /**
     * このベクトルのY成分の値を変更します。
     * @param value 新しい値
     * @return
     */
    public Vector3Builder y(double value) {
        y = value;
        return this;
    }

    /**
     * このベクトルのZ成分の値を返します。
     * @return Z成分の値
     */
    public double z() {
        return z;
    }

    /**
     * このベクトルのZ成分の値を変更します。
     *
     * @param value 新しい値
     * @return
     */
    public Vector3Builder z(double value) {
        z = value;
        return this;
    }

    @Override
    public Vector3Builder calculate(UnaryOperator<Double> operator) {
        this.x = operator.apply(x);
        this.y = operator.apply(y);
        this.z = operator.apply(z);
        return this;
    }

    @Override
    public Vector3Builder calculate(Vector3Builder other, BiFunction<Double, Double, Double> operator) {
        this.x = operator.apply(x, other.x);
        this.y = operator.apply(y, other.y);
        this.z = operator.apply(z, other.z);
        return this;
    }

    /**
     * このベクトルに別のベクトルを足します。
     * @param other 別のベクトル
     * @return このベクトル
     */
    public Vector3Builder add(Vector3Builder other) {
        return calculate(other, Double::sum);
    }

    /**
     * このベクトルに別のベクトルを足します。
     * @param x 別のベクトルのX成分
     * @param y 別のベクトルのY成分
     * @param z 別のベクトルのZ成分
     * @return このベクトル
     */
    public Vector3Builder add(double x, double y, double z) {
        return add(new Vector3Builder(x, y, z));
    }

    /**
     * このベクトルから別のベクトルを引きます。
     * @param other 別のベクトル
     * @return このベクトル
     */
    public Vector3Builder subtract(Vector3Builder other) {
        return add(other.copy().invert());
    }

    /**
     * このベクトルから別のベクトルを引きます。
     * @param x 別のベクトルのX成分
     * @param y 別のベクトルのY成分
     * @param z 別のベクトルのZ成分
     * @return このベクトル
     */
    public Vector3Builder subtract(double x, double y, double z) {
        return subtract(new Vector3Builder(x, y, z));
    }

    /**
     * このベクトルを実数倍します。
     * @param scalar 倍率
     * @return このベクトル
     */
    @Override
    public Vector3Builder scale(@NotNull Double scalar) {
        return calculate(component -> component * scalar);
    }

    /**
     * このベクトルの向きを逆向きにします。
     * @return このベクトル
     */
    public Vector3Builder invert() {
        return scale(-1d);
    }

    /**
     * このベクトルと別のベクトルとの内積を求めます。
     * @param other 別のベクトル
     * @return 二つのベクトルの内積
     */
    public double dot(Vector3Builder other) {
        final double x2 = other.x();
        final double y2 = other.y();
        final double z2 = other.z();

        return x * x2 + y * y2 + z * z2;
    }

    /**
     * このベクトルと別のベクトルとの外積を求めます。
     * @param other 別のベクトル
     * @return 二つのベクトルの外積ベクトル
     */
    public Vector3Builder cross(Vector3Builder other) {
        final double x2 = other.x();
        final double y2 = other.y();
        final double z2 = other.z();

        return new Vector3Builder(
            y * z2 - z * y2,
            z * x2 - x * z2,
            x * y2 - y * x2
        );
    }

    /**
     * このベクトルの長さを返します。
     * @return ベクトルの長さ
     */
    public double length() {
        return Math.sqrt(dot(this));
    }

    /**
     * 向きはそのままに、このベクトルの長さを変更します。
     * @param length 新しい長さ
     * @return このベクトル
     */
    public Vector3Builder length(double length) {
        final double previous = length();

        return calculate(component -> component / previous * length);
    }

    /**
     * このベクトルと別のベクトルとがなす角の大きさを求めます。
     * @param other 別のベクトル
     * @return 二つのベクトルがなす角の大きさ(度)
     */
    public double getAngleBetween(Vector3Builder other) {
        final double p = this.dot(other) / (length() * other.length());

        return Math.acos(p) * 180 / Math.PI;
    }

    /**
     * このベクトルを正規化します。
     * @return このベクトル
     */
    public Vector3Builder normalize() {
        return length(1d);
    }

    /**
     * このベクトルから別のベクトルへの方向ベクトルを求めます。
     * @param other 別のベクトル
     * @return 方向ベクトル
     */
    public Vector3Builder getDirectionTo(Vector3Builder other) {
        if (this.equals(other)) {
            throw new IllegalArgumentException("2つのベクトルは完全に一致しています");
        }

        return other.copy()
        .subtract(this)
        .normalize();
    }

    /**
     * このベクトルと別のベクトルとの距離を求めます。
     * @param other 別のベクトル
     * @return 距離
     */
    public double getDistanceTo(Vector3Builder other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z));
    }

    /**
     * このベクトルから別のベクトルへの射影を求めます。
     * @param other 別のベクトル
     * @return 射影ベクトル
     */
    public Vector3Builder projection(Vector3Builder other) {
        return other.copy().scale(
            other.length() * length() / other.length() * other.length()
        );
    }

    /**
     * このベクトルから別のベクトルへの反射影を求めます。
     * @param other 別のベクトル
     * @return 反射影ベクトル
     */
    public Vector3Builder rejection(Vector3Builder other) {
        return copy().subtract(projection(other));
    }

    /**
     * このベクトルを始点ベクトルとして、終点ベクトルへの線形補間を行います。
     * @param end 終点ベクトル
     * @param t 割合(0≦t≦1)
     * @return 線形補間したベクトル
     */
    public Vector3Builder lerp(Vector3Builder end, float t) {
        return copy().calculate(end, (a, b) -> (1 - t) * a + t * b);
    }

    /**
     * このベクトルを始点ベクトルとして、終点ベクトルへの球面線形補間を行います。
     * @param end 終点ベクトル
     * @param s 割合(0≦t≦1)
     * @return 球面線形補間したベクトル
     */
    public Vector3Builder slerp(Vector3Builder end, float s) {
        final double angle = this.getAngleBetween(end) * Math.PI / 180;

        final double p1 = Math.sin(angle * (1 - s)) / Math.sin(angle);
        final double p2 = Math.sin(angle * s) / Math.sin(angle);

        final Vector3Builder q1 = this.copy().scale(p1);
        final Vector3Builder q2 = end.copy().scale(p2);

        return q1.add(q2);
    }

    /**
     * このベクトルを指定の形式に従って文字列化します。形式には"$x", "$y", "$z", "$c"が使えます。
     * @return 文字列化されたベクトル
     */
    @Override
    public String format(String format) {
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

    /**
     * このベクトルを文字列化します。
     * @return 文字列化されたベクトル
     */
    @Override
    public String toString() {
        return format("($x, $y, $z)");
    }

    /**
     * このベクトルのコピーを返します。
     * @return コピーされたベクトル
     */
    @Override
    public Vector3Builder copy() {
        return new Vector3Builder(x, y, z);
    }

    /**
     * このベクトルをZ軸と考えたときのX, Y, Zの三軸を取得します。
     * @return ローカル軸
     */
    public LocalAxisProvider getLocalAxisProvider() {
        return new LocalAxisProvider(this);
    }

    /**
     * 渡されたLocationの位置以外の情報を参照してこのベクトルの情報と合体させた新しいLocationを返します。
     * @param location 向き・ディメンションの情報が含まれたLocation
     * @return 新しいLocation
     */
    public Location withLocation(Location location) {
        return new Location(location.getWorld(), x(), y(), z(), location.getYaw(), location.getPitch());
    }

    /**
     * 渡された情報を参照してこのベクトルの情報と合体させた新しいLocationを返します。
     * @param rotation 向きの情報
     * @param world ディメンションの情報
     * @return 新しいLocation
     */
    public Location withRotationAndWorld(DualAxisRotationBuilder rotation, World world) {
        return new Location(world, x(), y(), z(), rotation.yaw(), rotation.pitch());
    }

    /**
     * 渡されたディメンションを参照してこのベクトルの情報と合体させた新しいLocationを返します。
     * @param world ディメンションの情報
     * @return 向きが(0, 0)の新しいLocation
     */
    public Location withWorld(World world) {
        return new Location(world, x(), y(), z(), 0, 0);
    }

    /**
     * このベクトルを回転に変換します。
     * @return 回転
     */
    public DualAxisRotationBuilder getRotation2d() {
        return new DualAxisRotationBuilder(
            (float) (-Math.atan2(x() / length(), z() / length()) * 180d / Math.PI),
            (float) (-Math.asin(y() / length()) * 180d / Math.PI)
        );
    }

    /**
     * 渡されたベクトルの中からこのベクトルに最も距離が近いものを返します。
     * @deprecated この関数はすぐに削除される可能性が極めて高いため、使用を推奨しません。
     * @return 選ばれたベクトル
     */
    @ApiStatus.Experimental
    public Vector3Builder selectClosest(Vector3Builder... points) {
        if (points.length == 0) {
            throw new IllegalArgumentException("配列の長さが0です");
        }

        int index = -1;
        double distance = Double.POSITIVE_INFINITY;

        for (int i = 0; i < points.length; i++) {
            if (distance > getDistanceTo(points[i])) {
                index = i;
            }
        }

        return points[index];
    }

    /**
     * このベクトルをorg.bukkit.util.Vectorに変換します。
     * @return 変換されたベクトル
     */
    public org.bukkit.util.Vector toBukkitVector() {
        return new org.bukkit.util.Vector(x, y, z);
    }

    /**
     * org.bukkit.util.Vectorをこのクラスに変換します。
     * @param vector 変換するベクトル
     * @return 変換されたベクトル
     */
    public static Vector3Builder from(org.bukkit.util.Vector vector) {
        return new Vector3Builder(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Locationの位置の情報からこのクラスのインスタンスを作成します。
     * @param location 参照するLocation
     * @return 作成されたベクトル
     */
    public static Vector3Builder from(Location location) {
        return new Vector3Builder(location.x(), location.y(), location.z());
    }

    /**
     * 渡されたエンティティの位置を参照してこのクラスのインスタンスを作成します。
     * @param entity 参照するエンティティ
     * @return 作成されたベクトル
     */
    public static Vector3Builder from(Entity entity) {
        return Vector3Builder.from(entity.getLocation());
    }

    /**
     * 渡されたブロックとブロックの面の情報からこのクラスのインスタンスを作成します。
     * @param block ブロックの情報
     * @param blockFace ブロックの面の情報
     * @return 作成されたベクトル
     */
    public static Vector3Builder from(Block block, BlockFace blockFace) {
        return Vector3Builder.from(block.getLocation())
        .add(new Vector3Builder(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()));
    }

    public static Vector3Builder from(BoundingBox box) {
        return new Vector3Builder(box.getWidthX() / 2, box.getHeight() / 2, box.getWidthZ() / 2);
    }

    /**
     * 渡された二つのベクトルから最小のベクトルを作成します。
     * @param a 一つ目のベクトル
     * @param b 二つ目のベクトル
     * @return 作成されたベクトル
     */
    public static Vector3Builder min(Vector3Builder a, Vector3Builder b) {
        return a.copy().calculate(b, Math::min);
    }

    /**
     * 渡された二つのベクトルから最大のベクトルを作成します。
     * @param a 一つ目のベクトル
     * @param b 二つ目のベクトル
     * @return 作成されたベクトル
     */
    public static Vector3Builder max(Vector3Builder a, Vector3Builder b) {
        return a.copy().calculate(b, Math::max);
    }

    public static class LocalAxisProvider {
        private final Vector3Builder forward;

        /**
         * 渡されたベクトルをZ軸ベクトルとして、X, Y, Zの三軸を作成します。
         * @param forward Z軸ベクトル
         */
        public LocalAxisProvider(Vector3Builder forward) {
            this.forward = forward;
        }

        /**
         * X軸を取得します。
         * @return X軸ベクトル
         */
        public Vector3Builder getX() {
            final Vector3Builder z = getZ();
            return new Vector3Builder(z.z(), 0, -z.x()).normalize();
        }

        /**
         * Y軸を取得します。
         * @return Y軸ベクトル
         */
        public Vector3Builder getY() {
            return getZ().cross(getX());
        }

        /**
         * Z軸を取得します。
         * @return Z軸ベクトル
         */
        public Vector3Builder getZ() {
            return forward.copy().normalize();
        }
    }
}
