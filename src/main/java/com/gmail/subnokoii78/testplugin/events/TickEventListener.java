package com.gmail.subnokoii78.testplugin.events;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.TickEvent;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackCustomDataAccess;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TickEventListener {
    public static final TickEventListener INSTANCE = new TickEventListener();

    private TickEventListener() {}

    public void onTick(@NotNull TickEvent event) {
        if (event.isFrozen()) return;

        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            final EntityEquipment equipment = player.getEquipment();
            if (equipment == null) continue;
            final MojangsonCompound mainHand = ItemStackCustomDataAccess.of(equipment.getItemInMainHand()).read();
            final MojangsonCompound offHand = ItemStackCustomDataAccess.of(equipment.getItemInOffHand()).read();

            final String tagInMainHand = mainHand.has("custom_item_tag")
                ? mainHand.get("custom_item_tag", MojangsonValueTypes.STRING).getValue()
                : null;
            final String tagInOffHand = offHand.has("custom_item_tag")
                ? offHand.get("custom_item_tag", MojangsonValueTypes.STRING).getValue()
                : null;

            if (Objects.equals(tagInMainHand, "data_getter") || Objects.equals(tagInOffHand, "data_getter")) {
                final Entity entity = player.getTargetEntity(16);
                final Block block = player.getTargetBlockExact(16);

                if (entity != null) {
                    final Location location = entity.getLocation();
                    final Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0x00FF00), 0.8f);

                    final BoundingBox box = entity.getBoundingBox();

                    player.spawnParticle(Particle.DUST, location.add(0, box.getHeight() / 2, 0), 20, box.getWidthX() / 2, box.getHeight() / 2, box.getWidthZ() / 2, 0.000000001d, dustOptions);
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
