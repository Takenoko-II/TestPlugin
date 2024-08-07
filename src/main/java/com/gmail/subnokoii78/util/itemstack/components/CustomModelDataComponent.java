package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
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
    public boolean isEnabled() {
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
    public @NotNull String getComponentId() {
        return "minecraft:custom_model_data";
    }
}
