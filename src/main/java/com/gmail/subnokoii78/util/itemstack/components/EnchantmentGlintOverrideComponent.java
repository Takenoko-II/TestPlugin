package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantmentGlintOverrideComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private EnchantmentGlintOverrideComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return getGlintOverride();
    }

    @Override
    public void disable() {
        itemMeta.setEnchantmentGlintOverride(null);
    }

    public boolean getGlintOverride() {
        return itemMeta.getEnchantmentGlintOverride();
    }

    public void setGlintOverride(boolean flag) {
        itemMeta.setEnchantmentGlintOverride(flag);
    }

    @Override
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:enchantment_glint_override";
}
