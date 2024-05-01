package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
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
                            final Optional<Long> size = TextFileUtils.getAll("logs")
                            .filter(path -> path.endsWith(".log.gz"))
                            .map(TextFileUtils::getSize)
                            .reduce(Long::sum);

                            if (size.isEmpty()) throw new RuntimeException();

                            sender.sendMessage("ログのアーカイブの合計ファイルサイズは" + (float) size.get() / 1024.0f + "KBです");

                            break;
                        }
                        case "bukkit_version": {
                            final String version = TestPlugin.get().getServer().getBukkitVersion();
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

                final ItemStack serverSelector = TestPlugin.getServerSelector();

                if (player.getInventory().contains(serverSelector)) {
                    player.sendMessage(Component.text("あなたは既に所持しています").color(TextColor.color(252, 64, 72)));
                    return false;
                }

                player.getInventory().addItem(serverSelector);

                return true;
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
            if (sender.isOp()) return List.of("get_info", "get_server_selector");
            else return List.of("get_server_selector");
        }
        else if (args.length == 2) {
            if (args[0].equals("get_info")) {
                return List.of("plugin_name", "api_version", "ip", "port", "current_tick", "max_players", "log_archive_size", "bukkit_version");
            }
        }

        return List.of();
    }
}
