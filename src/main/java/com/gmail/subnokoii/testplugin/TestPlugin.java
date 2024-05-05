package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.*;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.event.TestPluginEvent;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.ui.*;
import com.google.common.io.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

public final class TestPlugin extends JavaPlugin {
    private static TestPlugin plugin;

    @Override
    public void onEnable() {
        // 準備
        plugin = this;

        TestPlugin.log("Plugin", "TestPluginが起動しました");

        final PluginManager manager = getServer().getPluginManager();

        // イベントリスナー登録
        manager.registerEvents(EntityEventListener.get(), this);
        manager.registerEvents(PlayerEventListener.get(), this);
        manager.registerEvents(new ChestUIClickEventListener(), this);
        TestPluginEventListener.init();
        EntityEventListener.get().runTaskTimer(this, 0L, 1L);
        new TickEventListener().runTaskTimer(this, 0L, 1L);

        TestPlugin.log("Plugin", "イベントリスナーの登録が完了しました");

        // コマンド登録
        setCommandManager("foo", new Foo());
        setCommandManager("log", new Log());
        setCommandManager("lobby", new Lobby());
        setCommandManager("tools", new Tools());
        setCommandManager("test", new Test());

        TestPlugin.log("Plugin", "testplugin:*コマンドを登録しました");

        // BungeeCordに接続
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        TestPlugin.log("Plugin", "BungeeCordチャンネルにTestPluginを登録しました");

        TestPluginEvent.init();
    }

    @Override
    public void onDisable() {
        TestPlugin.log("Plugin", "TestPluginが停止しました");
    }

    public static TestPlugin get() {
        return plugin;
    }

    public static void transfer(Player player, String serverName) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverName);
        final byte[] data = output.toByteArray();

        player.sendPluginMessage(plugin, "BungeeCord", data);

        TestPlugin.log("Plugin", player.getName() + "からBungeeCordチャンネルにプラグインメッセージが送信されました: [\"Connect\", \"" + serverName + "\"]");
    }

    public static void log(String target, String... messages) {
        final String text = String.join(", ", messages);

        switch (target.toLowerCase()) {
            case "server": {
                plugin.getLogger().info(text);
                break;
            }
            case "plugin": {
                final Path logPath = Path.of("plugins/TestPlugin-1.0-SNAPSHOT.log");

                if (!java.nio.file.Files.exists(logPath.getParent())) {
                    try { java.nio.file.Files.createDirectory(logPath.getParent()); }
                    catch (IOException e) { throw new RuntimeException(e); }
                }

                if (!java.nio.file.Files.exists(logPath)) {
                    try { Files.createFile(logPath); }
                    catch (IOException e) { throw new RuntimeException(); }
                }

                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

                TextFileUtils.write(logPath.toString(), "[" + formatter.format(timestamp) + "] " + text);
                break;
            }
        }
    }

    public static ItemStack getServerSelector() {
        final ItemStack itemStack = new ItemStackBuilder(Material.COMPASS)
        .name("Server Selector", Color.fromRGB(0x00FF55))
        .lore("Right Click to Open", Color.GRAY)
        .enchantment(Enchantment.ARROW_INFINITE, 1)
        .hideFlag(ItemFlag.HIDE_ENCHANTS)
        .get();

        final String json = "{\"locked\": true, \"custom_item_tag\": \"server_selector\"}";

        return NBTEditor.set(itemStack, NBTEditor.NBTCompound.fromJson(json), "plugin");
    }

    public static void openServerSelector(Player player) {
        new ChestUIBuilder("Battle of Apostolos", 1)
        .set(1, builder -> {
            return builder.type(Material.NETHER_STAR)
            .name("Game", Color.AQUA)
            .lore("ゲームサーバーに移動", Color.GRAY)
            .onClick(response -> {
                TestPlugin.transfer(player, "game");
                player.sendMessage("gameサーバーへの接続を試行中...");
                response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                response.close();
            });
        })
        .set(3, builder -> {
            return builder.type(Material.PAPER)
            .name("Lobby", Color.WHITE)
            .lore("ロビーサーバーに移動", Color.GRAY)
            .glint(true)
            .onClick(response -> {
                TestPlugin.transfer(player, "lobby");
                player.sendMessage("lobbyサーバーへの接続を試行中...");
                response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                response.close();
            });
        })
        .set(5, builder -> {
            if (player.isOp()) {
                return builder.type(Material.COMMAND_BLOCK)
                .name("Development", Color.ORANGE)
                .lore("開発サーバーに移動", Color.GRAY)
                .glint(true)
                .onClick(response -> {
                    TestPlugin.transfer(player, "develop");
                    player.sendMessage("developサーバーへの接続を試行中...");
                    response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                    response.close();
                });
            }
            else return builder.type(Material.BARRIER)
            .name("Development", Color.ORANGE)
            .lore("権限がないため利用できません", Color.RED)
            .onClick(response -> {
                response.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
                response.close();
            });
        })
        .set(7, builder -> {
            return builder.type(Material.RED_BED)
            .name("Respawn", Color.RED)
            .lore("ワールドのスポーンポイントに戻る", Color.GRAY)
            .onClick(response -> {
                final Location spawnPoint = player.getWorld().getSpawnLocation();
                player.teleport(spawnPoint);
                player.sendMessage("スポーンポイントに戻ります...");
                response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                response.close();
            });
        })
        .open(player);
    }

    public static boolean runCommand(Entity entity, String command) {
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

    public static TestPluginEvent.EventRegisterer events() {
        return TestPluginEvent.EventRegisterer.get();
    }

    public static <T extends CommandExecutor & TabCompleter> void setCommandManager(String name, T manager) {
        final PluginCommand command = plugin.getCommand(name);

        if (command == null) return;

        command.setExecutor(manager);
        command.setTabCompleter(manager);
    }
}
