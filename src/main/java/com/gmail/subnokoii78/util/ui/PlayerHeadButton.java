package com.gmail.subnokoii78.util.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class PlayerHeadButton extends ItemButton {
    public PlayerHeadButton() {
        super(Material.PLAYER_HEAD);
    }

    public PlayerHeadButton player(@NotNull String name) {
        itemStackBuilder.profile().setOwner(Bukkit.getOfflinePlayer(name));
        return this;
    }
}
