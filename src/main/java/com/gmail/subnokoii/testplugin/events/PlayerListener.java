package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.event.data.PlayerClickEvent;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.vector.RotationBuilder;
import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {
    private static PlayerListener instance;

    public static PlayerListener get() {
        if (instance == null) instance = new PlayerListener();

        return instance;
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        final boolean locked = NBTEditor.getBoolean(itemStack, "plugin", "locked");

        if (locked && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();
        if (!player.getInventory().equals(inventory)) return;

        final ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null) return;

        final boolean locked = NBTEditor.getBoolean(itemStack, "plugin", "locked");
        if (locked && !player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        final ItemStack mainHandItem = event.getMainHandItem();
        final ItemStack offhandItem = event.getOffHandItem();

        final boolean mainHandLocked = NBTEditor.getBoolean(mainHandItem, "plugin", "locked");
        final boolean offHandLocked = NBTEditor.getBoolean(offhandItem, "plugin", "locked");

        if (mainHandLocked && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            return;
        }

        if (offHandLocked && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack serverSelector = TestPlugin.getServerSelector();

        if (!inventory.contains(serverSelector)) {
            inventory.addItem(serverSelector);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();

        TestPlugin.log("Plugin", player.getName() + " joined the server.");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        TestPlugin.log("Plugin", event.getPlayer().getName() + " left.");
    }

    public void onLeftClick(PlayerClickEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemStack();

        final String tag = NBTEditor.getString(itemStack, "plugin", "custom_item_tag");

        if (tag != null) {
            switch (tag) {
                case "instant_shoot_bow": {
                    final Integer count = shootableCountByLeftClick.get(player);

                    if (count == null) break;
                    if (count <= 0) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 5f, 1f);
                        break;
                    }

                    shootableCountByLeftClick.put(player, count - 1);

                    final Vector3Builder vector = RotationBuilder.from(player.getLocation())
                    .getDirection3d();

                    final Arrow arrow = player.getWorld().spawnArrow(player.getEyeLocation(), vector.toBukkitVector(), 1.6f, 12f);
                    arrow.setCritical(true);
                    arrow.setDamage(4d);
                    arrow.setLifetimeTicks(1200);
                    arrow.setShooter(player);
                    arrow.setHasLeftShooter(false);

                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 5f, 1f);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onShot(EntityShootBowEvent event) {
        final Entity entity = event.getEntity();
        final Entity projectile = event.getProjectile();

        if (entity instanceof Player && projectile instanceof Arrow) {
            final Player player = (Player) entity;
            final Arrow arrow = (Arrow) projectile;

            if (!arrow.isCritical()) return;

            shootableCountByLeftClick.put(player, 3);
        }
    }

    static private final Map<Player, Integer> shootableCountByLeftClick = new HashMap<>();
}
