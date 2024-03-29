package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.commands.FooCommand;
import com.gmail.subnokoii.testplugin.events.PlayerListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("きどうしたぜいぇい");

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        final PluginCommand foo = getCommand("foo");
        if (foo != null) {
            foo.setExecutor(new FooCommand());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("ていししたぜいぇい");
    }
}
