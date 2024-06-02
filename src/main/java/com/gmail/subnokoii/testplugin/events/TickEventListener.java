package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.datacontainer.EntityDataContainerManager;
import com.gmail.subnokoii.testplugin.lib.datacontainer.ItemStackDataContainerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.Objects;

public class TickEventListener extends BukkitRunnable {
    private static TickEventListener instance;

    public static TickEventListener get() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new TickEventListener();
            instance.runTaskTimer(TestPlugin.getInstance(), 0L, 1L);
        }
    }

    private TickEventListener() {}

    @Override
    public void run() {
        if (Bukkit.getServer().getServerTickManager().isFrozen()) return;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            final ItemStack itemStackInMainHand = player.getEquipment().getItemInMainHand();
            final ItemStack itemStackInOffHand = player.getEquipment().getItemInOffHand();

            final String tagInMainHand = new ItemStackDataContainerManager(itemStackInMainHand).getString("custom_item_tag");
            final String tagInOffHand = new ItemStackDataContainerManager(itemStackInOffHand).getString("custom_item_tag");

            if (Objects.equals(tagInMainHand, "data_getter") || Objects.equals(tagInOffHand, "data_getter")) {
                final Entity entity = player.getTargetEntity(16);
                final Block block = player.getTargetBlockExact(16);

                if (entity != null) {
                    final Location location = entity.getLocation();
                    final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x00FF00), 0.8f);

                    final BoundingBox box = entity.getBoundingBox();

                    player.spawnParticle(Particle.DUST, location, 10, box.getWidthX() / 2, box.getHeight(), box.getWidthZ() / 2, 0.000000001d, dustOptions);
                }
                else if (block != null) {
                    final Location location = block.getLocation();
                    final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x00FF00), 0.5f);

                    player.spawnParticle(Particle.DUST, location.clone().add(0.5d, 0d, 0d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(0.5d, 1d, 0d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(0.5d, 0d, 1d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(0.5d, 1d, 1d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);

                    player.spawnParticle(Particle.DUST, location.clone().add(0d, 0d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(1d, 0d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(0d, 1d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(1d, 1d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);

                    player.spawnParticle(Particle.DUST, location.clone().add(0d, 0.5d, 0d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(1d, 0.5d, 0d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(0d, 0.5d, 1d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.DUST, location.clone().add(1d, 0.5d, 1d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                }
            }
        }
    }
}
