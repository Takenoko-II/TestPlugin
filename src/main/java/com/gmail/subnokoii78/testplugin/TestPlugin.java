package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.system.field.GameFieldChangeObserver;
import com.gmail.subnokoii78.testplugin.system.field.GameFieldRestorer;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.PluginApi;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class TestPlugin extends JavaPlugin {
    @Nullable
    private static GameFieldRestorer gameFieldRestorer;

    private final TestPluginBootstrap bootstrap;

    TestPlugin(TestPluginBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void onLoad() {
        getComponentLogger().info(Component.text("TestPluginを読み込んでいます...").color(NamedTextColor.GRAY));
    }

    @Override
    public void onEnable() {
        // TestPluginPersistentディレクトリを用意
        if (getDataFolder().exists()) {
            getComponentLogger().info(Component.text("データフォルダが既に存在するため、作成をスキップしました"));
        }
        else {
            final boolean created = getDataFolder().mkdir();

            if (!created) {
                throw new IllegalStateException("データフォルダの作成に失敗しました; 致命的な例外のためプラグインは停止されます");
            }
        }

        // ライブラリを準備
        TPLCore.initialize(
            this, bootstrap,
            getConfigFilePath(),
            TestPlugin.DEFAULT_CONFIG_RESOURCE_PATH
        );

        gameFieldRestorer = new GameFieldRestorer(DimensionAccess.of(GAME_FIELD_DIMENSION_ID).getWorld());
        getGameFieldRestorer().open();

        // データパック導入チェック
        getComponentLogger().info(
            TPLCore.pluginApi.getDatapack().isEnabled()
                ? Component.text("データパック " + PluginApi.ID + " をロードしました").color(NamedTextColor.GREEN)
                : Component.text("データパック " + PluginApi.ID + " のロードに失敗しました").color(NamedTextColor.RED)
        );

        // イベントリスナー登録
        PlayerEventListener.init();
        EntityEventListener.init();
        getServer().getPluginManager().registerEvents(GameFieldChangeObserver.INSTANCE, this);

        // コマンド登録
        // TODO: 廃止済みらしいのでbrigadier式に置き換え
        // setCommandManager("lobby", new Lobby());
        // setCommandManager("tools", new Tools());

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            ServerSelectorCommand.SERVER_SELECTOR_COMMAND.register(registrar);
            CustomItemsCommand.CUSTOM_ITEMS.register(registrar);
            ConfigCommand.CONFIG_COMMAND.register(registrar);
            LobbyCommand.LOBBY_COMMAND.register(registrar);
            DatabaseCommand.DATABASE_COMMAND.register(registrar);
            GameFieldCommand.GAME_FIELD_COMMAND.register(registrar);
        });

        TPLCore.events.register(TPLEventTypes.PLAYER_CLICK, CustomEventListener.INSTANCE::onLeftClick);
        TPLCore.events.register(TPLEventTypes.DATAPACK_MESSAGE_RECEIVE, CustomEventListener.INSTANCE::onDatapackMessageReceive);
        TPLCore.events.register(TPLEventTypes.TICK, TickEventListener.INSTANCE::onTick);

        getComponentLogger().info(Component.text("TestPluginが起動しました").color(NamedTextColor.GREEN));

        // TODO: config.jsonへの書き込み手段の提供(set, add, remove 可能な限りすべて)
        // TODO: serverselectorからのtp時の音問題の修正ができてるかチェック
        // TODO: 何故か書き込まれたり書き込まれなかったりする
        // TODO: 何故かロードできてない
        // TODO: org.bukkit.plugin.IllegalPluginAccessException: Plugin attempted to register task while disabledが終了時に出る
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

        getGameFieldRestorer().close();

        getComponentLogger().info(Component.text("TestPluginが停止しました").color(NamedTextColor.BLUE));
    }

    public static GameFieldRestorer getGameFieldRestorer() {
        if (gameFieldRestorer == null) {
            throw new IllegalStateException("gamefieldrestorer is null");
        }
        return gameFieldRestorer;
    }

    public static final String INTERNAL_ENTITY_TAG = "TestPlugin.Internal";

    public String getConfigFilePath() {
        return getDataPath() + "/config.json";
    }

    public String getDatabaseFilePath() {
        return getDataPath() + "/database.db";
    }

    public static final String DEFAULT_CONFIG_RESOURCE_PATH = "/default_config.json";

    public static final String GAME_FIELD_DIMENSION_ID = "plugin_api:game_field";
}
