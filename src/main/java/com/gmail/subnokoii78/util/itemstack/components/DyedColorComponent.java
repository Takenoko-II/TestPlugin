package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DyedColorComponent extends TooltipShowable {
    private DyedColorComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        return hasColor();
    }

    public @Nullable Color getColor() {
        if (itemMeta instanceof LeatherArmorMeta) {
            return ((LeatherArmorMeta) itemMeta).getColor();
        }
        else return null;
    }

    public boolean hasColor() {
        if (itemMeta instanceof LeatherArmorMeta) {
            return !((LeatherArmorMeta) itemMeta).getColor().equals(DyedColorComponent.getDefaultLeatherColor());
        }
        else return false;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException();
        }

        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(color);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) itemMeta).setColor(null);
        }
    }

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_DYE);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_DYE);
        else itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
    }

    public static Color getDefaultLeatherColor() {
        final ItemMeta leatherHelmetMeta = new ItemStack(Material.LEATHER_HELMET).getItemMeta();

        if (leatherHelmetMeta instanceof LeatherArmorMeta) {
            return ((LeatherArmorMeta) leatherHelmetMeta).getColor();
        }
        else throw new RuntimeException("このエラー出るってことはこの世界がおかしい(？)");
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:dyed_color";
    }
}
