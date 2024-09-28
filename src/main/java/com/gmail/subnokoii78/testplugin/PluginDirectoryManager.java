package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.util.datacontainer.FileDataContainerManager;
import com.gmail.subnokoii78.util.file.BinaryFileUtils;
import com.gmail.subnokoii78.util.file.FolderUtils;
import com.gmail.subnokoii78.util.file.TextFileUtils;
import com.gmail.subnokoii78.util.file.json.JSONFileHandler;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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

    public static JSONObject getConfig() {
        return jsonObject;
    }

    public static void reloadConfig() {
        TextFileUtils.create(TestPlugin.CONFIG_FILE_PATH, "{}");
        jsonObject = new JSONFileHandler(TestPlugin.CONFIG_FILE_PATH).readObject();
    }

    private static void deleteLegacy() {
        TextFileUtils.delete("plugins/TestPlugin-1.0-SNAPSHOT.log");
        TextFileUtils.delete("plugins/TestPlugin-1.0-SNAPSHOT.bin");
    }
}
