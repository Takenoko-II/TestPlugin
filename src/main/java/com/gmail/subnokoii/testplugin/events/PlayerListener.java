package com.gmail.subnokoii.testplugin.events;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import com.gmail.subnokoii.testplugin.lib.scoreboard.ScoreboardUtils;
import com.gmail.subnokoii.testplugin.lib.ui.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

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

            if (type == null || content == null) return;
            if (!(type.equals("open_ui") && content.equals("server_selector"))) return;

            final ChestUIBuilder ui = new ChestUIBuilder("Battle of Apostolos", 1)
            .set(1, builder -> {
                return builder.type(Material.NETHER_STAR)
                .name("Game", Color.AQUA)
                .lore("ゲームサーバーに移動", Color.GRAY)
                .onClick(response -> {
                    response.getPlayer().chat("$PluginMessageSender;TransferPlayer;game");
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
                    response.getPlayer().chat("$PluginMessageSender;TransferPlayer;lobby");
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
                        response.getPlayer().chat("$PluginMessageSender;TransferPlayer;develop");
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

            ui.open(player);
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
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

        final ItemStack itemStack = new ItemStackBuilder(Material.COMPASS)
        .name("Server Selector")
        .lore("Right Click to Open", Color.GRAY)
        .enchantment(Enchantment.ARROW_INFINITE, 1)
        .hideFlag(ItemFlag.HIDE_ENCHANTS)
        .get();

        final String json = "{\"locked\": true, \"on_right_click\": {\"type\": \"open_ui\", \"content\":\"server_selector\" }}";

        final ItemStack serverSelector = NBTEditor.set(itemStack, NBTEditor.NBTCompound.fromJson(json), "plugin");

        final PlayerInventory inventory = player.getInventory();

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
    }

    private static final Map<Player, Boolean> isLeftClick = new HashMap<Player, Boolean>();

    private static final Map<Player, Long> lastBlockBreakTimestamp = new HashMap<Player, Long>();

    private static void onLeftClick(Player player) {

        ScoreboardUtils
        .getObjective("plugin.events.player.left_click")
        .addScore(player, 1);
    }
}
