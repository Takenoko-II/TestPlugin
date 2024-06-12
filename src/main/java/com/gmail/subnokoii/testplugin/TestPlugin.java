package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.*;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.datacontainer.FileDataContainerManager;
import com.gmail.subnokoii.testplugin.lib.event.TestPluginEvent;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

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

        TestPlugin.log(LoggingTarget.ALL, "TestPluginが起動しました");

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
        setCommandManager("database", new ManageDatabase());

        // BungeeCordに接続
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        TestPlugin.database().set("foo:bar", 0);

        TestPlugin.log(LoggingTarget.SERVER, TestPlugin.database().toJson());
    }

    @Override
    public void onDisable() {
        TestPlugin.log(LoggingTarget.ALL, "TestPluginが停止しました");
    }

    /**
     * このプラグインのインスタンスを返します。
     * @return TestPlugin
     */
    public static TestPlugin getInstance() {
        return plugin;
    }

    /**
     * ログにメッセージを書き込みます。
     * @param target 書き込み先(TestPlugin.LoggingTarget)
     * @param messages メッセージ(複数可)
     */
    public static void log(LoggingTarget target, String... messages) {
        final String text = String.join(", ", messages);

        switch (target) {
            case SERVER: {
                getInstance().getLogger().info(text);
                break;
            }
            case PLUGIN: {
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

                TextFileUtils.create(LOG_FILE_PATH);
                TextFileUtils.write(LOG_FILE_PATH, "[" + formatter.format(timestamp) + "] " + text);

                break;
            }
            case ALL: {
                log(LoggingTarget.SERVER, messages);
                log(LoggingTarget.PLUGIN, messages);
                break;
            }
            default: {
                throw new IllegalArgumentException("ログターゲットが無効です");
            }
        }
    }

    /**
     * ログにメッセージを書き込みます。
     * @param messages メッセージ(複数可)
     */
    public static void log(LoggingTarget target, TextComponent... messages) {
        final TextComponent separator = Component.text(", ").color(NamedTextColor.WHITE);
        final Optional<TextComponent> text = Arrays.stream(messages).reduce((a, b) -> a.append(separator).append(b));

        if (text.isEmpty()) return;

        switch (target) {
            case SERVER: {
                getInstance().getComponentLogger().info(text.get());
                break;
            }
            case PLUGIN: {
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

                TextFileUtils.create(LOG_FILE_PATH);
                TextFileUtils.write(LOG_FILE_PATH, "[" + formatter.format(timestamp) + "] " + text.get().content());

                break;
            }
            case ALL: {
                log(LoggingTarget.SERVER, messages);
                log(LoggingTarget.PLUGIN, messages);
                break;
            }
            default: {
                throw new IllegalArgumentException("ログターゲットが無効です");
            }
        }
    }

    /**
     * 特定のエンティティを実行者にコマンドをコンソールに実行させます。
     * ゲームルールを一時的に変更するためチャットへのログは流れません。
     * @param entity 実行者
     * @param command コマンドを表現する文字列
     * @return コマンドの実行結果
     */
    public static boolean runCommandAsEntity(Entity entity, String command) {
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

    /**
     * コマンドをコンソールに実行させます。
     * ゲームルールを一時的に変更するためチャットへのログは流れません。
     * @param command コマンドを表現する文字列
     * @return コマンドの実行結果
     */
    public static boolean runCommandAsConsole(String command) {
        try {
            return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        catch (CommandException e) {
            return false;
        }
    }

    /**
     * TestPluginが管理する独自イベントを登録します。
     * @return TestPluginEvent.EventRegisterer
     */
    public static TestPluginEvent.EventRegistrar events() {
        return TestPluginEvent.EventRegistrar.get();
    }

    /**
     * コマンドをプラグインと紐づけます。
     * @param name コマンド名
     * @param manager CommandExecutorとTabCompleterを同時に実装しているクラス
     */
    private <T extends CommandExecutor & TabCompleter> void setCommandManager(String name, T manager) {
        final PluginCommand command = getCommand(name);

        if (command == null) return;

        command.setExecutor(manager);
        command.setTabCompleter(manager);
    }

    public static FileDataContainerManager database() {
        return new FileDataContainerManager(DATABASE_FILE_PATH);
    }

    private static final String LOG_FILE_PATH = "plugins/TestPlugin-1.0-SNAPSHOT.log";

    private static final String DATABASE_FILE_PATH = "plugins/TestPlugin-1.0-SNAPSHOT.bin";

    public enum LoggingTarget {
        SERVER,
        PLUGIN,
        ALL
    }
}
