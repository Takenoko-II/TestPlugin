package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.function.TiFunction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * 三次元ベクトルを表現するクラス
 */
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
    public boolean equals(@NotNull Vector3Builder other) {
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
     * @return this
     */
    @Destructive
    public @NotNull Vector3Builder x(double value) {
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
     * @return this
     */
    @Destructive
    public @NotNull Vector3Builder y(double value) {
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
     * @return this
     */
    @Destructive
    public @NotNull Vector3Builder z(double value) {
        z = value;
        return this;
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder calculate(@NotNull UnaryOperator<Double> operator) {
        this.x = operator.apply(x);
        this.y = operator.apply(y);
        this.z = operator.apply(z);
        return this;
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder calculate(@NotNull Vector3Builder other, @NotNull BiFunction<Double, Double, Double> operator) {
        this.x = operator.apply(x, other.x);
        this.y = operator.apply(y, other.y);
        this.z = operator.apply(z, other.z);
        return this;
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder calculate(@NotNull Vector3Builder other1, @NotNull Vector3Builder other2, @NotNull TiFunction<Double, Double, Double, Double> operator) {
        this.x = operator.apply(x, other1.x, other2.x);
        this.y = operator.apply(y, other1.y, other2.y);
        this.z = operator.apply(z, other1.z, other2.z);
        return this;
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder add(@NotNull Vector3Builder other) {
        return calculate(other, Double::sum);
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder subtract(@NotNull Vector3Builder other) {
        return add(other.copy().invert());
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder scale(@NotNull Double scalar) {
        return calculate(component -> component * scalar);
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder invert() {
        return scale(-1d);
    }

    /**
     * このベクトルと別のベクトルとの内積を求めます。
     * @param other 別のベクトル
     * @return 二つのベクトルの内積
     */
    public double dot(@NotNull Vector3Builder other) {
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
    public @NotNull Vector3Builder cross(@NotNull Vector3Builder other) {
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
    @Destructive
    public @NotNull Vector3Builder length(double length) {
        final double previous = length();

        return calculate(component -> component / previous * length);
    }

    /**
     * このベクトルと別のベクトルとがなす角の大きさを求めます。
     * @param other 別のベクトル
     * @return 二つのベクトルがなす角の大きさ(度)
     */
    public double getAngleBetween(@NotNull Vector3Builder other) {
        final double p = this.dot(other) / (length() * other.length());

        return Math.acos(p) * 180 / Math.PI;
    }

    /**
     * このベクトルを正規化します。
     * @return このベクトル
     */
    @Destructive
    public @NotNull Vector3Builder normalize() {
        return length(1d);
    }

    /**
     * このベクトルから別のベクトルへの方向ベクトルを求めます。
     * @param other 別のベクトル
     * @return 方向ベクトル
     */
    public @NotNull Vector3Builder getDirectionTo(@NotNull Vector3Builder other) {
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
    public double getDistanceTo(@NotNull Vector3Builder other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z));
    }

    /**
     * このベクトルから別のベクトルへの射影を求めます。
     * @param other 別のベクトル
     * @return 射影ベクトル
     */
    public @NotNull Vector3Builder projection(@NotNull Vector3Builder other) {
        return other.copy().scale(
            other.length() * length() / other.length() * other.length()
        );
    }

    /**
     * このベクトルから別のベクトルへの反射影を求めます。
     * @param other 別のベクトル
     * @return 反射影ベクトル
     */
    public @NotNull Vector3Builder rejection(@NotNull Vector3Builder other) {
        return copy().subtract(projection(other));
    }

    /**
     * 法線ベクトルを受け取ってこのベクトルを反射させた新しいベクトルを返します。
     * @param normal 法線ベクトル
     * @return 反射ベクトル
     */
    public Vector3Builder reflect(@NotNull Vector3Builder normal) {
        return this.copy().calculate(normal, (a, b) -> a - 2 * this.dot(normal) * b);
    }

    /**
     * このベクトルを始点ベクトルとして、終点ベクトルへの線形補間を行います。
     * @param end 終点ベクトル
     * @param t 割合(0≦t≦1)
     * @return 線形補間したベクトル
     */
    public @NotNull Vector3Builder lerp(@NotNull Vector3Builder end, float t) {
        return copy().calculate(end, (a, b) -> (1 - t) * a + t * b);
    }

    /**
     * このベクトルを始点ベクトルとして、終点ベクトルへの球面線形補間を行います。
     * @param end 終点ベクトル
     * @param s 割合(0≦t≦1)
     * @return 球面線形補間したベクトル
     */
    public @NotNull Vector3Builder slerp(@NotNull Vector3Builder end, float s) {
        final double angle = this.getAngleBetween(end) * Math.PI / 180;

        final double p1 = Math.sin(angle * (1 - s)) / Math.sin(angle);
        final double p2 = Math.sin(angle * s) / Math.sin(angle);

        final Vector3Builder q1 = this.copy().scale(p1);
        final Vector3Builder q2 = end.copy().scale(p2);

        return q1.add(q2);
    }

    @Override
    @Destructive
    public @NotNull Vector3Builder clamp(@NotNull Vector3Builder min, @NotNull Vector3Builder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    /**
     * ベクトルを軸と角度に基づいて回転させます。
     * @param axis 回転軸
     * @param degree 角度
     * @return this
     */
    @Destructive
    public @NotNull Vector3Builder rotate(@NotNull Vector3Builder axis, float degree) {
        final double radian = degree * Math.PI / 180;
        final double sin = Math.sin(radian);
        final double cos = Math.cos(radian);

        final double x = axis.copy().normalize().x;
        final double y = axis.copy().normalize().y;
        final double z = axis.copy().normalize().z;

        final double[][] matrix = new double[][]{
            new double[]{cos + x * x * (1 - cos), x * y * (1 - cos) - z * sin, x * z * (1 - cos) + y * sin},
            new double[]{y * x * (1 - cos) + z * sin, cos + y * y * (1 - cos), y * z * (1 - cos) - x * sin},
            new double[]{z * x * (1 - cos) - y * sin, z * y * (1 - cos) + x * sin, cos + z * z * (1 - cos)}
        };

        this.x = matrix[0][0] * this.x + matrix[0][1] * this.y + matrix[0][2] * this.z;
        this.y = matrix[1][0] * this.x + matrix[1][1] * this.y + matrix[1][2] * this.z;
        this.z = matrix[2][0] * this.x + matrix[2][1] * this.y + matrix[2][2] * this.z;

        return this;
    }

    /**
     * このベクトルを指定の形式に従って文字列化します。形式には"$x", "$y", "$z", "$c"が使えます。
     * @return 文字列化されたベクトル
     */
    @Override
    public @NotNull String format(@NotNull String format) {
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
    public @NotNull String toString() {
        return format("($x, $y, $z)");
    }

    @Override
    public @NotNull Vector3Builder copy() {
        return new Vector3Builder(x, y, z);
    }

    @Override
    public boolean isZero() {
        return equals(new Vector3Builder());
    }

    /**
     * このベクトルをZ軸と考えたときのX, Y, Zの三軸を取得します。
     * @return ローカル軸
     */
    public @NotNull LocalAxisProvider getLocalAxisProvider() {
        return new LocalAxisProvider(this);
    }

    /**
     * 渡されたLocationの位置以外の情報を参照してこのベクトルの情報と合体させた新しいLocationを返します。
     * @param location 向き・ディメンションの情報が含まれたLocation
     * @return 新しいLocation
     */
    public @NotNull Location withLocation(@NotNull Location location) {
        return new Location(location.getWorld(), x(), y(), z(), location.getYaw(), location.getPitch());
    }

    /**
     * 渡された情報を参照してこのベクトルの情報と合体させた新しいLocationを返します。
     * @param rotation 向きの情報
     * @param world ディメンションの情報
     * @return 新しいLocation
     */
    public @NotNull Location withRotationAndWorld(@NotNull DualAxisRotationBuilder rotation, @NotNull World world) {
        return new Location(world, x(), y(), z(), rotation.yaw(), rotation.pitch());
    }

    /**
     * 渡されたディメンションを参照してこのベクトルの情報と合体させた新しいLocationを返します。
     * @param world ディメンションの情報
     * @return 向きが(0, 0)の新しいLocation
     */
    public @NotNull Location withWorld(@NotNull World world) {
        return new Location(world, x(), y(), z(), 0, 0);
    }

    /**
     * このベクトルを回転に変換します。
     * @return 回転
     */
    public @NotNull DualAxisRotationBuilder getRotation2d() {
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
    public @NotNull Vector3Builder selectClosest(@NotNull Vector3Builder... points) {
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
    public @NotNull org.bukkit.util.Vector toBukkitVector() {
        return new org.bukkit.util.Vector(x, y, z);
    }

    /**
     * org.bukkit.util.Vectorをこのクラスに変換します。
     * @param vector 変換するベクトル
     * @return 変換されたベクトル
     */
    public static @NotNull Vector3Builder from(@NotNull org.bukkit.util.Vector vector) {
        return new Vector3Builder(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Locationの位置の情報からこのクラスのインスタンスを作成します。
     * @param location 参照するLocation
     * @return 作成されたベクトル
     */
    public static @NotNull Vector3Builder from(@NotNull Location location) {
        return new Vector3Builder(location.x(), location.y(), location.z());
    }

    /**
     * 渡されたエンティティの位置を参照してこのクラスのインスタンスを作成します。
     * @param entity 参照するエンティティ
     * @return 作成されたベクトル
     */
    public static @NotNull Vector3Builder from(@NotNull Entity entity) {
        return Vector3Builder.from(entity.getLocation());
    }

    /**
     * 渡されたブロックとブロックの面の情報からこのクラスのインスタンスを作成します。
     * @param block ブロックの情報
     * @param blockFace ブロックの面の情報
     * @return 作成されたベクトル
     */
    public static @NotNull Vector3Builder from(@NotNull Block block, @NotNull BlockFace blockFace) {
        return Vector3Builder.from(block.getLocation())
        .add(new Vector3Builder(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()));
    }

    /**
     * 渡された二つのベクトルから最小のベクトルを作成します。
     * @param a 一つ目のベクトル
     * @param b 二つ目のベクトル
     * @return 作成されたベクトル
     */
    public static @NotNull Vector3Builder min(@NotNull Vector3Builder a, @NotNull Vector3Builder b) {
        return a.copy().calculate(b, Math::min);
    }

    /**
     * 渡された二つのベクトルから最大のベクトルを作成します。
     * @param a 一つ目のベクトル
     * @param b 二つ目のベクトル
     * @return 作成されたベクトル
     */
    public static @NotNull Vector3Builder max(@NotNull Vector3Builder a, @NotNull Vector3Builder b) {
        return a.copy().calculate(b, Math::max);
    }

    /**
     * ローカル軸を取得するためのクラス
     */
    public static class LocalAxisProvider {
        private final Vector3Builder forward;

        /**
         * 渡されたベクトルをZ軸ベクトルとして、X, Y, Zの三軸を作成します。
         * @param forward Z軸ベクトル
         */
        protected LocalAxisProvider(@NotNull Vector3Builder forward) {
            this.forward = forward;
        }

        /**
         * X軸を取得します。
         * @return X軸ベクトル
         */
        public @NotNull Vector3Builder getX() {
            final Vector3Builder z = getZ();
            return new Vector3Builder(z.z(), 0, -z.x()).normalize();
        }

        /**
         * Y軸を取得します。
         * @return Y軸ベクトル
         */
        public @NotNull Vector3Builder getY() {
            return getZ().cross(getX());
        }

        /**
         * Z軸を取得します。
         * @return Z軸ベクトル
         */
        public @NotNull Vector3Builder getZ() {
            return forward.copy().normalize();
        }
    }
}
