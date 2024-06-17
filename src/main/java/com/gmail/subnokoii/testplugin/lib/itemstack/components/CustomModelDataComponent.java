package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public final class CustomModelDataComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private CustomModelDataComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return itemMeta.hasCustomModelData();
    }

    public @Nullable Integer getData() {
        if (itemMeta.hasCustomModelData()) {
            return itemMeta.getCustomModelData();
        }
        else return null;
    }

    public void setCustomModelData(int data) {
        itemMeta.setCustomModelData(data);
    }

    @Override
    public void disable() {
        itemMeta.setCustomModelData(null);
    }

    @Override
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:custom_model_data";
}
