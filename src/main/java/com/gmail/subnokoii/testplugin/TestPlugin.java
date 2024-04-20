package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.*;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEventListener;
import com.google.common.io.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TestPlugin extends JavaPlugin {
    private static TestPlugin plugin;

    @Override
    public void onEnable() {
        getLogger().info("きどうしたぜいぇい");

        final PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new ChestUIClickEventListener(), this);
        new TickListener().runTaskTimer(this, 0L, 1L);

        Objects.requireNonNull(getCommand("foo")).setExecutor(new FooCommand());
        Objects.requireNonNull(getCommand("ui")).setExecutor(new UICommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        plugin = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("ていししたぜいぇい");
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
    }

    public static void log(String message) {
        plugin.getLogger().info(message);
        TextFileUtils.log(message);
    }
}
