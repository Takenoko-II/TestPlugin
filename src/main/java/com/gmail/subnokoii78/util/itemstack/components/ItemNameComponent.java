package com.gmail.subnokoii78.util.itemstack.components;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemNameComponent extends ItemStackComponent {
    private ItemNameComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return itemMeta.hasItemName();
    }

    @Override
    public void disable() {
        itemMeta.itemName(null);
    }

    public @Nullable TextComponent getItemName() {
        if (isEnabled()) {
            return (TextComponent) itemMeta.itemName();
        }
        else return null;
    }

    public void setItemName(TextComponent component) {
        itemMeta.itemName(component);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:item_name";
    }
}
