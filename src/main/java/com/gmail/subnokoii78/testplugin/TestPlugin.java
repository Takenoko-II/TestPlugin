package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.util.datacontainer.FileDataContainerManager;
import com.gmail.subnokoii78.util.event.CustomEvents;
import com.gmail.subnokoii78.util.file.TextFileUtils;
import com.gmail.subnokoii78.util.ui.ChestUIClickEvent;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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
        CustomEvents.init();
        PlayerEventListener.init();
        EntityEventListener.init();
        ChestUIClickEvent.Listener.init();
        TickEventListener.init();

        // コマンド登録
        // setCommandManager("foo", new Foo());
        setCommandManager("log", new Log());
        setCommandManager("lobby", new Lobby());
        setCommandManager("tools", new Tools());
        setCommandManager("test", new Test());
        setCommandManager("database", new ManageDatabase());

        // BungeeCordに接続
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(getFooCommandNode());
            event.registrar().register(getLoggerCommandNode());
        });
    }

    @Override
    public void onDisable() {
        TestPlugin.log(LoggingTarget.ALL, "TestPluginが停止しました");
    }

    private Command<CommandSourceStack> getLogReader(int start, int end, boolean fromLast) {
        return ctx -> {
            final var log = TextFileUtils.read("logs/latest.log");
            final var sender = ctx.getSource().getSender();
            var component = fromLast
                ? Component.text("ログの終わり" + (end - start + 1) + "行を表示しています")
                : Component.text("ログの初め" + (end - start + 1) + "行を表示しています");

            final var sub = fromLast
                ? log.subList(log.size() - (end + 1), log.size() - (start + 1))
                : log.subList(start, end);

            for (int i = 0; i < sub.size(); i++) {
                final var line = sub.get(i);
                if (i % 2 == 0) {
                    component = component.append(Component.text("\n- ").color(NamedTextColor.BLUE)).append(Component.text(line).color(NamedTextColor.WHITE));
                }
                else {
                    component = component.append(Component.text("\n- ").color(NamedTextColor.BLUE)).append(Component.text(line).color(NamedTextColor.GRAY));
                }
            }
            sender.sendMessage(component);
            return Command.SINGLE_SUCCESS;
        };
    }

    private LiteralCommandNode<CommandSourceStack> getFooCommandNode() {
        return Commands.literal("foo")
            .executes(ctx -> {
                ctx.getSource().getSender().sendMessage(Component.text("foo!"));
                return Command.SINGLE_SUCCESS;
            })
            .build();
    }

    private LiteralCommandNode<CommandSourceStack> getLoggerCommandNode() {
        return Commands.literal("serverlogger")
            .then(
                Commands.literal("read")
                    .then(Commands.literal("first").executes(getLogReader(0, 31, false)))
                    .then(Commands.literal("last").executes(getLogReader(0, 31, true)))
                    .executes(getLogReader(0, 15, true))
            )
            .then(
                Commands.literal("write")
                    .then(Commands.argument("message", ArgumentTypes.component())
                        .executes(ctx -> {
                            final var message = ctx.getArgument("message", Component.class);
                            getComponentLogger().info(message);
                            ctx.getSource().getSender().sendMessage(Component.text("メッセージをログに書き込みました: ").append(message));
                            return Command.SINGLE_SUCCESS;
                        })
                    )
            )
            .then(Commands.literal("clear").executes(ctx -> {
                Arrays.stream(TextFileUtils.getAll("logs"))
                    .filter(path -> path.endsWith(".log.gz"))
                    .forEach(TextFileUtils::delete);
                ctx.getSource().getSender().sendPlainMessage("ログのアーカイブを全て削除しました");
                return Command.SINGLE_SUCCESS;
            }))
            .build();
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
