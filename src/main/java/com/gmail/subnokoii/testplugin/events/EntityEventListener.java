package com.gmail.subnokoii.testplugin.events;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemDataContainerAccessor;
import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class EntityEventListener extends BukkitRunnable implements Listener {
    private static EntityEventListener instance;

    public static EntityEventListener get() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new EntityEventListener();
            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.get());
            instance.runTaskTimer(TestPlugin.get(), 0L, 1L);
        }
    }

    private EntityEventListener() {}

    @Override
    public void run() {
        final World world = Bukkit.getServer().getWorld("world");

        if (world == null) return;

        for (final Entity entity : world.getEntities()) {
            if (entity instanceof FishHook && entity.getVelocity().isZero()) {
                onHookHit((FishHook) entity);
            }
        }
    }

    public void onHookHit(FishHook hook) {
        final ProjectileSource source = hook.getShooter();

        if (!(source instanceof Player)) return;

        final Player player = (Player) source;
        final ItemStack itemInMainHand = player.getEquipment().getItemInMainHand();
        final ItemStack itemInOffHand = player.getEquipment().getItemInOffHand();

        if (isGrapplingHook(itemInMainHand) || isGrapplingHook(itemInOffHand)) {
            if (Objects.equals(grapplingHookExists.get(player), true)) {
                hook.teleport(grapplingHookLocation.get(player).mergeWithLocation(hook.getLocation()));
                return;
            }

            grapplingHookExists.put(player, true);
            grapplingHookLocation.put(player, Vector3Builder.from(hook.getLocation()));

            hook.getWorld().spawnParticle(Particle.CRIT, hook.getLocation(), 25, 0.6, 0.6, 0.6, 0.0000001);
            hook.getWorld().playSound(hook.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 10, 2);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) return;

        final Player player = event.getPlayer();
        final Boolean hookExists = grapplingHookExists.get(player);
        final ItemStack itemInMainHand = player.getEquipment().getItemInMainHand();
        final ItemStack itemInOffHand = player.getEquipment().getItemInOffHand();

        if (Objects.equals(hookExists, true) && (isGrapplingHook(itemInMainHand) || isGrapplingHook(itemInOffHand))) {
            grapplingHookExists.put(player, false);

            final Vector3Builder vector = grapplingHookLocation.get(player);

            vector.subtract(Vector3Builder.from(player.getLocation()));
            vector.add(new Vector3Builder(0d, 0.25d, 0d));

            vector.calculate(component -> {
                final double absolute = Math.abs(component);
                return component / absolute * Math.cbrt(absolute);
            })
            .multiplyByScalar(0.7d);

            player.setVelocity(vector.toBukkitVector());

            final World world = player.getWorld();

            world.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 10f, 2f);
            world.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 10f, 0.8f);
            world.spawnParticle(Particle.CRIT, player.getLocation(), 50, 1.3d, 1.3d, 1.3d, 0.000000001);
        }
    }

    @EventHandler
    public void onRemove(EntityRemoveFromWorldEvent event) {
        final Entity entity = event.getEntity();

        if (!(entity instanceof Projectile)) return;

        final Projectile hook = (Projectile) entity;
        final ProjectileSource source = hook.getShooter();

        if (!(source instanceof Player)) return;

        final Player player = (Player) source;

        if (Objects.equals(grapplingHookExists.get(player), true)) {
            grapplingHookExists.put(player, false);
            TestPlugin.log("Plugin", "set to false");
        }
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        final Entity entity = event.getEntity();

        if (!entity.getType().equals(EntityType.MARKER)) return;

        final Set<String> tags = entity.getScoreboardTags();

        tags.forEach(tag -> {
            if (tag.startsWith("testplugin:")) {
                final String message = tag.split("testplugin:", 2)[1];

                final Entity[] entities = entity.getWorld()
                .getEntities()
                .stream()
                .filter(e -> e.getScoreboardTags().contains("plugin_api.target"))
                .toArray(Entity[]::new);

                onDataPackMessage(entities, message.split("\\s+"));
            }
        });
    }

    private void onDataPackMessage(Entity[] targets, String[] message) {
        if (message.length >= 1) {
            switch (message[0]) {
                case "knockback": {
                    if (message.length != 4) return;

                    try {
                        final double x = Double.parseDouble(message[1]);
                        final double y = Double.parseDouble(message[2]);
                        final double z = Double.parseDouble(message[3]);

                        for (final Entity target : targets) {
                            target.setVelocity(target.getVelocity().add(new Vector(x, y, z)));
                        }
                    }
                    catch (IllegalArgumentException ignored) {}

                    break;
                }
            }
        }
    }

    private final Map<Player, Boolean> grapplingHookExists = new HashMap<>();

    private final Map<Player, Vector3Builder> grapplingHookLocation = new HashMap<>();

    private boolean isGrapplingHook(ItemStack itemStack) {
        if (itemStack == null) return false;

        final String tag = new ItemDataContainerAccessor(itemStack).getString("custom_item_tag");
        return itemStack.getType().equals(Material.FISHING_ROD) && Objects.equals(tag, "grappling_hook");
    }
}
