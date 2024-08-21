package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class MaxDamageComponent extends ItemStackComponent {
    private MaxDamageComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof Damageable) {
            return ((Damageable) itemMeta).hasMaxDamage();
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setMaxDamage(null);
        }
    }

    public Integer getMaxDamage() {
        if (itemMeta instanceof Damageable) {
            return ((Damageable) itemMeta).getMaxDamage();
        }
        else return null;
    }

    public void setMaxDamage(int damage) {
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setMaxDamage(damage);
        }
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:max_damage";
    }
}
