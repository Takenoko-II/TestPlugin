package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.system.field.GameFieldChangeObserver;
import com.gmail.subnokoii78.testplugin.system.field.GameFieldRestorer;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.PluginApi;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButton;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButtonClickSound;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
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
        // TODO: serverselectorを別ディメンションへのテレポートにする
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

//    private static final ContainerInteraction SERVER_SELECTOR = new ContainerInteraction(Component.text("Battle of Apostolos"), 1)
//        .set(1, ItemButton.item(Material.NETHER_STAR)
//            .clickSound(ItemButtonClickSound.BASIC)
//            .name(Component.text("Game").color(NamedTextColor.AQUA))
//            .lore(Component.text("ゲームサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
//            .onClick(event -> {
//                event.close();
//                event.getPlayer().teleport(Location)
//                // transfer(event.getPlayer(), PaperVelocityManager.BoAServer.GAME);
//            })
//        )
//        .set(3, ItemButton.item(Material.PAPER)
//            .clickSound(ItemButtonClickSound.BASIC)
//            .name(Component.text("Lobby").color(NamedTextColor.GOLD))
//            .lore(Component.text("ロビーサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
//            .glint(true)
//            .onClick(event -> {
//                event.close();
//                transfer(event.getPlayer(), PaperVelocityManager.BoAServer.LOBBY);
//            })
//        )
//        .set(5, ItemButton.item(Material.COMMAND_BLOCK)
//            .clickSound(ItemButtonClickSound.BASIC)
//            .name(Component.text("Development").color(NamedTextColor.GOLD))
//            .lore(Component.text("開発サーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
//            .glint(true)
//            .onClick(event -> {
//                event.close();
//                if (event.getPlayer().isOp()) {
//                    transfer(event.getPlayer(), PaperVelocityManager.BoAServer.DEVELOPMENT);
//                }
//                else {
//                    event.getPlayer().sendMessage(Component.text("このサーバーへの接続はオペレーター権限が必要です").color(NamedTextColor.RED));
//                }
//            })
//        )
//        .set(7, ItemButton.item(Material.RED_BED)
//            .clickSound(ItemButtonClickSound.BASIC)
//            .name(Component.text("Spawn").color(NamedTextColor.RED))
//            .lore(Component.text("スポーン地点に戻る").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
//            .onClick(event -> {
//                final Location spawnPoint = event.getPlayer().getRespawnLocation();
//                event.close();
//                event.getPlayer().teleport(
//                    spawnPoint == null
//                        ? event.getPlayer().getWorld().getSpawnLocation().add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector())
//                        : spawnPoint.add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector())
//                );
//            })
//        );
}
