package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class MaxStackSizeComponent extends ItemStackComponent {
    private MaxStackSizeComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasMaxStackSize();
    }

    @Override
    public void disable() {
        itemMeta.setMaxStackSize(null);
    }

    public int getMaxStackSize() {
        return itemMeta.getMaxStackSize();
    }

    public void setMaxStackSize(int stackSize) {
        itemMeta.setMaxStackSize(stackSize);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:max_stack_size";
    }
}
