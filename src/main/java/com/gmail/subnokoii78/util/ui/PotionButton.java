package com.gmail.subnokoii78.util.ui;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PotionButton extends ItemButton {
    private PotionButton(@NotNull Material material) {
        super(material);
    }

    public PotionButton color(@NotNull Color color) {
        itemStackBuilder.potionContents().setColor(color);
        return this;
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.potionContents().setShowInTooltip(false);
        return super.build();
    }

    public static PotionButton potion() {
        return new PotionButton(Material.POTION);
    }

    public static PotionButton splashPotion() {
        return new PotionButton(Material.SPLASH_POTION);
    }

    public static PotionButton lingeringPotion() {
        return new PotionButton(Material.LINGERING_POTION);
    }

    public static PotionButton tippedArrow() {
        return new PotionButton(Material.TIPPED_ARROW);
    }
}
