package com.gmail.subnokoii78.util.vector;

import java.lang.annotation.*;

/**
 * このアノテーションが付与されたメソッドは破壊的、すなわちインスタンス自身を上書きする関数であることを意味します。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Destructive {}
