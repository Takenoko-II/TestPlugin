package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.FooCommand;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEventListener;
import com.google.common.io.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin implements Listener {
    private void transferPlayer(Player player, String serverName) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(serverName);
        final byte[] data = output.toByteArray();

        player.sendPluginMessage(this, "BungeeCord", data);
    }

    @Override
    public void onEnable() {
        getLogger().info("きどうしたぜいぇい");

        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(this, this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new ChestUIClickEventListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        final PluginCommand foo = getCommand("foo");
        if (foo != null) foo.setExecutor(new FooCommand());

        new TickListener().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        getLogger().info("ていししたぜいぇい");
    }

    @EventHandler
    public void onUnknownCommand(UnknownCommandEvent event) {
        final String commandLine = event.getCommandLine();

        if (!(event.getSender() instanceof Player)) return;

        final Player player = (Player) event.getSender();

        if (!commandLine.startsWith("1d0a78b9-aa2e-4957-be57-5b745ea970b1;")) return;

        final String serverName = commandLine.split(";")[1];

        transferPlayer(player, serverName);
    }
}
