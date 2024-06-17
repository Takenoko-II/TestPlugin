package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.Nullable;

public final class RepairCostComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private RepairCostComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        if (itemMeta instanceof Repairable) {
            return ((Repairable) itemMeta).hasRepairCost();
        }
        else return false;
    }

    @Override
    public void disable() {
        if (itemMeta instanceof Repairable) {
            ((Repairable) itemMeta).setRepairCost(1);
        }
    }

    public @Nullable Integer getRepairCost() {
        if (itemMeta instanceof Repairable) {
            return ((Repairable) itemMeta).getRepairCost();
        }
        else return null;
    }

    public void setRepairCost(int cost) {
        if (itemMeta instanceof Repairable) {
            ((Repairable) itemMeta).setRepairCost(cost);
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

    public static final String COMPONENT_ID = "minecraft:repairable";
}
