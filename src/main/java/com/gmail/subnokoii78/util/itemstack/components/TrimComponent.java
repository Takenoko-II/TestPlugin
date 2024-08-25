package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TrimComponent extends TooltipShowable {
    private TrimComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return hasTrim();
    }

    public @Nullable ArmorTrim getTrim() {
        if (itemMeta instanceof ArmorMeta) {
            return ((ArmorMeta) itemMeta).getTrim();
        }
        else return null;
    }

    public boolean hasTrim() {
        if (itemMeta instanceof ArmorMeta) {
            return ((ArmorMeta) itemMeta).hasTrim();
        }
        else return false;
    }

    public void setTrim(ArmorTrim trim) {
        if (trim == null) {
            throw new IllegalArgumentException();
        }

        if (itemMeta instanceof ArmorMeta colorableArmorMeta) {
            colorableArmorMeta.setTrim(trim);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof ArmorMeta colorableArmorMeta) {
            colorableArmorMeta.setTrim(null);
        }
    }

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        else itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:trim";
    }
}
