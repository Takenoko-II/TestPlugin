package com.gmail.subnokoii78.util.ui;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class PotionButton extends ItemButton {
    private PotionButton(@NotNull Material material) {
        super(material);
    }

    public PotionButton color(@NotNull Color color) {
        itemStackBuilder.potionContents().setColor(color);
        return this;
    }

    @Override
    public @NotNull PotionButton name(@NotNull TextComponent component) {
        return (PotionButton) super.name(component);
    }

    @Override
    public @NotNull PotionButton addLore(@NotNull TextComponent component) {
        return (PotionButton) super.addLore(component);
    }

    @Override
    public @NotNull PotionButton setLore(@NotNull List<TextComponent> components) {
        return (PotionButton) super.setLore(components);
    }

    @Override
    public @NotNull PotionButton amount(int amount) {
        return (PotionButton) super.amount(amount);
    }

    @Override
    public @NotNull PotionButton glint(boolean flag) {
        return (PotionButton) super.glint(flag);
    }

    @Override
    public @NotNull PotionButton customModelData(int data) {
        return (PotionButton) super.customModelData(data);
    }

    @Override
    public @NotNull PotionButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (PotionButton) super.onClick(listener);
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
