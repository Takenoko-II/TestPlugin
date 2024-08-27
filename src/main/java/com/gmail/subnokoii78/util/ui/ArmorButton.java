package com.gmail.subnokoii78.util.ui;

import com.destroystokyo.paper.MaterialSetTag;
import com.gmail.subnokoii78.util.itemstack.TypedAttributeModifier;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ArmorButton extends ItemButton {
    public ArmorButton(@NotNull Material material) {
        super(material);

        if (!MaterialSetTag.ITEMS_TRIMMABLE_ARMOR.isTagged(material)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public @NotNull ArmorButton name(@NotNull TextComponent component) {
        return (ArmorButton) super.name(component);
    }

    @Override
    public @NotNull ArmorButton addLore(@NotNull TextComponent component) {
        return (ArmorButton) super.addLore(component);
    }

    @Override
    public @NotNull ArmorButton setLore(@NotNull List<TextComponent> components) {
        return (ArmorButton) super.setLore(components);
    }

    @Override
    public @NotNull ArmorButton amount(int amount) {
        return (ArmorButton) super.amount(amount);
    }

    @Override
    public @NotNull ArmorButton glint(boolean flag) {
        return (ArmorButton) super.glint(flag);
    }

    @Override
    public @NotNull ArmorButton customModelData(int data) {
        return (ArmorButton) super.customModelData(data);
    }

    @Override
    public @NotNull ArmorButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (ArmorButton) super.onClick(listener);
    }

    public @NotNull ArmorButton trim(@NotNull TrimMaterial material, @NotNull TrimPattern pattern) {
        itemStackBuilder.trim().setTrim(new ArmorTrim(material, pattern));
        return this;
    }

    @Override
    protected @NotNull ItemStack build() {
        itemStackBuilder.trim().setShowInTooltip(false);
        itemStackBuilder.attributeModifiers().addModifier(new TypedAttributeModifier(Attribute.GENERIC_ARMOR));
        itemStackBuilder.attributeModifiers().setShowInTooltip(false);
        return super.build();
    }
}
