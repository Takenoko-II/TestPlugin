package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;

public final class MaxStackSizeComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private MaxStackSizeComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
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
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:max_stack_size";
}
