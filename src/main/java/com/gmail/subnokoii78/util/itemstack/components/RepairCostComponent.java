package com.gmail.subnokoii78.util.itemstack.components;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RepairCostComponent extends ItemStackComponent {
    private RepairCostComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
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
    public @NotNull String getComponentId() {
        return "minecraft:repairable";
    }
}
