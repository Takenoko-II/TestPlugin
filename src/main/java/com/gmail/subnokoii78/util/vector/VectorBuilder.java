package com.gmail.subnokoii78.util.vector;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

public interface VectorBuilder<T extends VectorBuilder<T, U>, U extends Number> {
    /**
     * 二つのベクトルが等しいかどうかを確かめます。
     * @param other もう一方のベクトル
     * @return 一致していれば真
     */
    boolean equals(T other);

    /**
     * このベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    T calculate(UnaryOperator<U> operator);

    /**
     * 引数に渡されたベクトルとこのベクトルのそれぞれの成分に対して関数を呼び出し、その結果で成分の値を上書きします。
     * @param operator 関数
     * @return このベクトル
     */
    T calculate(T other, BiFunction<U, U, U> operator);

    /**
     * 引数に渡された値との足し算を行って自身を返します。
     * @param other 他のインスタンス
     * @return このインスタンス自身
     */
    T add(T other);

    /**
     * 引数に渡された値との引き算を行って自身を返します。
     * @param other 他のインスタンス
     * @return このインスタンス自身
     */
    T subtract(T other);

    /**
     * 実数倍して自身を返します。
     * @param scalar 実数
     * @return このインスタンス自身
     */
    T scale(U scalar);

    /**
     * ベクトルの向きを逆向きにして自身を返します。
     * @return このインスタンス自身
     */
    T invert();

    /**
     * このベクトルを形式に従って文字列化します。
     * @return 文字列化されたベクトル
     */
    String format(String format);

    /**
     * このベクトルを文字列化します。
     * @return 文字列化されたベクトル
     */
    @Override
    String toString();

    /**
     * このベクトルのコピーを返します。
     * @return コピーされたベクトル
     */
    T copy();
}
