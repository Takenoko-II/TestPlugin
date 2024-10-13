package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.commands.brigadier.CommandNodes;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.util.command.PluginDebugger;
import com.gmail.subnokoii78.util.event.*;
import com.gmail.subnokoii78.util.execute.*;
import com.gmail.subnokoii78.util.file.json.JSONObject;
import com.gmail.subnokoii78.util.file.json.JSONValueType;
import com.gmail.subnokoii78.util.file.json.TypedJSONArray;
import com.gmail.subnokoii78.util.other.PaperVelocityManager;
import com.gmail.subnokoii78.util.shape.DustSpawner;
import com.gmail.subnokoii78.util.shape.ParticleSpawner;
import com.gmail.subnokoii78.util.shape.ShapeTemplate;
import com.gmail.subnokoii78.util.shape.StraightLine;
import com.gmail.subnokoii78.util.ui.ContainerUI;
import com.gmail.subnokoii78.util.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.TiltedBoundingBox;
import com.gmail.subnokoii78.util.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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

        DataPackMessageReceiveEvent.DataPackMessageReceiverRegistry.INSTANCE.register("bounding_box", event -> {
            final JSONObject message = event.getMessage();
            final TiltedBoundingBox box = new TiltedBoundingBox(
                message.get("width", JSONValueType.NUMBER).doubleValue(),
                message.get("height", JSONValueType.NUMBER).doubleValue(),
                message.get("depth", JSONValueType.NUMBER).doubleValue()
            );

            final float roll = message.get("roll", JSONValueType.NUMBER).floatValue();

            box.put(event.getSender().getWorld(), Vector3Builder.from(event.getSender().getLocation()));
            box.rotate(new TripleAxisRotationBuilder(event.getSender().getYaw(), event.getSender().getPitch(), roll));

            final Set<Entity> entities = box.getIntersectingEntitiesBySAT();

            if (entities.size() < 2) {
                box.showOutline();
            }
            else {
                box.showOutline(Color.RED);
            }
        });

        DataPackMessageReceiveEvent.DataPackMessageReceiverRegistry.INSTANCE.register("y", event -> {
            final DualAxisRotationBuilder d = DualAxisRotationBuilder.from(event.getSender());
            final TripleAxisRotationBuilder t = TripleAxisRotationBuilder.from(d).roll(45f);

            final var a = d.getDirection3d().getLocalAxisProvider();
            final var b = t.getLocalAxisProviderE();
            System.out.println("x: " + a.getX().getAngleBetween(b.getX()));
            System.out.println("y: " + a.getY().getAngleBetween(b.getY()));
            System.out.println("z: " + a.getZ().getAngleBetween(b.getZ()));
        });

        DataPackMessageReceiveEvent.DataPackMessageReceiverRegistry.INSTANCE.register("a", event -> {
            final DualAxisRotationBuilder d = DualAxisRotationBuilder.from(event.getSender());
            final TripleAxisRotationBuilder t = TripleAxisRotationBuilder.from(d);
            t.roll(event.getMessage().get("r", JSONValueType.NUMBER).floatValue());
            TripleAxisRotationBuilder.LocalAxisProviderE l = t.getLocalAxisProviderE();
            new Execute(new SourceStack(SourceOrigin.of(event.getSender())))
                .run.callback(stack -> {
                    drawVector(stack.getDimension(), stack.getPosition(), Color.RED, l.getZ());
                    drawVector(stack.getDimension(), stack.getPosition(), Color.BLUE, l.getX());
                    drawVector(stack.getDimension(), stack.getPosition(), Color.LIME, l.getY());

                    return Execute.SUCCESS;
                });
        });
    }

    private void drawVector(World dimension, Vector3Builder center, Color color, Vector3Builder vector) {
        new ShapeTemplate()
            .scale(2)
            .world(dimension)
            .center(center)
            .particle(new DustSpawner(new Particle.DustOptions(color, 0.5f)))
            .rotation(TripleAxisRotationBuilder.from(vector.getRotation2d()))
            .newShape(StraightLine.class)
            .draw();
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
