package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.takenokoii78.json.JSONFile;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONValue;
import com.gmail.takenokoii78.json.JSONValueType;
import com.gmail.takenokoii78.json.values.JSONObject;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

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
            write(path, defaultValue, true);
            return defaultValue;
        }
    }

    public static void write(@NotNull JSONPath path, @NotNull Object value, boolean forced) {
        final JSONObject obj = getRootObject();
        obj.set(path, value);
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);

        if (!file.exists()) {
            file.create();
        }

        if (forced) file.write(obj);
        else if (getRootObject().has(path)) {
            file.write(obj);
        }
        else {
            throw new IllegalArgumentException("存在しないパスのため値を設定できませんでした: 既存のキーのみ変更可能です");
        }
    }

    public static void reload() {
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);

        TPLCore.getPlugin().getComponentLogger().info(Component.text(TestPlugin.CONFIG_FILE_PATH + " がリロードされました"));

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
