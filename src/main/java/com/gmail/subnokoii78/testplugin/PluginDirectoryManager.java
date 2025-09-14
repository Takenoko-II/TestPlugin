package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.util.datacontainer.FileDataContainerManager;
import com.gmail.subnokoii78.util.file.BinaryFileUtils;
import com.gmail.subnokoii78.util.file.FolderUtils;
import com.gmail.subnokoii78.util.file.TextFileUtils;
import com.gmail.subnokoii78.util.file.json.JSONFile;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueConverter;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

public final class PluginDirectoryManager {
    private PluginDirectoryManager() {}

    public static void init() {
        deleteLegacy();
        FolderUtils.create(TestPlugin.PERSISTENT_DIRECTORY_PATH);
        TextFileUtils.create(TestPlugin.LOG_FILE_PATH);
        BinaryFileUtils.create(TestPlugin.DATABASE_FILE_PATH);
        reloadConfig();
    }

    public static void log(String... message) {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        TextFileUtils.write(TestPlugin.LOG_FILE_PATH, "[" + formatter.format(timestamp) + "] " + String.join(", ", message));
    }

    public static @NotNull FileDataContainerManager database() {
        return new FileDataContainerManager(TestPlugin.DATABASE_FILE_PATH);
    }

    private static JSONObject jsonObject;

    public static @NotNull JSONObject getConfig() {
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);
        final JSONObject defaultObj = new JSONObject();

        if (!file.exists()) {
            file.create();
            file.write(defaultObj);
        }

        return jsonObject == null ? defaultObj : jsonObject;
    }

    public static <T> T getConfigValueOf(@NotNull String path, @NotNull JSONValueType<T> type, T defaultValue) {
        if (getConfig().has(path)) {
            return getConfig().get(path, type);
        }
        else {
            writeConfig(path, defaultValue);
            return defaultValue;
        }
    }

    public static <T> T getConfigValueOf(@NotNull String path, @NotNull JSONValueConverter<T> type, T defaultValue) {
        if (getConfig().has(path)) {
            return getConfig().get(path, type);
        }
        else {
            writeConfig(path, defaultValue);
            return defaultValue;
        }
    }

    public static void writeConfig(@NotNull String path, @NotNull Object value) {
        final JSONObject obj = getConfig();
        obj.set(path, value);
        final JSONFile file = new JSONFile(TestPlugin.CONFIG_FILE_PATH);

        if (!file.exists()) {
            file.create();
        }

        file.write(obj);
    }

    public static void reloadConfig() {
        TextFileUtils.create(TestPlugin.CONFIG_FILE_PATH, "{}");
        jsonObject = new JSONFile(TestPlugin.CONFIG_FILE_PATH).readAsObject();
    }

    private static void deleteLegacy() {
        TextFileUtils.delete("plugins/TestPlugin-1.0-SNAPSHOT.log");
        TextFileUtils.delete("plugins/TestPlugin-1.0-SNAPSHOT.bin");
    }
}
