package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.BungeeCordUtils;
import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.event.data.PlayerClickEvent;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemDataContainerAccessor;
import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii.testplugin.lib.vector.RotationBuilder;
import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlayerEventListener implements Listener {
    private static PlayerEventListener instance;

    public static PlayerEventListener get() {
        if (instance == null) {
            throw new RuntimeException("init()が実行されるよりも前にインスタンスを取得することはできません");
        }

        return instance;
    }

    public static void init() {
        if (instance == null) {
            instance = new PlayerEventListener();

            Bukkit.getServer().getPluginManager().registerEvents(instance, TestPlugin.get());

            registerPluginEvents();
        }
    }

    private static void registerPluginEvents() {
        TestPlugin.events().onLeftClick(event -> {
            instance.onLeftClick(event);

            final ItemStack itemStack = event.getItemStack();

            ScoreboardUtils
            .getOrCreateObjective("plugin_api.on_left_click")
            .addScore(event.getPlayer(), 1);

            final String type = new ItemDataContainerAccessor(itemStack).getString("on_left_click.type");
            final String content = new ItemDataContainerAccessor(itemStack).getString("on_left_click.content");

            if (type != null && content != null) {
                if (type.equals("run_command")) {
                    TestPlugin.runCommand(event.getPlayer(), content);
                }
            }
        });

        TestPlugin.events().onRightClick(event -> {
            final Player player = event.getPlayer();
            final ItemStack itemStack = event.getItemStack();

            if (itemStack == null) return;

            final String type = new ItemDataContainerAccessor(itemStack).getString("on_right_click.type");
            final String content = new ItemDataContainerAccessor(itemStack).getString("on_right_click.content");

            if (type != null && content != null) {
                switch (type) {
                    case "run_command":
                        TestPlugin.runCommand(player, content);
                        break;
                }
            }
        });

        TestPlugin.events().onCustomItemUse(event -> {
            final Player player = event.getPlayer();

            switch (event.getTag()) {
                case "quick_teleporter": {
                    event.cancel();

                    final Block block = player.getTargetBlockExact(127);
                    final BlockFace face = player.getTargetBlockFace(127);
                    final Location previousLocation = player.getLocation();

                    if (block == null || face == null) break;

                    final Vector3Builder location = Vector3Builder.from(block.getLocation());
                    final Vector3Builder direction = RotationBuilder.from(previousLocation).getDirection3d();

                    location.add(new Vector3Builder(face.getModX(), face.getModY(), face.getModZ())).subtract(direction.length(0.5d));

                    final Location destination = location.toLocation(previousLocation);

                    player.teleport(destination);
                    player.getWorld().playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 10.0f, 2.0f);
                    player.getWorld().spawnParticle(Particle.PORTAL, destination, 40, 0.5d, 1.0d, 0.5d);

                    break;
                }
                case "data_getter": {
                    event.cancel();

                    final Entity entity = player.getTargetEntity(16);
                    final Block block = player.getTargetBlockExact(16);

                    if (entity != null) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                        player.getWorld().spawnParticle(Particle.ENCHANT, entity.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                        player.sendMessage(Component.text(entity.getType().name() + "は以下のNBTを持っています:\n" + entity.getAsString()));

                    }
                    else if (block != null) {
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                        player.getWorld().spawnParticle(Particle.ENCHANT, block.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                        player.sendMessage(Component.text(block.getType().name() + "は以下のデータを持っています:\n" + block.getBlockData().getAsString()));
                    }

                    break;
                }
                case "tick_progress_canceler": {
                    event.cancel();

                    final ServerTickManager manager = player.getServer().getServerTickManager();
                    final boolean isFrozen = manager.isFrozen();

                    if (isFrozen) {
                        manager.setFrozen(false);
                        player.getServer().getOnlinePlayers().forEach(p -> {
                            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10.0f, 2.0f);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 127, false, false));
                        });
                    }
                    else {
                        manager.setFrozen(true);
                        player.getServer().getOnlinePlayers().forEach(p -> {
                            p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 10.0f, 0.8f);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 127, false, false));
                        });
                    }
                    break;
                }
                case "server_selector": {
                    event.cancel();
                    BungeeCordUtils.openServerSelector(player);
                }
            }
        });
    }

    private PlayerEventListener() {}

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        final Boolean locked = new ItemDataContainerAccessor(itemStack).getBoolean("locked");

        if (locked == null) return;

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

        final Boolean locked = new ItemDataContainerAccessor(itemStack).getBoolean("locked");

        if (locked == null) return;

        if (locked && !player.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        final ItemStack mainHandItem = event.getMainHandItem();
        final ItemStack offhandItem = event.getOffHandItem();

        final Boolean mainHandLocked = new ItemDataContainerAccessor(mainHandItem).getBoolean("locked");
        final Boolean offHandLocked = new ItemDataContainerAccessor(offhandItem).getBoolean("locked");

        if (Objects.equals(mainHandLocked, true) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            return;
        }

        if (Objects.equals(offHandLocked, true) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        final ItemStack serverSelector = BungeeCordUtils.getServerSelector();

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

        final String tag = new ItemDataContainerAccessor(itemStack).getString("custom_item_tag");

        if (tag != null) {
            switch (tag) {
                case "instant_shoot_bow": {
                    final int count = Objects.requireNonNullElse(shootableCountByLeftClick.get(player), 0);

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
