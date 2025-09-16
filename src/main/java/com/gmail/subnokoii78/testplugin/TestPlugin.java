package com.gmail.subnokoii78.testplugin;

import com.gmail.subnokoii78.testplugin.commands.*;
import com.gmail.subnokoii78.testplugin.commands.brigadier.BrigadierCommandNodes;
import com.gmail.subnokoii78.testplugin.events.*;
import com.gmail.subnokoii78.testplugin.events.TickEventListener;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.commands.ConsoleCommand;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.Execute;
import com.gmail.subnokoii78.tplcore.execute.SelectorArgument;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackCustomDataAccess;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        // ライブラリを準備
        TPLCore.initialize(this, bootstrap);

        if (bootstrap.getDatapack().isEnabled()) {
            getComponentLogger().info(Component.text("データパック tpl をロードしました").color(NamedTextColor.GREEN));
        }
        else {
            getComponentLogger().info(Component.text("データパック tpl のロードに失敗しました").color(NamedTextColor.RED));
        }

        // TestPluginPersistentディレクトリを用意
        try {
            Files.createDirectories(Path.of(TestPlugin.PERSISTENT_DIRECTORY_PATH));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        PluginConfigurationManager.reload();

        getLogger().info("TestPluginが起動しました");

        // イベントリスナー登録
        PlayerEventListener.init();
        EntityEventListener.init();

        // コマンド登録
        // TODO: 廃止済みらしいのでbrigadier式に置き換え
        // setCommandManager("lobby", new Lobby());
        // setCommandManager("tools", new Tools());
        // setCommandManager("test", new Test());

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final var registrar = event.registrar();
            for (final BrigadierCommandNodes node : BrigadierCommandNodes.values()) {
                registrar.register(node.getNode());
                registrar.register(Commands.literal("debug").executes(ctx -> {
                    final ItemStack itemStack = ((Player) ctx.getSource().getExecutor()).getEquipment().getItemInMainHand();
                    System.out.println("debug: " + ItemStackCustomDataAccess.of(itemStack).read());
                    return Command.SINGLE_SUCCESS;
                }).build());
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

    @Deprecated
    private <T extends CommandExecutor & TabCompleter> void setCommandManager(String name, T manager) {
        final PluginCommand command = getCommand(name);

        if (command == null) return;

        command.setExecutor(manager);
        command.setTabCompleter(manager);
    }

    public static final String PERSISTENT_DIRECTORY_PATH = "plugins/TestPluginPersistent";

    @Deprecated
    public static final String LOG_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/plugin.log";

    @Deprecated
    public static final String DATABASE_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/database.dat";

    public static final String CONFIG_FILE_PATH = PERSISTENT_DIRECTORY_PATH + "/config.json";
}

/* TODO
* モブを殴ったりする(気のせいかも)と「plugin_api.on_right_clickは存在しないよ」と出る -> TPLCoreのhasObjective()をよく見て、反転忘れてるよ？
* サーバーセレクターが右クリックで開けない(=TPLCore.eventsを怪しむ) -> またあした
* 左右どちらもクリックイベントが発火しない？ -> 「キー "custom_item_tag" は存在しないよ」-> DataContainerのころと仕様が違うから。has()してからget()するようにTestPlugin側を書き換える
*/
