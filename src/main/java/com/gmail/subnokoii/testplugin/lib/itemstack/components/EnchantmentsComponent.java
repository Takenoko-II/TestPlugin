package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public final class EnchantmentsComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private EnchantmentsComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return itemMeta.hasEnchants();
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return itemMeta.getEnchants();
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
    }

    public void removeEnchantment(Enchantment enchantment) {
        itemMeta.removeEnchant(enchantment);
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return itemMeta.hasEnchant(enchantment);
    }

    @Override
    public void disable() {
        itemMeta.removeEnchantments();
    }

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        else itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public static final String COMPONENT_ID = "minecraft:enchantments";
}
