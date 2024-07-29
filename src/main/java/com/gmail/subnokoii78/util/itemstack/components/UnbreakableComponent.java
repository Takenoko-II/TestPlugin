package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class UnbreakableComponent implements TooltipShowable {
    private final ItemMeta itemMeta;

    private UnbreakableComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean isEnabled() {
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

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        else itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:unbreakable";
    }
}
