package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public final class UnbreakableComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private UnbreakableComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return getUnbreakable();
    }

    @Override
    public void disable() {
        itemMeta.setUnbreakable(false);
    }

    public boolean getUnbreakable() {
        return itemMeta.isUnbreakable();
    }

    public void setUnbreakable(boolean flag) {
        itemMeta.setUnbreakable(flag);
    }

    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
    }

    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        else itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    }

    public static final String COMPONENT_ID = "minecraft:unbreakable";
}
