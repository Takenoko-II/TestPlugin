package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.FooCommand;
import com.gmail.subnokoii.testplugin.events.PlayerListener;
import com.gmail.subnokoii.testplugin.events.TickListener;
import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("きどうしたぜいぇい");

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        final PluginCommand foo = getCommand("foo");
        if (foo != null) foo.setExecutor(new FooCommand());

        new TickListener().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        getLogger().info("ていししたぜいぇい");
    }
}
