package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.*;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.event.TestPluginEvent;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import com.gmail.subnokoii.testplugin.lib.ui.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

public final class TestPlugin extends JavaPlugin {
    private static TestPlugin plugin;

    @Override
    public void onLoad() {
        getLogger().info("TestPluginを読み込んでいます...");
    }

    @Override
    public void onEnable() {
        // 準備
        plugin = this;

        TestPlugin.log("Server", "TestPluginが起動しました");
        TestPlugin.log("Plugin", "TestPluginが起動しました");

        // イベントリスナー登録
        TestPluginEvent.init();

        PlayerEventListener.init();
        EntityEventListener.init();
        ChestUIClickEvent.Listener.init();
        TickEventListener.init();

        // コマンド登録
        setCommandManager("foo", new Foo());
        setCommandManager("log", new Log());
        setCommandManager("lobby", new Lobby());
        setCommandManager("tools", new Tools());
        setCommandManager("test", new Test());

        // BungeeCordに接続
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        TestPlugin.log("Server", "TestPluginが停止しました");
        TestPlugin.log("Plugin", "TestPluginが停止しました");
    }

    public static TestPlugin get() {
        return plugin;
    }

    public static void log(String target, String... messages) {
        final String text = String.join(", ", messages);

        switch (target.toLowerCase()) {
            case "server": {
                plugin.getLogger().info(text);
                break;
            }
            case "plugin": {
                final Path logPath = Path.of("plugins/TestPlugin-1.0-SNAPSHOT.log");

                if (!java.nio.file.Files.exists(logPath.getParent())) {
                    try { java.nio.file.Files.createDirectory(logPath.getParent()); }
                    catch (IOException e) { throw new RuntimeException(e); }
                }

                if (!java.nio.file.Files.exists(logPath)) {
                    try { Files.createFile(logPath); }
                    catch (IOException e) { throw new RuntimeException(); }
                }

                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

                TextFileUtils.write(logPath.toString(), "[" + formatter.format(timestamp) + "] " + text);
                break;
            }
        }
    }

    public static boolean runCommand(Entity entity, String command) {
        try {
            final Boolean sendCommandFeedback = Objects.requireNonNullElse(entity.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK), true);

            entity.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
            final boolean result = entity.getServer().dispatchCommand(Bukkit.getConsoleSender(), String.format("execute as %s at @s run %s", entity.getUniqueId(), command));
            entity.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, sendCommandFeedback);

            return result;
        }
        catch (CommandException e) {
            return false;
        }
    }

    public static TestPluginEvent.EventRegisterer events() {
        return TestPluginEvent.EventRegisterer.get();
    }

    private <T extends CommandExecutor & TabCompleter> void setCommandManager(String name, T manager) {
        final PluginCommand command = getCommand(name);

        if (command == null) return;

        command.setExecutor(manager);
        command.setTabCompleter(manager);
    }
}
