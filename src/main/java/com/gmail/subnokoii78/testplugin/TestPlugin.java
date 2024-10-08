package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.commands.brigadier.CommandNodes;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.util.command.PluginDebugger;
import com.gmail.subnokoii78.util.event.CustomEventHandlerRegistry;
import com.gmail.subnokoii78.util.event.CustomEventType;
import com.gmail.subnokoii78.util.event.CustomEvents;
import com.gmail.subnokoii78.util.other.PaperVelocityManager;
import com.gmail.subnokoii78.util.ui.ContainerUI;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TestPlugin extends JavaPlugin {
    private static TestPlugin plugin;

    private static PaperVelocityManager paperVelocityManager;

    @Override
    public void onLoad() {
        getLogger().info("TestPluginを読み込んでいます...");
    }

    @Override
    public void onEnable() {
        // 準備
        plugin = this;
        paperVelocityManager = PaperVelocityManager.register(plugin);

        PluginDirectoryManager.init();

        TestPlugin.log(LoggingTarget.ALL, "TestPluginが起動しました");

        // イベントリスナー登録
        CustomEvents.init();
        PlayerEventListener.init();
        EntityEventListener.init();
        ContainerUI.UIEventHandler.init(this);
        TickEventListener.init();

        // コマンド登録
        setCommandManager("lobby", new Lobby());
        setCommandManager("tools", new Tools());
        setCommandManager("test", new Test());

        // BungeeCordに接続
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            PluginDebugger.INSTANCE.init("tspldebugger", registrar);
            for (final CommandNodes node : CommandNodes.values()) {
                registrar.register(node.getNode());
            }
        });

        CustomEventHandlerRegistry.init(this);

        CustomEventHandlerRegistry.register(CustomEventType.PLAYER_LEFT_CLICK, CustomEventListener.INSTANCE::onLeftClick);
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

    public static PaperVelocityManager getPaperVelocityManager() {
        return paperVelocityManager;
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
                PluginDirectoryManager.log(messages);
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
                PluginDirectoryManager.log(text.get().content());
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
     * TestPluginが管理する独自イベントを登録します。
     * @return TestPluginEvent.EventRegisterer
     */
    public static CustomEvents.Registrar events() {
        return new CustomEvents.Registrar();
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

    public static final String PERSISTENT_DIRECTORY_PATH = "plugins/TestPluginPersistent";

    public static final String LOG_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/plugin.log";

    public static final String DATABASE_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/database.dat";

    public static final String CONFIG_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/config.json";

    public enum LoggingTarget {
        SERVER,
        PLUGIN,
        ALL
    }
}
