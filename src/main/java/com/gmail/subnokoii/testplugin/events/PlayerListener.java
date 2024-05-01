package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii.testplugin.lib.ui.*;
import com.gmail.subnokoii.testplugin.lib.vector.RotationBuilder;
import com.gmail.subnokoii.testplugin.lib.vector.Vector3Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.structure.StructureManager;

import java.util.HashMap;
import java.util.Map;

public class PlayerListener implements Listener {
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        final ItemStack itemStack = event.getItemDrop().getItemStack();
        final boolean locked = NBTEditor.getBoolean(itemStack, "plugin", "locked");

        if (locked && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            return;
        }

        PlayerListener.isLeftClick.put(event.getPlayer(), false);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        PlayerListener.lastBlockBreakTimestamp.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().isLeftClick()) {
            long lastBlockBreakTimestamp = PlayerListener.lastBlockBreakTimestamp.get(event.getPlayer());

            if (System.currentTimeMillis() - lastBlockBreakTimestamp < 50) {
                return;
            }

            final Player player = event.getPlayer();

            final boolean isNonDrop = PlayerListener.isLeftClick.get(player);
            PlayerListener.isLeftClick.put(player, true);

            if (isNonDrop) {
                PlayerListener.onLeftClick(player);
            }
        }
        else if (event.getAction().isRightClick()) {
            final Player player = event.getPlayer();
            final ItemStack itemStack = event.getItem();

            if (itemStack == null) return;

            final String type = NBTEditor.getString(itemStack, "plugin", "on_right_click", "type");
            final String content = NBTEditor.getString(itemStack, "plugin", "on_right_click", "content");

            if (type != null && content != null) {
                final ChestUIBuilder ui = new ChestUIBuilder("Battle of Apostolos", 1)
                .set(1, builder -> {
                    return builder.type(Material.NETHER_STAR)
                    .name("Game", Color.AQUA)
                    .lore("ゲームサーバーに移動", Color.GRAY)
                    .onClick(response -> {
                        TestPlugin.transfer(player, "game");
                        player.sendMessage("gameサーバーへの接続を試行中...");
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                        response.close();
                    });
                })
                .set(3, builder -> {
                    return builder.type(Material.PAPER)
                    .name("Lobby", Color.WHITE)
                    .lore("ロビーサーバーに移動", Color.GRAY)
                    .glint(true)
                    .onClick(response -> {
                        TestPlugin.transfer(player, "lobby");
                        player.sendMessage("lobbyサーバーへの接続を試行中...");
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                        response.close();
                    });
                })
                .set(5, builder -> {
                    if (player.isOp()) {
                        return builder.type(Material.COMMAND_BLOCK)
                        .name("Development", Color.ORANGE)
                        .lore("開発サーバーに移動", Color.GRAY)
                        .glint(true)
                        .onClick(response -> {
                            TestPlugin.transfer(player, "develop");
                            player.sendMessage("developサーバーへの接続を試行中...");
                            response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                            response.close();
                        });
                    }
                    else return builder.type(Material.BARRIER)
                    .name("Development", Color.ORANGE)
                    .lore("権限がないため利用できません", Color.RED)
                    .onClick(response -> {
                        response.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
                        response.close();
                    });
                })
                .set(7, builder -> {
                    return builder.type(Material.RED_BED)
                    .name("Respawn", Color.RED)
                    .lore("ワールドのスポーンポイントに戻る", Color.GRAY)
                    .onClick(response -> {
                        final Location spawnPoint = player.getWorld().getSpawnLocation();
                        player.teleport(spawnPoint);
                        player.sendMessage("スポーンポイントに戻ります...");
                        response.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 10, 2);
                        response.close();
                    });
                });

                switch (type) {
                    case "run_command":
                        player.getServer().dispatchCommand(player, content);
                        break;
                    case "open_ui":
                        if (content.equals("server_selector")) ui.open(player);
                        break;
                }
            }

            final String tag = NBTEditor.getString(itemStack, "plugin", "custom_item_tag");

            if (tag != null) {
                switch (tag) {
                    case "quick_teleporter": {
                        event.setCancelled(true);

                        final Block block = player.getTargetBlockExact(127);
                        final BlockFace face = player.getTargetBlockFace(127);
                        final Location previousLocation = player.getLocation();

                        if (block == null || face == null) break;

                        final Vector3Builder location = Vector3Builder.from(block.getLocation());
                        final Vector3Builder direction = RotationBuilder.from(previousLocation).getDirection3d();

                        location.add(new Vector3Builder(face.getModX(), face.getModY(), face.getModZ())).subtract(direction.length(0.5d));

                        final Location destination = location.mergeWithLocation(previousLocation);

                        player.teleport(destination);
                        player.getWorld().playSound(destination, Sound.ENTITY_ENDERMAN_TELEPORT, 10.0f, 2.0f);
                        player.getWorld().spawnParticle(Particle.PORTAL, destination, 40, 0.5d, 1.0d, 0.5d);

                        break;
                    }
                    case "data_getter": {
                        event.setCancelled(true);

                        final Entity entity = player.getTargetEntity(16);
                        final Block block = player.getTargetBlockExact(16);

                        if (entity != null) {
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, entity.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                            final NBTEditor.NBTCompound nbt = NBTEditor.getNBTCompound(entity);

                            if (nbt == null) {
                                throw new RuntimeException("NBT持ってないエンティティなんておるわけないやろ！");
                            }

                            player.sendMessage(Component.text(entity.getType().name() + "は以下のNBTを持っています:\n" + nbt.toJson()));

                        }
                        else if (block != null) {
                            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 10.0f, 2.0f);
                            player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, block.getLocation(), 40, 0.6d, 0.6d, 0.6d);

                            final NBTEditor.NBTCompound nbt = NBTEditor.getNBTCompound(block);

                            if (nbt == null) {
                                player.sendMessage(Component.text("そのブロックはNBTを持っていません").color(TextColor.color(252, 64, 72)));
                                break;
                            }

                            player.sendMessage(Component.text(block.getType().name() + "は以下のNBTを持っています:\n" + nbt.toJson()));
                        }

                        break;
                    }
                    case "structure_manager": {
                        final StructureManager manager = player.getServer().getStructureManager();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityAttacked(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            PlayerListener.onLeftClick((Player) event.getDamager());
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

        PlayerListener.isLeftClick.put(player, true);
        PlayerListener.lastBlockBreakTimestamp.put(player, 0L);

        TestPlugin.log("Plugin", player.getName() + " joined the server.");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        TestPlugin.log("Plugin", event.getPlayer().getName() + " left.");
    }

    private static final Map<Player, Boolean> isLeftClick = new HashMap<>();

    private static final Map<Player, Long> lastBlockBreakTimestamp = new HashMap<>();

    private static void onLeftClick(Player player) {
        ScoreboardUtils
        .getObjective("plugin.events.player.left_click")
        .addScore(player, 1);

        final ItemStack itemStack = player.getEquipment().getItemInMainHand();

        final String type = NBTEditor.getString(itemStack, "plugin", "on_left_click", "type");
        final String content = NBTEditor.getString(itemStack, "plugin", "on_left_click", "content");

        if (type == null || content == null) return;

        if (type.equals("run_command")) {
            player.getServer().dispatchCommand(player, content);
        }
    }
}
