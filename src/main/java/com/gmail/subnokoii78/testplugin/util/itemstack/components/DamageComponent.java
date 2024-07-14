package com.gmail.subnokoii78.testplugin.util.itemstack.components;

import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class DamageComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private DamageComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
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
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }
}
