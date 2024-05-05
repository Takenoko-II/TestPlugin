package com.gmail.subnokoii.testplugin.lib.event.data;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public class TickEvent {
    private final Server server = Bukkit.getServer();

    private final long tickWhenEventFired = server.getCurrentTick();

    public Server getServer() {
        return server;
    }

    public long getTickWhenEventFired() {
        return tickWhenEventFired;
    }
}
