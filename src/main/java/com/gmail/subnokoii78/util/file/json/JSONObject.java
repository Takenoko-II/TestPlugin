package com.gmail.subnokoii78.util.file.json;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class JSONObject extends JSONValue<Map<String, Object>> {
    public JSONObject() {
        super(new HashMap<>());
    }

    public JSONObject(@NotNull Map<String, Object> map) {
        super(map);
    }

    public boolean has(@NotNull String key) {
        return value.containsKey(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public JSONValueType<?> getTypeOf(@NotNull String key) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JSONValueType.of(value.get(key));
    }

    public <T> T get(@NotNull String key, JSONValueType<T> type) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません");
        }

        return type.get(value.get(key));
    }

    public void set(@NotNull String key, Object value) {
        if (value instanceof JSONValue<?> jsonValue) {
            this.value.put(key, jsonValue.value);
            return;
        }

        this.value.put(key, value);
    }

    public void delete(@NotNull String key) {
        if (has(key)) value.remove(key);
    }

    public void clear() {
        value.clear();
    }

    public Set<String> keys() {
        return value.keySet();
    }

    public void merge(@NotNull JSONObject jsonObject) {
        for (String key : jsonObject.keys()) {
            set(key, jsonObject.value.get(key));
        }
    }

    public Map<String, Object> asMap() {
        final Map<String, Object> map = new HashMap<>();

        for (String key : keys()) {
            final JSONValueType<?> type = getTypeOf(key);

            if (type.equals(JSONValueType.OBJECT)) {
                final JSONObject object = get(key, JSONValueType.OBJECT);
                map.put(key, object.asMap());
            }
            else if (type.equals(JSONValueType.ARRAY)) {
                final JSONArray array = get(key, JSONValueType.ARRAY);
                map.put(key, array.asList());
            }
            else {
                map.put(key, value.get(key));
            }
        }

        return map;
    }
}
