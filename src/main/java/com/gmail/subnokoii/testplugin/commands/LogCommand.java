package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.TestPlugin;
import com.gmail.subnokoii.testplugin.lib.file.TextFileUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class LogCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
            return false;
        }

        if (args.length < 1) {
            commandSender.sendMessage(Component.text("引数が不足しています").color(TextColor.color(252, 64, 72)));
            return false;
        }

        if (args[0].equals("read")) {
            final List<String> texts = TextFileUtils.read("plugins/TestPlugin-1.0-SNAPSHOT.log");

            if (texts.isEmpty()) {
                commandSender.sendMessage("ログは空です");
            }
            else if (args.length < 2) {
                final String text = String.join("\n", texts);

                commandSender.sendMessage("ログは以下の内容を持っています: \n" + text);
            }
            else {
                try {
                    final int line = Integer.parseInt(args[1]);
                    final String text = texts.get(line - 1);
                    commandSender.sendMessage("ログ(" + line + "行目)は以下の内容を持っています: \n" + text);
                }
                catch (IndexOutOfBoundsException | NumberFormatException e) {
                    commandSender.sendMessage(args[1] + "行目は存在しません");
                    return false;
                }
            }
        }
        else if (args[0].equals("write")) {
            if (args.length < 2) {
                commandSender.sendMessage(Component.text("文字列を入力してください").color(TextColor.color(252, 64, 72)));
                return false;
            }

            final String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            TestPlugin.log(text);
            commandSender.sendMessage("ログに書き込みました: " + text);
        }
        else if (args[0].equals("clear")) {
            TextFileUtils.erase("plugins/TestPlugin-1.0-SNAPSHOT.log");
            commandSender.sendMessage("ログの内容を削除しました");
        }
        else {
            commandSender.sendMessage(Component.text("無効な引数です").color(TextColor.color(252, 64, 72)));
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("read", "write", "clear");
        }
        else if (args.length > 1) {
            if (args[0].equals("read")) {
                return List.of(String.valueOf(TextFileUtils.read("plugins/TestPlugin-1.0-SNAPSHOT.log").size()));
            }
        }

        return List.of();
    }
}
