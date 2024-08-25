package com.gmail.subnokoii78.util.ui;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeatherArmorButton extends ArmorButton {
    public LeatherArmorButton(@NotNull Material material) {
        super(material);
    }

    public LeatherArmorButton color(@Nullable Color color) {
        if (color == null) itemStackBuilder.dyedColor().disable();
        else itemStackBuilder.dyedColor().setColor(color);
        return this;
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.dyedColor().setShowInTooltip(false);
        return super.build();
    }
}
