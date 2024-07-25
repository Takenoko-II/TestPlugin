package com.gmail.subnokoii78.util.shape;

import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public final class DustSpawner extends ParticleSpawner<Particle.DustOptions> {
    public DustSpawner(@NotNull Particle.DustOptions data) {
        super(Particle.DUST, data);
    }
}
