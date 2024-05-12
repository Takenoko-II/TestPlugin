package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIBuilder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class BungeeCordUtils {
    private BungeeCordUtils() {}

    public static ItemStack getServerSelector() {
        return new ItemStackBuilder(Material.COMPASS)
        .name("Server Selector", Color.fromRGB(0x00FF55))
        .lore("Right Click to Open", Color.GRAY)
        .enchantment(Enchantment.INFINITY, 1)
        .hideFlag(ItemFlag.HIDE_ENCHANTS)
        .dataContainer("locked", true)
        .dataContainer("custom_item_tag", "server_selector")
        .get();
    }

    public static void openServerSelector(Player player) {
        new ChestUIBuilder("Battle of Apostolos", 1)
        .set(1, builder -> {
            return builder.type(Material.NETHER_STAR)
            .name("Game", Color.AQUA)
            .lore("ゲームサーバーに移動", Color.GRAY)
            .onClick(response -> {
                transfer(player, "game");
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
                transfer(player, "lobby");
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
                    transfer(player, "develop");
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
        })
        .open(player);
    }

    public static void transfer(Player player, String targetServer) {
        final ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(targetServer);
        final byte[] data = output.toByteArray();

        player.sendPluginMessage(TestPlugin.get(), "BungeeCord", data);

        TestPlugin.log("Plugin", player.getName() + "からBungeeCordチャンネルにプラグインメッセージが送信されました: [\"Connect\", \"" + targetServer + "\"]");
    }
}
