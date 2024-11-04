package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.commands.brigadier.BrigadierCommandNodes;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.util.command.PluginDebugger;
import com.gmail.subnokoii78.util.event.*;
import com.gmail.subnokoii78.util.execute.*;
import com.gmail.subnokoii78.util.other.PaperVelocityManager;
import com.gmail.subnokoii78.util.schedule.GameTickScheduler;
import com.gmail.subnokoii78.util.ui.ContainerUI;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class TestPlugin extends JavaPlugin {
    private static TestPlugin plugin;

    private static PaperVelocityManager paperVelocityManager;

    @Override
    public void onLoad() {
        getLogger().info("TestPluginを読み込んでいます...");
        plugin = this;
    }

    @Override
    public void onEnable() {
        // 準備
        paperVelocityManager = PaperVelocityManager.register(this);
        PluginDirectoryManager.init();
        GameTickScheduler.init(this);

        getLogger().info("TestPluginが起動しました");

        // イベントリスナー登録
        PlayerEventListener.init();
        EntityEventListener.init();
        TickEventListener.init();
        ContainerUI.UIEventHandler.init(this);

        // コマンド登録
        setCommandManager("lobby", new Lobby());
        setCommandManager("tools", new Tools());
        setCommandManager("test", new Test());

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            PluginDebugger.INSTANCE.init("tspldebugger", registrar);
            for (final BrigadierCommandNodes node : BrigadierCommandNodes.values()) {
                registrar.register(node.getNode());
            }
        });

        CustomEventHandlerRegistry.init(this);
        CustomEventHandlerRegistry.register(CustomEventType.PLAYER_LEFT_CLICK, CustomEventListener.INSTANCE::onLeftClick);

        CustomEventListener.INSTANCE.registerDataPackMessageIds();

        PluginDebugger.INSTANCE.register("execute", ctx -> {
            final Execute execute = new Execute()
                .at(EntitySelector.P)
                .as(EntitySelector.E.arg(SelectorArgument.TAG, "Test").arg(SelectorArgument.SORT, SelectorSortOrder.NEAREST))
                .ifOrUnless(IfUnless.IF).items.entity(
                    EntitySelector.S,
                    ItemSlotsGroup.ARMOR.getSlots(ItemSlotsGroup.ArmorSlots.HEAD),
                    itemStack -> {
                        System.out.println(itemStack);
                        var a = itemStack.getType().equals(Material.COMMAND_BLOCK);
                        System.out.println(a);
                        return a;
                    }
                )
                .at(EntitySelector.S);

            execute.at(EntitySelector.E.notArg(SelectorArgument.TYPE, EntityType.ARMOR_STAND).notArg(SelectorArgument.TYPE, EntityType.PLAYER).arg(SelectorArgument.SORT, SelectorSortOrder.FURTHEST))
                .ifOrUnless(IfUnless.IF).score(
                    ScoreHolder.of(EntitySelector.S), "_",
                    ScoreComparator.EQUALS,
                    ScoreHolder.of(EntitySelector.N), "_"
                );

            execute.run.command("say " + Bukkit.getCurrentTick() + " command");
            execute.run.callback(stack -> {
                System.out.println(Bukkit.getCurrentTick() + " callback");
                return Execute.SUCCESS;
            });

            execute.anchored(EntityAnchorType.EYES)
                .positioned.$("^ ^ ^1")
                .run.callback(stack -> {
                    stack.getExecutor().teleport(stack.getLocation());
                    return Execute.SUCCESS;
                });
            return Execute.SUCCESS;
        });
    }

    @Override
    public void onDisable() {
        getLogger().info("TestPluginが停止しました");
    }

    public static TestPlugin getInstance() {
        return plugin;
    }

    public static PaperVelocityManager getPaperVelocityManager() {
        return paperVelocityManager;
    }

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
}
