package com.gmail.subnokoii78.util.file.json;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class JSONValueType<T> {
    private final Function<Object, T> getter;

    private JSONValueType(Function<Object, T> getter) {
        this.getter = getter;
    }

    T get(Object value) {
        return getter.apply(value);
    }

    public static JSONValueType<?> of(Object value) {
        return switch (value) {
            case Boolean v -> JSONValueType.BOOLEAN;
            case Number v -> JSONValueType.NUMBER;
            case String v -> JSONValueType.STRING;
            case Map<?, ?> v -> JSONValueType.OBJECT;
            case List<?> v -> JSONValueType.ARRAY;
            case Set<?> v -> JSONValueType.ARRAY;
            case Stream<?> v -> JSONValueType.ARRAY;
            case boolean[] v -> JSONValueType.ARRAY;
            case byte[] v -> JSONValueType.ARRAY;
            case short[] v -> JSONValueType.ARRAY;
            case int[] v -> JSONValueType.ARRAY;
            case long[] v -> JSONValueType.ARRAY;
            case float[] v -> JSONValueType.ARRAY;
            case double[] v -> JSONValueType.ARRAY;
            case char[] v -> JSONValueType.ARRAY;
            case Object[] v -> JSONValueType.ARRAY;
            case null -> JSONValueType.NULL;
            case JSONValue<?> v -> of(v.value);
            default -> throw new IllegalArgumentException("渡された値はjsonで使用できない型です: " + value.getClass().getName());
        };
    }

    public static final JSONValueType<Boolean> BOOLEAN = new JSONValueType<>(value -> {
        if (value instanceof Boolean v) return v;
        else throw new IllegalArgumentException("value is not a boolean value");
    });

    public static final JSONValueType<Number> NUMBER = new JSONValueType<>(value -> {
        if (value instanceof Number v) return v;
        else throw new IllegalArgumentException("value is not a number value");
    });

    public static final JSONValueType<String> STRING = new JSONValueType<>(value -> {
        if (value instanceof String v) return v;
        else throw new IllegalArgumentException("value is not a string value");
    });

    public static final JSONValueType<JSONObject> OBJECT = new JSONValueType<>(value -> {
        if (value instanceof Map<?, ?> v) return new JSONObject((Map<String, Object>) v);
        else throw new IllegalArgumentException("value is not a json object value");
    });

    public static final JSONValueType<JSONArray> ARRAY = new JSONValueType<>(value -> {
        if (value instanceof List<?> v) return new JSONArray((List<Object>) v);
        else if (value instanceof Set<?> v) return new JSONArray((List<Object>) v.stream().toList());
        else if (value instanceof Stream<?> v) return new JSONArray((List<Object>) v.toList());
        else if (value instanceof boolean[] v) {
            List<Object> l = new ArrayList<>();
            for (boolean b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof byte[] v) {
            List<Object> l = new ArrayList<>();
            for (byte b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof short[] v) {
            List<Object> l = new ArrayList<>();
            for (short b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof int[] v) {
            List<Object> l = new ArrayList<>();
            for (int b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof long[] v) {
            List<Object> l = new ArrayList<>();
            for (long b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof float[] v) {
            List<Object> l = new ArrayList<>();
            for (float b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof double[] v) {
            List<Object> l = new ArrayList<>();
            for (double b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof char[] v) {
            List<Object> l = new ArrayList<>();
            for (char b : v) l.add(b);
            return new JSONArray(l);
        }
        else if (value instanceof Object[] v) return new JSONArray(Arrays.stream(v).toList());
        else throw new IllegalArgumentException("value is not a json array value");
    });

    public static final JSONValueType<JSONNull> NULL = new JSONValueType<>(value -> {
        if (value == null) return new JSONNull();
        else throw new IllegalArgumentException("value is not a null value");
    });
}
