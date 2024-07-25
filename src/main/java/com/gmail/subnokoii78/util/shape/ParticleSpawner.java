package com.gmail.subnokoii78.util.shape;

import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleSpawner<T> {
    private World world = Bukkit.getWorlds().getFirst();

    private final Vector3Builder center = new Vector3Builder();

    private final Particle particle;

    private final Vector3Builder delta = new Vector3Builder();

    private int count = 1;

    private double speed = 0;

    private final T data;

    public ParticleSpawner(@NotNull Particle particle, @Nullable T data) {
        this.particle = particle;
        this.data = data;
    }

    public ParticleSpawner<T> place(@NotNull World world, @NotNull Vector3Builder center) {
        this.world = world;
        this.center.x(center.x());
        this.center.y(center.y());
        this.center.z(center.z());
        return this;
    }

    public ParticleSpawner<T> delta(@NotNull Vector3Builder delta) {
        this.delta.x(delta.x());
        this.delta.y(delta.y());
        this.delta.z(delta.z());
        return this;
    }

    public ParticleSpawner<T> count(int count) {
        this.count = count;
        return this;
    }

    public ParticleSpawner<T> speed(double speed) {
        this.speed = speed;
        return this;
    }

    public void spawn() {
        world.spawnParticle(particle, center.withWorld(world), count, delta.x(), delta.y(), delta.z(), speed, data);
    }
}
