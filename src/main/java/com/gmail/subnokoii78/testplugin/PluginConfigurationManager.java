package com.gmail.subnokoii78.testplugin;

import com.gmail.takenokoii78.json.JSONFile;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONValue;
import com.gmail.takenokoii78.json.JSONValueType;
import com.gmail.takenokoii78.json.values.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PluginConfigurationManager {
    private PluginConfigurationManager() {}

    private static JSONObject jsonObject;

    public static @NotNull JSONObject getRootObject() {
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);
        final JSONObject defaultObj = new JSONObject();

        if (!file.exists()) {
            file.create();
            file.write(defaultObj);
        }

        return jsonObject == null ? defaultObj : jsonObject;
    }

    public static <T extends JSONValue<?>> T getOrWriteDefault(@NotNull JSONPath path, @NotNull JSONValueType<T> type, T defaultValue) {
        if (getRootObject().has(path)) {
            return getRootObject().get(path, type);
        }
        else {
            write(path, defaultValue);
            return defaultValue;
        }
    }

    public static void write(@NotNull JSONPath path, @NotNull Object value) {
        final JSONObject obj = getRootObject();
        obj.set(path, value);
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);

        if (!file.exists()) {
            file.create();
        }

        file.write(obj);
    }

    public static void reload() {
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);

        if (file.exists()) {
            jsonObject = file.readAsObject();
        }
        else {
            file.create();
            jsonObject = new JSONObject();
            file.write(jsonObject);
        }
    }
}
