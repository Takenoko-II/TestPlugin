package com.gmail.subnokoii78.testplugin.system.transfer;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.tplcore.execute.DimensionAccess;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButton;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButtonClickSound;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@ApiStatus.Experimental
@NullMarked
public class PlayerTransferManager {
    private PlayerTransferManager() {}

    private static Location getSpawnLocation(DimensionAccess access) {
        return access.getWorld().getSpawnLocation().add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector());
    }

    public static void transfer(Player player, DimensionAccess dimensionAccess) {
        player.teleport(getSpawnLocation(dimensionAccess));
    }

    private static final ContainerInteraction SERVER_SELECTOR = new ContainerInteraction(Component.text("Battle of Apostolos"), 1)
        .set(1, ItemButton.item(Material.NETHER_STAR)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Game").color(NamedTextColor.AQUA))
            .lore(Component.text("ゲームサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .onClick(event -> {
                event.close();
                event.getPlayer().teleport(getSpawnLocation(DimensionAccess.of(TestPlugin.GAME_FIELD_DIMENSION_ID)));
            })
        )
        .set(3, ItemButton.item(Material.PAPER)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Lobby").color(NamedTextColor.GOLD))
            .lore(Component.text("ロビーサーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .glint(true)
            .onClick(event -> {
                event.close();
                event.getPlayer().teleport(getSpawnLocation(DimensionAccess.OVERWORLD));
            })
        )
        .set(5, ItemButton.item(Material.COMMAND_BLOCK)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Development").color(NamedTextColor.GOLD))
            .lore(Component.text("開発サーバーに接続する").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .glint(true)
            .onClick(event -> {
                event.close();
                if (event.getPlayer().isOp()) {
                    event.getPlayer().teleport(getSpawnLocation(DimensionAccess.of(TestPlugin.DEVELOPMENT_DIMENSION_ID)));
                }
                else {
                    event.getPlayer().sendMessage(Component.text("このサーバーへの接続はオペレーター権限が必要です").color(NamedTextColor.RED));
                }
            })
        )
        .set(7, ItemButton.item(Material.RED_BED)
            .clickSound(ItemButtonClickSound.BASIC)
            .name(Component.text("Spawn").color(NamedTextColor.RED))
            .lore(Component.text("スポーン地点に戻る").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
            .onClick(event -> {
                final Location spawnPoint = event.getPlayer().getRespawnLocation();
                event.close();
                event.getPlayer().teleport(
                    spawnPoint == null
                        ? event.getPlayer().getWorld().getSpawnLocation().add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector())
                        : spawnPoint.add(new Vector3Builder(0.5, 0.5, 0.5).toBukkitVector())
                );
            })
        );

    public static ContainerInteraction interaction() {
        return SERVER_SELECTOR.copy();
    }

    public static ItemStack itemStack() {
        return new ItemStackBuilder(Material.COMPASS)
            .itemName(Component.text("Server Selector").color(NamedTextColor.GREEN))
            .lore(Component.text("Right Click to Open").color(NamedTextColor.GRAY))
            .glint(true)
            .maxStackSize(1)
            .customData(MojangsonPath.of("locked"), (byte) 1)
            .customData(MojangsonPath.of("custom_item_tag"), "server_selector")
            .build();
    }
}
