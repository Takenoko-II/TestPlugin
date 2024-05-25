package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.BungeeCordUtils;
import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.datacontainer.DataContainerManager;
import com.gmail.subnokoii.testplugin.lib.datacontainer.EntityDataContainerManager;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import com.gmail.subnokoii.testplugin.lib.itemstack.ItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.datacontainer.DataContainerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                            final String name = TestPlugin.get().getName();
                            sender.sendMessage("現在のプラグイン名は" + name + "です");
                            break;
                        }
                        case "api_version": {
                            final String version = TestPlugin.get().getPluginMeta().getAPIVersion();
                            sender.sendMessage("APIのバージョンは" + version + "です");
                            break;
                        }
                        case "ip": {
                            final String ip = TestPlugin.get().getServer().getIp();

                            if (ip.isEmpty()) sender.sendMessage("このサーバーはIPを持っていません");
                            else sender.sendMessage("このサーバーのIPは" + ip + "です");

                            break;
                        }
                        case "port": {
                            final int port = TestPlugin.get().getServer().getPort();
                            sender.sendMessage("このサーバーのポートは" + port + "です");
                            break;
                        }
                        case "current_tick": {
                            final int tick = TestPlugin.get().getServer().getCurrentTick();
                            sender.sendMessage("現在のtickは" + tick + "です");
                            break;
                        }
                        case "max_players": {
                            final int maxPlayers = TestPlugin.get().getServer().getMaxPlayers();
                            sender.sendMessage("このサーバーのプレイヤーの最大人数は" + maxPlayers + "です");
                            break;
                        }
                        case "log_archive_size": {
                            final Optional<Long> size = Arrays.stream(TextFileUtils.getAll("logs"))
                            .filter(path -> path.endsWith(".log.gz"))
                            .map(TextFileUtils::getSize)
                            .reduce(Long::sum);

                            if (size.isEmpty()) {
                                sender.sendMessage(Component.text("予期しないエラーが発生しました").color(TextColor.color(252, 64, 72)));
                                return false;
                            }

                            sender.sendMessage("ログのアーカイブの合計ファイルサイズは" + (float) size.get() / 1024.0f + "KBです");

                            break;
                        }
                        case "bukkit_version": {
                            final String version = TestPlugin.get().getServer().getBukkitVersion();
                            sender.sendMessage("このサーバーのBukkitのバージョンは" + version + "です");
                            break;
                        }
                        case "last_enabled": {
                            final String value = TestPlugin.database().getString("info.last_enabled");

                            if (value == null) {
                                sender.sendMessage("null");
                                return false;
                            }

                            sender.sendMessage(value);

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

                final ItemStack serverSelector = BungeeCordUtils.getServerSelector();

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
                final ItemStackBuilder itemStackBuilder = new ItemStackBuilder();

                if (args.length == 2) {
                    switch (args[1]) {
                        case "grappling_hook": {
                            itemStackBuilder
                            .type(Material.FISHING_ROD)
                            .customName("Grappling Hook", Color.ORANGE)
                            .dataContainer("custom_item_tag", "grappling_hook");
                            break;
                        }
                        case "instant_shoot_bow": {
                            itemStackBuilder
                            .type(Material.BOW)
                            .customName("Instant Shoot Bow", Color.ORANGE)
                            .dataContainer("custom_item_tag", "instant_shoot_bow");
                            break;
                        }
                        case "foo": {
                            itemStackBuilder
                            .type(Material.APPLE)
                            .dataContainer("a.b.c.d", "value");
                            break;
                        }
                        case "bar": {
                            itemStackBuilder
                            .type(Material.APPLE)
                            .dataContainer("a.b.c.d", new DataContainerAccessor[]{
                                DataContainerAccessor.create().set("e", "hoge"),
                                DataContainerAccessor.create().set("f", 0),
                                DataContainerAccessor.create().set("g", new DataContainerAccessor[]{
                                    DataContainerAccessor.create().set("h", true),
                                    DataContainerAccessor.create().set("i", 1L)
                                })
                            });
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
            case "set_data_container": {
                if (!(sender instanceof Player)) return false;

                final Player player = (Player) sender;

                if (args.length == 3) {
                    new EntityDataContainerManager(player).set(args[1], args[2]);
                    player.sendMessage(player.getName() + "の" + args[1] + "を" + args[2] + "に設定しました");
                    return true;
                }
                else {
                    player.sendMessage(Component.text("引数の数が不正です").color(TextColor.color(252, 64, 72)));
                    return false;
                }
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
            if (sender.isOp()) return List.of("get_info", "get_server_selector", "get_experimental_item", "set_data_container");
            else return List.of("get_server_selector");
        }
        else if (args.length == 2) {
            if (args[0].equals("get_info")) {
                return List.of("plugin_name", "api_version", "ip", "port", "current_tick", "max_players", "log_archive_size", "bukkit_version", "last_enabled");
            }
            else if (args[0].equals("get_experimental_item")) {
                return List.of("grappling_hook", "instant_shoot_bow");
            }
        }

        return List.of();
    }
}
