package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.*;
import com.gmail.subnokoii.testplugin.events.*;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEventListener;
import com.google.common.io.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        final PluginCommand ui = getCommand("ui");
        if (ui != null) ui.setExecutor(new UICommand());

        new TickListener().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        getLogger().info("ていししたぜいぇい");
    }

    @EventHandler
    public void onAsyncChatSend(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final String message = ((TextComponent) event.message()).content();

        if (!message.startsWith("$PluginMessageSender;")) return;

        event.setCancelled(true);

        if (message.split(";").length < 2) return;

        final String messageType = message.split(";")[1];

        if (messageType.equals("TransferPlayer")) {
            if (message.split(";").length < 3) return;

            final String serverName = message.split(";")[2];

            player.sendMessage(serverName + " サーバーへの接続を試行中...");
            transferPlayer(player, serverName);
        }
    }
}
