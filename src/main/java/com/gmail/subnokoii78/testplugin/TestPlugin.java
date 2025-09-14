package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.commands.brigadier.BrigadierCommandNodes;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.DatapackMessageReceiveEvent;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.execute.SelectorArgument;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

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
        // ライブラリを準備
        TPLCore.initialize(plugin);

        PluginDirectoryManager.init();

        getLogger().info("TestPluginが起動しました");

        // イベントリスナー登録
        PlayerEventListener.init();
        EntityEventListener.init();

        // コマンド登録
        setCommandManager("lobby", new Lobby());
        setCommandManager("tools", new Tools());
        setCommandManager("test", new Test());

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            PluginDebugger.DEFAULT_DEBUGGER.init("tspldebugger", registrar);
            for (final BrigadierCommandNodes node : BrigadierCommandNodes.values()) {
                registrar.register(node.getNode());
            }
        });

        TPLCore.events.register(TPLEventTypes.PLAYER_CLICK, CustomEventListener.INSTANCE::onLeftClick);
        TPLCore.events.register(TPLEventTypes.DATAPACK_MESSAGE_RECEIVE, CustomEventListener.INSTANCE::onDatapackMessageReceive);
        TPLCore.events.register(TPLEventTypes.TICK, TickEventListener.INSTANCE::onTick);
    }

    @Override
    public void onDisable() {
        getLogger().info("TestPluginが停止しました");

        new Execute()
            .as(EntitySelector.E.arg(SelectorArgument.TAG, INTERNAL_ENTITY_TAG))
            .run.callback(stack -> {
                stack.getExecutor().remove();
                return Execute.SUCCESS;
            });
    }

    public static final String INTERNAL_ENTITY_TAG = "TestPlugin.Internal";

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
