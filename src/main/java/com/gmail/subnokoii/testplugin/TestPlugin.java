package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.FooCommand;
import com.gmail.subnokoii.testplugin.events.PlayerListener;
import com.gmail.subnokoii.testplugin.events.TickListener;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIClickEventListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("きどうしたぜいぇい");

        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new ChestUIClickEventListener(), this);

        final PluginCommand foo = getCommand("foo");
        if (foo != null) foo.setExecutor(new FooCommand());

        new TickListener().runTaskTimer(this, 0L, 1L);
    }

    @Override
    public void onDisable() {
        getLogger().info("ていししたぜいぇい");
    }
}
