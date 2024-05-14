package com.gmail.subnokoii.testplugin.events;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackDataContainerAccessor;
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

            registerPluginEvents();
        }
    }

    private static void registerPluginEvents() {
        TestPlugin.events().onDataPackMessageReceive(event -> {
            final String id = event.getId();
            final String[] parameters = event.getParameters();
            final Entity[] targets = event.getTargets();

            switch (id) {
                case "foo": {
                    if (parameters.length != 0) return;

                    TestPlugin.log("Server", "foo!");

                    break;
                }
                case "knockback": {
                    if (parameters.length != 3) return;

                    try {
                        final double x = Double.parseDouble(parameters[0]);
                        final double y = Double.parseDouble(parameters[1]);
                        final double z = Double.parseDouble(parameters[2]);

                        for (final Entity target : targets) {
                            target.setVelocity(target.getVelocity().add(new Vector(x, y, z)));
                        }
                    }
                    catch (IllegalArgumentException ignored) {}

                    break;
                }
            }
        });
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
                hook.teleport(grapplingHookLocation.get(player).toLocation(hook.getLocation()));
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

    private final Map<Player, Boolean> grapplingHookExists = new HashMap<>();

    private final Map<Player, Vector3Builder> grapplingHookLocation = new HashMap<>();

    private boolean isGrapplingHook(ItemStack itemStack) {
        if (itemStack == null) return false;

        final String tag = new ItemStackDataContainerAccessor(itemStack).getString("custom_item_tag");
        return itemStack.getType().equals(Material.FISHING_ROD) && Objects.equals(tag, "grappling_hook");
    }
}
