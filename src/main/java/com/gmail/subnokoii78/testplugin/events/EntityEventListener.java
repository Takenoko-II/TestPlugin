package com.gmail.subnokoii78.testplugin.events;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.util.datacontainer.ItemStackDataContainerManager;
import com.gmail.subnokoii78.util.vector.Vector3Builder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

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

            vector.calculate(component -> {
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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        final Entity damagingEntity = event.getDamager();

        if (!(damagingEntity instanceof LivingEntity)) return;

        final LivingEntity livingEntity = (LivingEntity) damagingEntity;

        final Entity hurtEntity = event.getEntity();

        if (!(hurtEntity instanceof Damageable)) return;

        final Damageable damageableEntity = (Damageable) hurtEntity;

        final ItemStack itemStack = livingEntity.getEquipment().getItemInMainHand();

        final String tag = new ItemStackDataContainerManager(itemStack).getString("custom_item_tag");

        if (tag == null) return;

        if (tag.equals("sword_of_overwrite")) {
            final BoundingBox box = damageableEntity.getBoundingBox();
            final Vector3Builder offset = new Vector3Builder(
                box.getWidthX() / 2,
                box.getHeight() / 2,
                box.getWidthZ() / 2
            );

            damageableEntity.getWorld().spawnParticle(
                Particle.DUST_COLOR_TRANSITION,
                damageableEntity.getLocation().add(0, offset.y(), 0),
                60,
                offset.x() + 0.3, offset.y() + 0.3, offset.z() + 0.3,
                0,
                new Particle.DustTransition(Color.RED, Color.ORANGE, 3)
            );

            damageableEntity.getWorld().spawnParticle(
                Particle.FLAME,
                damageableEntity.getLocation().add(0, offset.y(), 0),
                40,
                offset.x(), offset.y(), offset.z(),
                0.1
            );

            damageableEntity.getWorld().playSound(
                damageableEntity.getLocation().add(0, offset.y(), 0),
                Sound.ENTITY_BLAZE_SHOOT,
                5f, 1f
            );

            damageableEntity.getWorld().playSound(
                damageableEntity.getLocation().add(0, offset.y(), 0),
                Sound.ENTITY_ZOMBIE_VILLAGER_CURE,
                5f, 2f
            );

            if (damageableEntity instanceof Lootable) {
                ((Lootable) hurtEntity).clearLootTable();
            }

            damageableEntity.setHealth(0);
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
