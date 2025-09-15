package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButton;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Test implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("引数が不足しています").color(TextColor.color(252, 64, 72)));
            return false;
        }

        switch (args[0]) {
            case "get_info": {
                if (!sender.isOp()) {
                    sender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
                    return false;
                }
                else if (args.length < 2) {
                    sender.sendMessage(Component.text("引数が不足しています").color(TextColor.color(252, 64, 72)));
                    return false;
                }
                else if (args.length > 2) {
                    sender.sendMessage(Component.text("引数が多すぎます").color(TextColor.color(252, 64, 72)));
                    return false;
                }
                else {
                    switch (args[1]) {
                        case "plugin_name": {
                            final String name = TPLCore.getPlugin().getName();
                            sender.sendMessage("現在のプラグイン名は" + name + "です");
                            break;
                        }
                        case "api_version": {
                            final String version = TPLCore.getPlugin().getPluginMeta().getAPIVersion();
                            sender.sendMessage("APIのバージョンは" + version + "です");
                            break;
                        }
                        case "ip": {
                            final String ip = Bukkit.getServer().getIp();

                            if (ip.isEmpty()) sender.sendMessage("このサーバーはIPを持っていません");
                            else sender.sendMessage("このサーバーのIPは" + ip + "です");

                            break;
                        }
                        case "port": {
                            final int port = Bukkit.getServer().getPort();
                            sender.sendMessage("このサーバーのポートは" + port + "です");
                            break;
                        }
                        case "current_tick": {
                            final int tick = Bukkit.getServer().getCurrentTick();
                            sender.sendMessage("現在のtickは" + tick + "です");
                            break;
                        }
                        case "max_players": {
                            final int maxPlayers = Bukkit.getServer().getMaxPlayers();
                            sender.sendMessage("このサーバーのプレイヤーの最大人数は" + maxPlayers + "です");
                            break;
                        }
                        case "bukkit_version": {
                            final String version = Bukkit.getServer().getBukkitVersion();
                            sender.sendMessage("このサーバーのBukkitのバージョンは" + version + "です");
                            break;
                        }
                        default: {
                            sender.sendMessage(Component.text("無効な引数です").color(TextColor.color(252, 64, 72)));
                            break;
                        }
                    }

                    return true;
                }
            }
            case "get_server_selector": {
                if (!(sender instanceof Player)) return false;

                final Player player = (Player) sender;

                final ItemStack serverSelector = TPLCore.paperVelocityManager.getServerSelectorItemStack();

                if (player.getInventory().contains(serverSelector)) {
                    player.sendMessage(Component.text("あなたは既に所持しています").color(TextColor.color(252, 64, 72)));
                    return false;
                }

                player.getInventory().addItem(serverSelector);

                return true;
            }
            case "get_experimental_item": {
                if (!(sender instanceof Player)) return false;

                if (!sender.isOp()) {
                    sender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
                    return false;
                }

                final Player player = (Player) sender;
                ItemStackBuilder itemStackBuilder = new ItemStackBuilder();

                if (args.length == 2) {
                    switch (args[1]) {
                        case "grappling_hook": {
                            itemStackBuilder = itemStackBuilder
                                .copyWithType(Material.FISHING_ROD)
                                .itemName(Component.text("Grappling Hook").color(NamedTextColor.GOLD))
                                .customData(MojangsonPath.of("custom_item_tag"), "grappling_hook");
                            break;
                        }
                        case "instant_shoot_bow": {
                            itemStackBuilder = itemStackBuilder
                                .copyWithType(Material.BOW)
                                .customName(Component.text("Instant Shoot Bow").color(NamedTextColor.GOLD))
                                .customData(MojangsonPath.of("custom_item_tag"), "instant_shoot_bow");
                            break;
                        }
                        default: {
                            player.sendMessage(Component.text("無効な引数です").color(TextColor.color(252, 64, 72)));
                            return false;
                        }
                    }

                    player.getInventory().addItem(itemStackBuilder.build());

                    return true;
                }
                else {
                    player.sendMessage(Component.text("引数の数が不正です").color(TextColor.color(252, 64, 72)));
                    return false;
                }
            }
            case "open_test_ui": {
                final var ui = new ContainerInteraction(Component.text("title"), 3);
                ui.set(
                    0,
                    ItemButton.item(Material.STONE)
                        .glint(true)
                        .lore(Component.text("lore1"))
                        .lore(Component.text("lore2"))
                        .name(Component.text("name"))
                        .amount(77)
                        .onClick(event -> {
                            event.getPlayer().sendMessage("a");
                        })
                );
                ui.add(ItemButton.playerHead().player("Chuzume"));
                ui.add(ItemButton.playerHead().player(sender.getName()));
                ui.add(ItemButton.splashPotion().color(Color.GREEN));
                ui.add(ItemButton.armor(Material.NETHERITE_CHESTPLATE).trim(TrimMaterial.EMERALD, TrimPattern.SPIRE));
                if (sender instanceof Player player) ui.open(player);

                break;
            }
            default: {
                sender.sendMessage(Component.text("無効な引数です").color(TextColor.color(252, 64, 72)));
                break;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) return List.of("get_info", "get_server_selector", "get_experimental_item", "open_test_ui");
            else return List.of("get_server_selector");
        }
        else if (args.length == 2) {
            if (args[0].equals("get_info")) {
                return List.of("plugin_name", "api_version", "ip", "port", "current_tick", "max_players", "log_archive_size", "bukkit_version");
            }
            else if (args[0].equals("get_experimental_item")) {
                return List.of("grappling_hook", "instant_shoot_bow", "potion");
            }
        }

        return List.of();
    }
}
