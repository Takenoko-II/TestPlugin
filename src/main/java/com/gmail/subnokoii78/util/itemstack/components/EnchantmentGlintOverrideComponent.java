package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class EnchantmentGlintOverrideComponent extends ItemStackComponent {
    private EnchantmentGlintOverrideComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
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
    public @NotNull String getComponentId() {
        return "minecraft:enchantment_glint_override";
    }
}
