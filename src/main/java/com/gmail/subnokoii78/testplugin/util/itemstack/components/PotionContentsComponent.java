package com.gmail.subnokoii78.testplugin.util.itemstack.components;

import org.bukkit.Color;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

public final class PotionContentsComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private PotionContentsComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        if (itemMeta instanceof PotionMeta) {
            return ((PotionMeta) itemMeta).hasCustomEffects();
        }
        else return false;
    }

    public PotionContent[] getContents() {
        if (itemMeta instanceof PotionMeta) {
            if (((PotionMeta) itemMeta).hasCustomEffects()) {
                return ((PotionMeta) itemMeta).getCustomEffects()
                .stream().map(PotionContent::from).toArray(PotionContent[]::new);
            }
            else if (((PotionMeta) itemMeta).getBasePotionType() != null) {
                return ((PotionMeta) itemMeta).getBasePotionType().getPotionEffects()
                .stream().map(PotionContent::from).toArray(PotionContent[]::new);
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
            ((PotionMeta) itemMeta).addCustomEffect(effect.toPotionEffect(), false);
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

    public static final class PotionContent {
        private final PotionEffectType type;

        private int amplifier = 0;

        private int duration = 600;

        private boolean showParticles = true;

        public PotionContent(PotionEffectType type) {
            if (type == null) {
                throw new IllegalArgumentException();
            }

            this.type = type;
        }

        public PotionEffectType getType() {
            return type;
        }

        public int getAmplifier() {
            return amplifier;
        }

        public int getDuration() {
            return duration;
        }

        public boolean getShowParticles() {
            return showParticles;
        }

        public PotionContent setAmplifier(int amplifier) {
            this.amplifier = amplifier;

            return this;
        }

        public PotionContent setDuration(int duration) {
            this.duration = duration;

            return this;
        }

        public PotionContent setShowParticles(boolean showParticles) {
            this.showParticles = showParticles;

            return this;
        }

        private PotionEffect toPotionEffect() {
            return new PotionEffect(type, duration, amplifier, false, showParticles);
        }

        private static PotionContent from(PotionEffect effect) {
            return new PotionContent(effect.getType())
            .setAmplifier(effect.getAmplifier())
            .setDuration(effect.getDuration())
            .setShowParticles(effect.hasParticles());
        }
    }

    public static final String COMPONENT_ID = "minecraft:potion_contents";
}
