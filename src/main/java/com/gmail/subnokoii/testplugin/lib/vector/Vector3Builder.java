package com.gmail.subnokoii.testplugin.lib.vector;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public class Vector3Builder implements VectorBuilder {
    private final double[] components;

    private final DimensionSize dimensionSize = new DimensionSize(3);

    /**
     * 三次元零ベクトルを作成します。
     */
    public Vector3Builder() {
        components = new double[]{0d, 0d, 0d};
    }

    /**
     * 三次元ベクトルを作成します。
     * @param x X成分
     * @param y Y成分
     * @param z Z成分
     */
    public Vector3Builder(double x, double y, double z) {
        components = new double[]{x, y, z};
    }

    /**
     * 三次元ベクトルを作成します。
     * @param components 全成分
     */
    public Vector3Builder(double... components) throws DimensionSizeMismatchException {
        if (components.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        this.components = new double[3];
        System.arraycopy(components, 0, this.components, 0, components.length);
    }

    /**
     * 特定のインデックスの成分の値を返します。
     * @param index インデックス
     * @return 成分の値
     */
    @Override
    public double getComponent(int index) {
        if (index > components.length - 1) {
            throw new DimensionSizeMismatchException();
        }

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
     * @return このベクトル
     */
    @Override
    public Vector3Builder setComponent(int index, double component) {
        if (index > 2) {
            throw new DimensionSizeMismatchException();
        }

        components[index] = component;

        return this;
    }

    /**
     * 全成分の値を変更します。
     * @param components 全成分の値
     * @return このベクトル
     */
    @Override
    public Vector3Builder setAllComponents(double... components) {
        if (components.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        System.arraycopy(components, 0, this.components, 0, components.length);

        return this;
    }

    /**
     * このベクトルのX成分の値を返します。
     * @return X成分の値
     */
    public double x() {
        return components[0];
    }

    /**
     * このベクトルのX成分の値を変更します。
     * @param value 新しい値
     */
    public void x(double value) {
        components[0] = value;
    }

    /**
     * このベクトルのY成分の値を返します。
     * @return Y成分の値
     */
    public double y() {
        return components[1];
    }

    /**
     * このベクトルのY成分の値を変更します。
     * @param value 新しい値
     */
    public void y(double value) {
        components[1] = value;
    }

    /**
     * このベクトルのZ成分の値を返します。
     * @return Z成分の値
     */
    public double z() {
        return components[2];
    }

    /**
     * このベクトルのZ成分の値を変更します。
     * @param value 新しい値
     */
    public void z(double value) {
        components[2] = value;
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
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Override
    public Vector3Builder calc(UnaryOperator<Double> operator) {
        return (Vector3Builder) VectorBuilder.super.calc(operator);
    }

    /**
     * 引数に渡されたベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    public Vector3Builder calc(Vector3Builder other, BiFunction<Double, Double, Double> operator) {
        return (Vector3Builder) VectorBuilder.super.calc(other, operator);
    }

    /**
     * このベクトルと別のベクトルとの内積を求めます。
     * @param vector3 別のベクトル
     * @return 二つのベクトルの内積
     */
    public double dot(Vector3Builder vector3) {
        final double x1 = x();
        final double y1 = y();
        final double z1 = z();
        final double x2 = vector3.x();
        final double y2 = vector3.y();
        final double z2 = vector3.z();

        return x1 * x2 + y1 * y2 + z1 * z2;
    }

    /**
     * このベクトルと別のベクトルとの外積を求めます。
     * @param vector3 別のベクトル
     * @return 二つのベクトルの外積ベクトル
     */
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

        return calc(component -> component / previous * length);
    }

    /**
     * このベクトルと別のベクトルとがなす角の大きさを求めます。
     * @param vector3 別のベクトル
     * @return 二つのベクトルがなす角の大きさ(度)
     */
    public double getAngleBetween(Vector3Builder vector3) {
        double p = this.dot(vector3) / (length() * vector3.length());

        return Math.acos(p) * 180 / Math.PI;
    }

    /**
     * このベクトルを正規化します。
     * @return このベクトル
     */
    public Vector3Builder normalized() {
        return length(1d);
    }

    /**
     * このベクトルに別のベクトルを足します。
     * @param vector3 別のベクトル
     * @return このベクトル
     */
    public Vector3Builder add(Vector3Builder vector3) {
        return calc(vector3, Double::sum);
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
     * @param vector3 別のベクトル
     * @return このベクトル
     */
    public Vector3Builder subtract(Vector3Builder vector3) {
        return add(vector3.copy().inverted());
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
    public Vector3Builder scale(double scalar) {
        return calc(component -> component * scalar);
    }

    /**
     * このベクトルの全成分を一つの値で埋めます。
     * @param value 任意の値
     * @return このベクトル
     */
    public Vector3Builder fill(double value) {
        return calc((component) -> value);
    }

    /**
     * このベクトルの向きを逆向きにします。
     * @return このベクトル
     */
    public Vector3Builder inverted() {
        return scale(-1d);
    }

    /**
     * このベクトルから別のベクトルへの方向ベクトルを求めます。
     * @param vector3 別のベクトル
     * @return 方向ベクトル
     */
    public Vector3Builder getDirectionTo(Vector3Builder vector3) {
        if (this.equals(vector3)) {
            throw new IllegalArgumentException("2つのベクトルは完全に一致しています");
        }

        return vector3.copy()
        .subtract(this)
        .normalized();
    }

    /**
     * このベクトルと別のベクトルとの距離を求めます。
     * @param other 別のベクトル
     * @return 距離
     */
    public double getDistanceBetween(Vector3Builder other) {
        double sumOfSquare = 0d;

        for (int i = 0; i < 3; i++) {
            final double a = components[i];
            final double b = other.components[i];
            sumOfSquare += (a - b) * (a - b);
        }

        return Math.sqrt(sumOfSquare);
    }

    /**
     * このベクトルから別のベクトルへの射影を求めます。
     * @param vector3 別のベクトル
     * @return 射影ベクトル
     */
    public Vector3Builder projection(Vector3Builder vector3) {
        return vector3.copy().scale(
            vector3.length() * length() / vector3.length() * vector3.length()
        );
    }

    /**
     * このベクトルから別のベクトルへの反射影を求めます。
     * @param vector3 別のベクトル
     * @return 反射影ベクトル
     */
    public Vector3Builder rejection(Vector3Builder vector3) {
        return subtract(projection(vector3));
    }

    /**
     * このベクトルを始点ベクトルとして、終点ベクトルへの線形補間を行います。
     * @param end 終点ベクトル
     * @param t 割合(0≦t≦1)
     * @return 線形補間したベクトル
     */
    public Vector3Builder lerp(Vector3Builder end, float t) {
        final BiFunction<Double, Double, Double> linear = (Double a, Double b) -> (1 - t) * a + t * b;

        return new Vector3Builder(
            linear.apply(x(), end.x()),
            linear.apply(y(), end.y()),
            linear.apply(z(), end.z())
        );
    }

    /**
     * このベクトルを始点ベクトルとして、終点ベクトルへの球面線形補間を行います。
     * @param end 終点ベクトル
     * @param s 割合(0≦t≦1)
     * @return 球面線形補間したベクトル
     */
    public Vector3Builder slerp(Vector3Builder end, float s) {
        final double angle = this.getAngleBetween(end);
        final double radian = angle * Math.PI / 180;

        final double p1 = Math.sin(radian * (1 - s)) / Math.sin(radian);
        final double p2 = Math.sin(radian * s) / Math.sin(radian);

        final Vector3Builder q1 = this.copy().scale(p1);
        final Vector3Builder q2 = end.copy().scale(p2);

        return q1.add(q2);
    }

    /**
     * このベクトルを指定の形式に従って文字列化します。形式には"$x", "$y", "$z", "$c"が使えます。
     * @return 文字列化されたベクトル
     */
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

    /**
     * このベクトルを文字列化します。
     * @return 文字列化されたベクトル
     */
    @Override
    public String toString() {
        return toString("($x, $y, $z)");
    }

    /**
     * このベクトルのコピーを返します。
     * @return コピーされたベクトル
     */
    @Override
    public Vector3Builder copy() {
        return new Vector3Builder(x(), y(), z());
    }

    /**
     * このベクトルをZ軸と考えたときのX, Y, Zの三軸を取得します。
     * @return ローカル軸
     */
    public LocalAxes getLocalAxes() {
        return new LocalAxes(this);
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
    public Location withRotationAndWorld(RotationBuilder rotation, World world) {
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
     * このベクトルをjava.util.Vectorに変換します。
     * @return 変換されたベクトル
     */
    public Vector<Double> toVector() {
        final Vector<Double> vec = new Vector<>();
        vec.add(x());
        vec.add(y());
        vec.add(z());

        return vec;
    }

    /**
     * このベクトルを回転に変換します。
     * @return 回転
     */
    public RotationBuilder getRotation2d() {
        return new RotationBuilder(
            -Math.atan2(x() / length(), z() / length()) * 180d / Math.PI,
            -Math.asin(y() / length()) * 180d / Math.PI
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
            if (distance > getDistanceBetween(points[i])) {
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
        return new org.bukkit.util.Vector(x(), y(), z());
    }

    /**
     * java.util.Vectorをこのクラスに変換します。
     * @param vector 変換するベクトル
     * @return 変換されたベクトル
     */
    public static Vector3Builder from(Vector<Double> vector) {
        if (vector.size() != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(vector.get(0), vector.get(1), vector.get(2));
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
     * 長さが3の配列をこのクラスに変換します。
     * @param array 配列
     * @return 変換されたベクトル
     */
    public static Vector3Builder from(double[] array) {
        if (array.length != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(array[0], array[1], array[2]);
    }

    /**
     * 長さが3のリストをこのクラスに変換します。
     * @param list リスト
     * @return 変換されたベクトル
     */
    public static Vector3Builder from(List<Double> list) {
        if (list.size() != 3) {
            throw new DimensionSizeMismatchException();
        }

        return new Vector3Builder(list.get(0), list.get(1), list.get(2));
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
        return a.copy().calc(b, Math::min);
    }

    /**
     * 渡された二つのベクトルから最大のベクトルを作成します。
     * @param a 一つ目のベクトル
     * @param b 二つ目のベクトル
     * @return 作成されたベクトル
     */
    public static Vector3Builder max(Vector3Builder a, Vector3Builder b) {
        return a.copy().calc(b, Math::max);
    }

    public static final class LocalAxes {
        private final Vector3Builder x;

        private final Vector3Builder y;

        private final Vector3Builder z;

        /**
         * 渡されたベクトルをZ軸ベクトルとして、X, Y, Zの三軸を作成します。
         * @param forward Z軸ベクトル
         */
        public LocalAxes(Vector3Builder forward) {
            z = forward.copy().normalized();

            x = new Vector3Builder(z.z(), 0, -z.x()).normalized();

            y = z.cross(x);
        }

        /**
         * X軸を取得します。
         * @return X軸ベクトル
         */
        public Vector3Builder getX() {
            return x.copy();
        }

        /**
         * Y軸を取得します。
         * @return Y軸ベクトル
         */
        public Vector3Builder getY() {
            return y.copy();
        }

        /**
         * Z軸を取得します。
         * @return Z軸ベクトル
         */
        public Vector3Builder getZ() {
            return z.copy();
        }
    }
}
