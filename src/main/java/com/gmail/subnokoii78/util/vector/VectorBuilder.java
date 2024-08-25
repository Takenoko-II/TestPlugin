package com.gmail.subnokoii78.util.vector;

import com.gmail.subnokoii78.util.function.TiFunction;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * ベクトルを表現するinterface
 * @param <T> このinterfaceの実装クラス
 * @param <U> 扱う数値型
 */
public interface VectorBuilder<T extends VectorBuilder<T, U>, U extends Number> {
    /**
     * 二つのベクトルが等しいかどうかを確かめます。
     * @param other もう一方のベクトル
     * @return 一致していれば真
     */
    boolean equals(@NotNull T other);

    /**
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Destructive
    @NotNull T calculate(@NotNull UnaryOperator<U> operator);

    /**
     * 引数に渡されたベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    @Destructive
    @NotNull T calculate(@NotNull T other, @NotNull BiFunction<U, U, U> operator);

    /**
     * 引数に渡された2つのベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     */
    @Destructive
    @NotNull T calculate(@NotNull T other1, @NotNull T other2, @NotNull TiFunction<U, U, U, U> operator);

    /**
     * 引数に渡された値との足し算を行って自身を返します。
     * @param other 他のインスタンス
     * @return このインスタンス自身
     */
    @Destructive
    @NotNull T add(@NotNull T other);

    /**
     * 引数に渡された値との引き算を行って自身を返します。
     * @param other 他のインスタンス
     * @return このインスタンス自身
     */
    @Destructive
    @NotNull T subtract(@NotNull T other);

    /**
     * このベクトルの各成分に引数に渡された値を掛けて自身を返します。
     * @param scalar 実数
     * @return このインスタンス自身
     */
    @Destructive
    @NotNull T scale(@NotNull U scalar);

    /**
     * ベクトルの向きを逆向きにして自身を返します。
     * @return このインスタンス自身
     */
    @Destructive
    @NotNull T invert();

    /**
     * ベクトルの各成分の値の範囲を制限します。
     * @return this
     */
    @Destructive
    @NotNull T clamp(@NotNull T min, @NotNull T max);

    /**
     * このベクトルを形式に従って文字列化します。
     * @return 文字列化されたベクトル
     */
    @NotNull String format(@NotNull String format);

    /**
     * このベクトルを文字列化します。
     * @return 文字列化されたベクトル
     */
    @Override
    @NotNull String toString();

    /**
     * このベクトルのコピーを返します。
     * @return コピーされたベクトル
     */
    @NotNull T copy();

    /**
     * このベクトルの全成分が0であれば真を返します。
     * @return components.every(v => v === 0);
     */
    boolean isZero();

    static void spawnFlameParticle(@NotNull Vector3Builder vector3Builder) {
        Bukkit.getWorlds().getFirst().spawnParticle(Particle.FLAME, vector3Builder.withWorld(Bukkit.getWorlds().getFirst()), 1, 0, 0, 0, 0);
    }
}
