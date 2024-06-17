package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

public final class ItemNameComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private ItemNameComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return itemMeta.hasItemName();
    }

    @Override
    public void disable() {
        itemMeta.itemName(null);
    }

    public @Nullable TextComponent getItemName() {
        if (getEnabled()) {
            return (TextComponent) itemMeta.itemName();
        }
        else return null;
    }

    public void setItemName(TextComponent component) {
        itemMeta.itemName(component);
    }

    @Override
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:item_name";
}
