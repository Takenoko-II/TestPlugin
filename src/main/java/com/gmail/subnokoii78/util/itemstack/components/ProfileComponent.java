package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public final class ProfileComponent extends ItemStackComponent {
    private ProfileComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return hasOwner();
    }

    public OfflinePlayer getOwner() {
        if (itemMeta instanceof SkullMeta) {
            return ((SkullMeta) itemMeta).getOwningPlayer();
        }
        else return null;
    }

    public boolean hasOwner() {
        if (itemMeta instanceof SkullMeta) {
            return ((SkullMeta) itemMeta).hasOwner();
        }
        else return false;
    }

    public void setOwner(OfflinePlayer player) {
        if (itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwningPlayer(player);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof SkullMeta) {
            ((SkullMeta) itemMeta).setOwningPlayer(null);
        }
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:profile";
    }
}
