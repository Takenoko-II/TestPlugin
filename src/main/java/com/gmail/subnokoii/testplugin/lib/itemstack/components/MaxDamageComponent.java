package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class MaxDamageComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private MaxDamageComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
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
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }
}
