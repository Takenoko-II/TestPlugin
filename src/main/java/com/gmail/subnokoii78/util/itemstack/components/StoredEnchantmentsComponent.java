package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class StoredEnchantmentsComponent implements TooltipShowable {
    private final ItemMeta itemMeta;

    private StoredEnchantmentsComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
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
        if (itemMeta instanceof EnchantmentStorageMeta) {
            Registry.ENCHANTMENT.forEach(enchantment -> {
                ((EnchantmentStorageMeta) itemMeta).removeStoredEnchant(enchantment);
            });
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
