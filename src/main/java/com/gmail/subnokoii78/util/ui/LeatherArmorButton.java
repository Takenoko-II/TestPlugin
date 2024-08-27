package com.gmail.subnokoii78.util.ui;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class LeatherArmorButton extends ArmorButton {
    public LeatherArmorButton(@NotNull Material material) {
        super(material);
    }

    @Override
    public @NotNull LeatherArmorButton name(@NotNull TextComponent component) {
        return (LeatherArmorButton) super.name(component);
    }

    @Override
    public @NotNull LeatherArmorButton addLore(@NotNull TextComponent component) {
        return (LeatherArmorButton) super.addLore(component);
    }

    @Override
    public @NotNull LeatherArmorButton setLore(@NotNull List<TextComponent> components) {
        return (LeatherArmorButton) super.setLore(components);
    }

    @Override
    public @NotNull LeatherArmorButton amount(int amount) {
        return (LeatherArmorButton) super.amount(amount);
    }

    @Override
    public @NotNull LeatherArmorButton glint(boolean flag) {
        return (LeatherArmorButton) super.glint(flag);
    }

    @Override
    public @NotNull LeatherArmorButton customModelData(int data) {
        return (LeatherArmorButton) super.customModelData(data);
    }

    @Override
    public @NotNull LeatherArmorButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (LeatherArmorButton) super.onClick(listener);
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
