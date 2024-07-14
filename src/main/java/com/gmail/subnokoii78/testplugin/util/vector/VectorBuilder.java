package com.gmail.subnokoii78.testplugin.util.vector;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface VectorBuilder {
    /**
     * 特定のインデックスの成分の値を返します。
     * @param index インデックス
     * @return 成分の値
     */
    double getComponent(int index);

    /**
     * 全成分の値を返します。
     * @return 全成分
     */
    double[] getAllComponents();

    /**
     * 特定のインデックスの成分の値を変更します。
     * @param index インデックス
     * @param component 成分の値
     * @return このベクトル
     */
    VectorBuilder setComponent(int index, double component);

    /**
     * 全成分の値を変更します。
     * @param components 全成分の値
     * @return このベクトル
     */
    VectorBuilder setAllComponents(double... components);

    /**
     * 二つのベクトルが等しいかどうかを確かめます。
     * @param other もう一方のベクトル
     * @return 一致していれば真
     */
    default boolean equals(VectorBuilder other) {
        for (int i = 0; i < getAllComponents().length; i++) {
            if (getComponent(i) != other.getComponent(i)) return false;
        }

        return true;
    }

    /**
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    default VectorBuilder calc(UnaryOperator<Double> operator) {
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
    default VectorBuilder calc(VectorBuilder other, BiFunction<Double, Double, Double> operator) {
        for (int i = 0; i < getAllComponents().length; i++) {
            setComponent(i, operator.apply(getComponent(i), other.getComponent(i)));
        }

        return this;
    }

    /**
     * このベクトルを文字列化します。
     * @return 文字列化されたベクトル
     */
    String toString();

    /**
     * このベクトルの次元の情報を返します。
     * @return 次元に関する情報
     */
    DimensionSize getDimensionSize();

    /**
     * このベクトルのコピーを返します。
     * @return コピーされたベクトル
     */
    VectorBuilder copy();

    /**
     * Vector3Builder, RotationBuilder, Worldの三つからLocationを作成します。
     * @param vector3 位置
     * @param vector2 回転
     * @param world ディメンション
     * @return 作成されたLocation
     */
    static Location merge(Vector3Builder vector3, DualAxisRotationHandler vector2, World world) {
        return new Location(world, vector3.x(), vector3.y(), vector3.z(), vector2.yaw(), vector2.pitch());
    }

    final class DimensionSize {
        private final int size;

        /**
         * 次元の大きさに関する情報を作成します。
         * @param size 次元の大きさ
         */
        public DimensionSize(int size) {
            this.size = size;
        }

        /**
         * このベクトルの次元の大きさを取得します。
         * @return 次元の大きさ
         */
        public int getValue() {
            return size;
        }

        /**
         * 渡されたベクトルとの次元が一致しているかどうかを確かめます。
         * @param vector 比較するベクトル
         * @return 一致していれば真
         */
        public boolean match(VectorBuilder vector) {
            return vector.getAllComponents().length == size;
        }
    }
}
