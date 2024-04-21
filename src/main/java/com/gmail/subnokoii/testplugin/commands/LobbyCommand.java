package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.TestPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LobbyCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player)) return false;

            if (((Player) commandSender).getScoreboardTags().contains("plugin.permission.cannot_use_lobby")) {
                commandSender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
                return false;
            }

            TestPlugin.transfer((Player) commandSender, "lobby");
        }
        else {
            if (!commandSender.isOp()) {
                commandSender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
                return false;
            }

            final Player player = commandSender.getServer().getPlayer(args[0]);

            if (player == null) {
                commandSender.sendMessage(Component.text("存在しないプレイヤー名です").color(TextColor.color(252, 64, 72)));
                return false;
            }

            TestPlugin.transfer(player, "lobby");
        }

        commandSender.sendMessage("lobbyサーバーへの接続を試行中...");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1 && commandSender.isOp()) return null;
        else return List.of();
    }
}
