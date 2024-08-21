package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public final class StoredEnchantmentsComponent extends TooltipShowable {
    private StoredEnchantmentsComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof EnchantmentStorageMeta) {
            return ((EnchantmentStorageMeta) itemMeta).hasStoredEnchants();
        }
        else return false;
    }

    public Map<Enchantment, Integer> getStoredEnchantments() {
        if (itemMeta instanceof EnchantmentStorageMeta) {
            return ((EnchantmentStorageMeta) itemMeta).getStoredEnchants();
        }
        else return null;
    }

    public void addStoredEnchantment(Enchantment enchantment, int level) {
        if (itemMeta instanceof EnchantmentStorageMeta) {
            ((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchantment, level, true);
        }
    }

    public void removeStoredEnchantment(Enchantment enchantment) {
        if (itemMeta instanceof EnchantmentStorageMeta) {
            ((EnchantmentStorageMeta) itemMeta).removeStoredEnchant(enchantment);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            Arrays.stream(Enchantment.class.getFields())
                .map(field -> {
                    if (field.canAccess(null)) {
                        try { return (Enchantment) field.get(null); }
                        catch (IllegalAccessException e) { throw new RuntimeException("Never Happens"); }
                    }
                    else return null;
                })
                .filter(Objects::nonNull)
                .forEach(enchantmentStorageMeta::removeStoredEnchant);
        }
    }

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_STORED_ENCHANTS);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
        else itemMeta.addItemFlags(ItemFlag.HIDE_STORED_ENCHANTS);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:stored_enchantments";
    }
}
