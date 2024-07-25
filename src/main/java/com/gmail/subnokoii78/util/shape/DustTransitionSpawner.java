package com.gmail.subnokoii78.util.shape;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public final class DustTransitionSpawner extends ParticleSpawner<Particle.DustTransition> {
    public DustTransitionSpawner(@NotNull Particle.DustTransition data) {
        super(Particle.DUST_COLOR_TRANSITION, data);
    }
}
