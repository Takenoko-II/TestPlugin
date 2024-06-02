package com.gmail.subnokoii.testplugin;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIBuilder;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BungeeCordUtils {
    private BungeeCordUtils() {}

    public static ItemStack getServerSelector() {
        return new ItemStackBuilder(Material.COMPASS)
        .customName("Server Selector", NamedTextColor.GREEN)
        .lore("Right Click to Open", Color.GRAY)
        .glint(true)
        .maxCount(1)
        .dataContainer("locked", true)
        .dataContainer("custom_item_tag", "server_selector")
        .build();
    }

    public static void openServerSelector(Player player) {
        new ChestUIBuilder("Battle of Apostolos", 1)
        .set(1, builder -> {
            return builder.type(Material.NETHER_STAR)
            .customName("Game", NamedTextColor.AQUA)
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
            .customName("Lobby")
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
                .customName("Development", NamedTextColor.GOLD)
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
            .customName("Development", NamedTextColor.GOLD)
            .lore("権限がないため利用できません", Color.RED)
            .onClick(response -> {
                response.playSound(Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
                response.close();
            });
        })
        .set(7, builder -> {
            return builder.type(Material.RED_BED)
            .customName("Respawn", NamedTextColor.RED)
            .lore("ワールドのスポーンポイントに戻る", Color.GRAY)
            .onClick(response -> {
                final Location spawnPoint = player.getWorld().getSpawnLocation().add(0.5d, 0.0d, 0.5d);
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

        player.sendPluginMessage(TestPlugin.getInstance(), "BungeeCord", data);

        TestPlugin.log(TestPlugin.LoggingTarget.PLUGIN, player.getName() + "からBungeeCordチャンネルにプラグインメッセージが送信されました: [\"Connect\", \"" + targetServer + "\"]");
    }
}
