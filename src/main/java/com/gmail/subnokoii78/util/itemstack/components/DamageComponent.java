package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public final class DamageComponent extends ItemStackComponent {
    private DamageComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof Damageable) {
            return ((Damageable) itemMeta).hasDamage();
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(0);
        }
    }

    public Integer getDamage() {
        if (itemMeta instanceof Damageable) {
            return ((Damageable) itemMeta).getDamage();
        }
        else return null;
    }

    public void setDamage(int damage) {
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:damage";
    }
}
