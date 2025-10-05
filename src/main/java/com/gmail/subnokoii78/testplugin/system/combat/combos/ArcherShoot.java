package com.gmail.subnokoii78.testplugin.system.combat.combos;

import com.gmail.subnokoii78.tplcore.entity.FakeArrowLauncher;
import com.gmail.subnokoii78.tplcore.shape.DustSpawner;
import com.gmail.subnokoii78.tplcore.shape.ParticleSpawner;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ArcherShoot extends Combo {
    private ArcherShoot() {
        super("archer_shoot", 3, 5 * 20, 0);
    }

    @Override
    public void onComboProgress(Player player, int currentComboCount, @Nullable Object data) {
        if (!(data instanceof Entity target)) {
            throw new IllegalStateException();
        }

        final BoundingBox box = target.getBoundingBox();

        new ParticleSpawner<>(Particle.CRIT)
            .place(target.getLocation())
            .delta(new Vector3Builder(box.getWidthX(), box.getHeight(), box.getWidthZ()))
            .speed(0.2)
            .count(20)
            .spawn();

        new DustSpawner(new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.25f))
            .place(target.getLocation())
            .delta(new Vector3Builder(box.getWidthX(), box.getHeight(), box.getWidthZ()))
            .speed(1)
            .count(20)
            .spawn();

        target.getWorld().playSound(
            target.getLocation(),
            Sound.ENTITY_FIREWORK_ROCKET_BLAST,
            10f,
            1.1f
        );

        player.sendMessage(Component.text("コンボ数: " + currentComboCount));
    }

    @Override
    public void onComboComplete(Player player, @Nullable Object data) {
        if (!(data instanceof Damageable target)) {
            throw new IllegalStateException();
        }

        final BoundingBox box = target.getBoundingBox();

        new ParticleSpawner<>(Particle.FLASH)
            .place(target.getLocation())
            .delta(new Vector3Builder(box.getWidthX(), box.getHeight(), box.getWidthZ()))
            .speed(0)
            .count(4)
            .spawn();

        target.setVelocity(
            target.getVelocity().add(
                Vector3Builder.from(player).getDirectionTo(Vector3Builder.from(target))
                    .scale(2d)
                    .y(0.5d)
                    .toBukkitVector()
            )
        );

        player.sendMessage(Component.text("コンボ完成").color(NamedTextColor.GREEN));
    }

    @Override
    public void onComboStop(Player player) {
        player.sendMessage(Component.text("コンボ中断").color(NamedTextColor.RED));
    }

    public FakeArrowLauncher getLauncher(Player player) {
        return new FakeArrowLauncher(player)
            .setDamage(2.5)
            .setCritical(true)
            .setPower(2.5)
            .setTrailParticle(
                new DustSpawner(new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1))
                    .count(5)
                    .speed(0)
                    .delta(new Vector3Builder(0.3, 0.3, 0.3))
            );
    }

    public static final ArcherShoot ARCHER_SHOOT = new ArcherShoot();
}
