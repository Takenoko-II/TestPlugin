package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ProfileComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private ProfileComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
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
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:profile";
}
