package com.gmail.subnokoii.testplugin.events;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii.testplugin.BungeeCordUtils;
import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.datacontainer.ItemStackDataContainerManager;
import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.getInstance());
            instance.runTaskTimer(TestPlugin.getInstance(), 0L, 1L);

            registerPluginEvents();
        }
    }

    private static void registerPluginEvents() {
        TestPlugin.events().onDataPackMessageReceive(event -> {
            final String id = event.getId();
            final String[] parameters = event.getParameters();
            final Entity[] targets = event.getTargets();

            switch (id) {
                case "logging": {
                    if (parameters.length < 1) return;

                    final String message = String.join(" ", parameters);

                    TestPlugin.log(TestPlugin.LoggingTarget.ALL, message);

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
                case "transfer": {
                    if (parameters.length != 1) return;

                    for (final Entity target : targets) {
                        if (!(target instanceof Player)) continue;

                        final BungeeCordUtils.ServerType serverType = BungeeCordUtils.ServerType.from(parameters[0]);

                        if (serverType == null) return;

                        BungeeCordUtils.transfer((Player) target, serverType);
                    }

                    break;
                }
                case "math": {
                    if (parameters.length <= 1) return;

                    final double x;

                    try {
                        x = Double.parseDouble(parameters[1]);
                    }
                    catch (IllegalArgumentException e) {
                        return;
                    }

                    switch (parameters[0]) {
                        case "sin": {
                            event.returnValue(Math.sin(x * Math.PI / 180));
                            break;
                        }
                        case "cos": {
                            event.returnValue(Math.cos(x * Math.PI / 180));
                            break;
                        }
                        case "tan": {
                            event.returnValue(Math.tan(x * Math.PI / 180));
                            break;
                        }
                        case "sqrt": {
                            event.returnValue(Math.sqrt(x));
                            break;
                        }
                        case "cbrt": {
                            event.returnValue(Math.cbrt(x));
                            break;
                        }
                    }

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
                hook.teleport(grapplingHookLocation.get(player).withLocation(hook.getLocation()));
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

            vector.calc(component -> {
                final double absolute = Math.abs(component);
                return component / absolute * Math.cbrt(absolute);
            })
            .scale(0.7d);

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
        }
    }

    private final Map<Player, Boolean> grapplingHookExists = new HashMap<>();

    private final Map<Player, Vector3Builder> grapplingHookLocation = new HashMap<>();

    private boolean isGrapplingHook(ItemStack itemStack) {
        if (itemStack == null) return false;

        final String tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");
        return itemStack.getType().equals(Material.FISHING_ROD) && Objects.equals(tag, "grappling_hook");
    }
}
