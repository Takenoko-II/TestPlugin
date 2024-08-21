package com.gmail.subnokoii78.util.itemstack.components;

import com.gmail.subnokoii78.util.itemstack.PotionContent;
import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PotionContentsComponent extends TooltipShowable {
    private PotionContentsComponent(@NotNull ItemMeta itemMeta) {
        super(itemMeta);
    }

    @Override
    public boolean isEnabled() {
        if (itemMeta instanceof PotionMeta) {
            return ((PotionMeta) itemMeta).hasCustomEffects();
        }
        else return false;
    }

    public PotionContent[] getContents() {
        if (itemMeta instanceof PotionMeta) {
            if (((PotionMeta) itemMeta).hasCustomEffects()) {
                return ((PotionMeta) itemMeta).getCustomEffects()
                .stream().map(PotionContent::fromBukkit).toArray(PotionContent[]::new);
            }
            else if (((PotionMeta) itemMeta).getBasePotionType() != null) {
                return ((PotionMeta) itemMeta).getBasePotionType().getPotionEffects()
                .stream().map(PotionContent::fromBukkit).toArray(PotionContent[]::new);
            }
            else return new PotionContent[0];
        }
        else return new PotionContent[0];
    }

    public boolean hasContent(PotionEffectType type) {
        if (itemMeta instanceof PotionMeta) {
            return ((PotionMeta) itemMeta).hasCustomEffect(type) || ((PotionMeta) itemMeta).hasBasePotionType();
        }
        else return false;
    }

    public void addContent(PotionContent effect) {
        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).addCustomEffect(effect.toBukkit(), false);
        }
    }

    public @Nullable PotionType getBasePotion() {
        if (itemMeta instanceof PotionMeta) {
            return ((PotionMeta) itemMeta).getBasePotionType();
        }
        else return null;
    }

    public void setBasePotion(PotionType type) {
        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).setBasePotionType(type);
        }
    }

    public void removeContents(PotionEffectType type) {
        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).removeCustomEffect(type);
        }
    }

    public @Nullable Color getColor() {
        if (itemMeta instanceof PotionMeta) {
            return ((PotionMeta) itemMeta).getColor();
        }
        else return null;
    }

    public void setColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException();
        }

        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).setColor(color);
        }
    }

    @Override
    public void disable() {
        if (itemMeta instanceof PotionMeta) {
            ((PotionMeta) itemMeta).clearCustomEffects();
            ((PotionMeta) itemMeta).setBasePotionType(null);
        }
    }

    @Override
    public boolean getShowInTooltip() {
        return !itemMeta.hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        if (flag) itemMeta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        else itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
    }

    @Override
    public @NotNull String getComponentId() {
        return "minecraft:potion_contents";
    }

}
