package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.*;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEventListener;
import com.google.common.io.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new ChestUIClickEventListener(), this);
        new TickListener().runTaskTimer(this, 0L, 1L);

        TestPlugin.log("Plugin", "イベントリスナーの登録が完了しました");

        // コマンド登録
        final Foo foo = new Foo();
        Objects.requireNonNull(getCommand("foo")).setExecutor(foo);
        Objects.requireNonNull(getCommand("foo")).setTabCompleter(foo);

        final Log log = new Log();
        Objects.requireNonNull(getCommand("log")).setExecutor(log);
        Objects.requireNonNull(getCommand("log")).setTabCompleter(log);

        final Lobby lobby = new Lobby();
        Objects.requireNonNull(getCommand("lobby")).setExecutor(lobby);
        Objects.requireNonNull(getCommand("lobby")).setTabCompleter(lobby);

        final Tools tools = new Tools();
        Objects.requireNonNull(getCommand("tools")).setExecutor(tools);
        Objects.requireNonNull(getCommand("tools")).setTabCompleter(tools);

        final Test test = new Test();
        Objects.requireNonNull(getCommand("test")).setExecutor(test);
        Objects.requireNonNull(getCommand("test")).setTabCompleter(test);

        TestPlugin.log("Plugin", "testplugin:*コマンドを登録しました");

        // BungeeCordに接続
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        TestPlugin.log("Plugin", "BungeeCordチャンネルにTestPluginを登録しました");
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

    public static void log(String target, String message) {
        switch (target) {
            case "Server": {
                plugin.getLogger().info(message);
                break;
            }
            case "Plugin": {
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

                TextFileUtils.write(logPath.toString(), "[" + formatter.format(timestamp) + "] " + message);
                break;
            }
        }
    }

    public static ItemStack getServerSelector() {
        final ItemStack itemStack = new ItemStackBuilder(Material.COMPASS)
        .name("Server Selector")
        .lore("Right Click to Open", Color.GRAY)
        .enchantment(Enchantment.ARROW_INFINITE, 1)
        .hideFlag(ItemFlag.HIDE_ENCHANTS)
        .get();

        final String json = "{\"locked\": true, \"on_right_click\": {\"type\": \"open_ui\", \"content\":\"server_selector\" }}";

        return NBTEditor.set(itemStack, NBTEditor.NBTCompound.fromJson(json), "plugin");
    }
}
