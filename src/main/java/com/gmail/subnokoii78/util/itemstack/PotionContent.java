package com.gmail.subnokoii78.util.itemstack;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PotionContent {
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

    public int amplifier() {
        return amplifier;
    }

    public int duration() {
        return duration;
    }

    public boolean showParticles() {
        return showParticles;
    }

    public PotionContent amplifier(int amplifier) {
        this.amplifier = amplifier;

        return this;
    }

    public PotionContent duration(int duration) {
        this.duration = duration;

        return this;
    }

    public PotionContent showParticles(boolean showParticles) {
        this.showParticles = showParticles;

        return this;
    }

    public PotionEffect toBukkit() {
        return new PotionEffect(type, duration, amplifier, false, showParticles);
    }

    public static PotionContent fromBukkit(PotionEffect effect) {
        return new PotionContent(effect.getType())
            .amplifier(effect.getAmplifier())
            .duration(effect.getDuration())
            .showParticles(effect.hasParticles());
    }
}
