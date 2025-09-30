package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.ConfigCommand;
import com.gmail.subnokoii78.testplugin.commands.CustomItemsCommand;
import com.gmail.subnokoii78.testplugin.commands.ServerSelectorCommand;
import com.gmail.subnokoii78.testplugin.commands.brigadier.BrigadierCommandNodes;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.PluginApi;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.takenokoii78.json.JSONSerializer;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestPlugin extends JavaPlugin {
    private final TestPluginBootstrap bootstrap;

    TestPlugin(@NotNull TestPluginBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void onLoad() {
        getComponentLogger().info(Component.text("TestPluginを読み込んでいます...").color(NamedTextColor.GRAY));
    }

    @Override
    public void onEnable() {
        // TestPluginPersistentディレクトリを用意
        try {
            Files.createDirectories(Path.of(TestPlugin.PERSISTENT_DIRECTORY_PATH));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        // ライブラリを準備
        TPLCore.initialize(
            this, bootstrap,
            TestPlugin.CONFIG_FILE_PATH,
            TestPlugin.DEFAULT_CONFIG_PATH
        );

        // System.out.println(JSONSerializer.serialize(TPLCore.getPluginConfigLoader().get()));

        // データパック導入チェック
        getComponentLogger().info(
            TPLCore.pluginApi.getDatapack().isEnabled()
                ? Component.text("データパック " + PluginApi.ID + " をロードしました").color(NamedTextColor.GREEN)
                : Component.text("データパック " + PluginApi.ID + " のロードに失敗しました").color(NamedTextColor.RED)
        );

        // イベントリスナー登録
        PlayerEventListener.init();
        EntityEventListener.init();

        // コマンド登録
        // TODO: 廃止済みらしいのでbrigadier式に置き換え
        // setCommandManager("lobby", new Lobby());
        // setCommandManager("tools", new Tools());

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            for (final BrigadierCommandNodes node : BrigadierCommandNodes.values()) {
                registrar.register(node.getNode());
                registrar.register(ServerSelectorCommand.SERVER_SELECTOR_COMMAND.getCommandNode());
                CustomItemsCommand.CUSTOM_ITEMS.register(registrar);
                ConfigCommand.CONFIG_COMMAND.register(registrar);
            }
        });

        TPLCore.events.register(TPLEventTypes.PLAYER_CLICK, CustomEventListener.INSTANCE::onLeftClick);
        TPLCore.events.register(TPLEventTypes.DATAPACK_MESSAGE_RECEIVE, CustomEventListener.INSTANCE::onDatapackMessageReceive);
        TPLCore.events.register(TPLEventTypes.TICK, TickEventListener.INSTANCE::onTick);

        getComponentLogger().info(Component.text("TestPluginが起動しました").color(NamedTextColor.GREEN));

        // TODO: config.jsonへの書き込み手段の提供(set, add, remove 可能な限りすべて)
        // TODO: serverselectorからのtp時の音問題の修正ができてるかチェック
    }

    @Override
    public void onDisable() {
        if (TPLCore.pluginApi.getDatapack().isEnabled()) {
            TPLCore.pluginApi.getDatapack().setEnabled(false);
            getComponentLogger().info(Component.text("データパック '" + PluginApi.ID + "' が無効化されました"));
        }

        new Execute()
            .as(EntitySelector.E.arg(SelectorArgument.TAG, INTERNAL_ENTITY_TAG))
            .run.callback(stack -> {
                stack.getExecutor().remove();
                return Execute.SUCCESS;
            });

        getComponentLogger().info(Component.text("TestPluginが停止しました").color(NamedTextColor.BLUE));
    }

    public static final String INTERNAL_ENTITY_TAG = "TestPlugin.Internal";

    @Deprecated
    private <T extends CommandExecutor & TabCompleter> void setCommandManager(String name, T manager) {
        final PluginCommand command = getCommand(name);

        if (command == null) return;

        command.setExecutor(manager);
        command.setTabCompleter(manager);
    }

    public static final String PERSISTENT_DIRECTORY_PATH = "plugins/TestPluginPersistent";

    public static final String CONFIG_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/config.json";

    public static final String DEFAULT_CONFIG_PATH = "/default_config.json";
}
