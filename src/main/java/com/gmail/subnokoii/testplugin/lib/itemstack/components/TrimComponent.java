package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

public final class TrimComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private TrimComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return hasTrim();
    }

    public @Nullable ArmorTrim getTrim() {
        if (itemMeta instanceof ColorableArmorMeta) {
            return ((ColorableArmorMeta) itemMeta).getTrim();
        }
        else return null;
    }

    public boolean hasTrim() {
        if (itemMeta instanceof ColorableArmorMeta) {
            return ((ColorableArmorMeta) itemMeta).hasTrim();
        }
        else return false;
    }

    public void setTrim(ArmorTrim trim) {
        if (trim == null) {
            throw new IllegalArgumentException();
        }

        if(itemMeta instanceof ColorableArmorMeta) {
            ((ColorableArmorMeta) itemMeta).setTrim(trim);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof ColorableArmorMeta) {
            ((ColorableArmorMeta) itemMeta).setTrim(null);
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

    public static final String COMPONENT_ID = "minecraft:trim";
}
