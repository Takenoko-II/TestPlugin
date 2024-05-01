package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.scoreboard.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Objects;

public class TickListener extends BukkitRunnable {
    @Override
    public void run() {
        final ScoreboardUtilsObjective objective = ScoreboardUtils.getObjective("plugin.scheduler.tick_listener");

        final ScoreboardUtilsObjective objX = ScoreboardUtils.getObjective("plugin.api.knockback.x");
        final ScoreboardUtilsObjective objY = ScoreboardUtils.getObjective("plugin.api.knockback.y");
        final ScoreboardUtilsObjective objZ = ScoreboardUtils.getObjective("plugin.api.knockback.z");

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (objective.getScore(player) > 0) {
                objective.setScore(player, 0);

                final Vector velocity = player.getVelocity();

                final double x = ((double) objX.getScore(player)) / 1000d;
                final double y = ((double) objY.getScore(player)) / 1000d;
                final double z = ((double) objZ.getScore(player)) / 1000d;

                objX.setScore(player, 0);
                objY.setScore(player, 0);
                objZ.setScore(player, 0);

                player.setVelocity(new Vector(x, y, z).add(velocity));
            }

            final ItemStack itemStackInMainHand = player.getEquipment().getItemInMainHand();
            final ItemStack itemStackInOffHand = player.getEquipment().getItemInOffHand();

            final String tagInMainHand = NBTEditor.getString(itemStackInMainHand, "plugin", "custom_item_tag");
            final String tagInOffHand = NBTEditor.getString(itemStackInOffHand, "plugin", "custom_item_tag");

            if (Objects.equals(tagInMainHand, "data_getter") || Objects.equals(tagInOffHand, "data_getter")) {
                final Entity entity = player.getTargetEntity(16);
                final Block block = player.getTargetBlockExact(16);

                if (entity != null) {
                    final Location location = entity.getLocation();
                    final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x00FF00), 0.8f);

                    player.spawnParticle(Particle.REDSTONE, location, 8, 0.4d, 0.8d, 0.4d, 0.000000001d, dustOptions);
                }
                else if (block != null) {
                    final Location location = block.getLocation();
                    final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x00FF00), 0.5f);

                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0.5d, 0d, 0d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0.5d, 1d, 0d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0.5d, 0d, 1d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0.5d, 1d, 1d), 4, 0.4d, 0.0d, 0.0d, 0.000000001d, dustOptions);

                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0d, 0d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(1d, 0d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0d, 1d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(1d, 1d, 0.5d), 4, 0.0d, 0.0d, 0.4d, 0.000000001d, dustOptions);

                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0d, 0.5d, 0d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(1d, 0.5d, 0d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(0d, 0.5d, 1d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                    player.spawnParticle(Particle.REDSTONE, location.clone().add(1d, 0.5d, 1d), 4, 0.0d, 0.4d, 0.0d, 0.000000001d, dustOptions);
                }
            }
        }
    }
}
