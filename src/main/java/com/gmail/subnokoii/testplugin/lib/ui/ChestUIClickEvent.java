package com.gmail.subnokoii.testplugin.lib.ui;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ChestUIClickEvent {
    private final Player player;

    private final ChestUIButtonBuilder button;

    public ChestUIClickEvent(Player player, ChestUIButtonBuilder button) {
        this.player = player;
        this.button = button;
    }

    public Player getPlayer() {
        return player;
    }

    public ChestUIButtonBuilder getClicked() {
        return button;
    }

    public void close() {
        player.closeInventory();
    }

    public void playSound(Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void runCommand(String command) {
        player.getServer().dispatchCommand(player, command);
    }
}
