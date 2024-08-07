package com.gmail.subnokoii78.util.itemstack.components;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomNameComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private CustomNameComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasDisplayName();
    }

    @Override
    public void disable() {
        itemMeta.displayName(null);
    }

    public @Nullable TextComponent getCustomName() {
        if (isEnabled()) {
            return (TextComponent) itemMeta.displayName();
        }
        else return null;
    }

    public void setCustomName(TextComponent component) {
        itemMeta.displayName(component);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:custom_name";
    }
}
