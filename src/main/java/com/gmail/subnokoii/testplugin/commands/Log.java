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

public class Log implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
            return false;
        }

        if (args.length < 2) {
            commandSender.sendMessage(Component.text("引数が不足しています").color(TextColor.color(252, 64, 72)));
            return false;
        }

        switch (args[1]) {
            case "read":
                List<String> texts = null;

                switch (args[0]) {
                    case "server":
                        texts = TextFileUtils.read("logs/latest.log");
                        break;
                    case "plugin":
                        texts = TextFileUtils.read("plugins/TestPlugin-1.0-SNAPSHOT.log");
                        break;
                }

                if (texts == null) {
                    commandSender.sendMessage(Component.text("参照先が無効です").color(TextColor.color(252, 64, 72)));
                }
                else if (texts.isEmpty()) {
                    commandSender.sendMessage("ログは空です");
                }
                else if (args.length == 2) {
                    commandSender.sendMessage("ログは以下の内容を持っています: \n" + String.join("\n", texts));
                }
                else {
                    try {
                        final int line = Integer.parseInt(args[2]);
                        final String text = texts.get(line - 1);
                        commandSender.sendMessage("ログ(" + line + "行目)は以下の内容を持っています: \n" + text);
                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                        commandSender.sendMessage(args[2] + "行目は存在しません");
                        return false;
                    }
                }

                break;
            case "write":
                if (args.length == 2) {
                    commandSender.sendMessage(Component.text("文字列を入力してください").color(TextColor.color(252, 64, 72)));
                    return false;
                }

                final String text = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                if (args[0].equals("server")) {
                    TestPlugin.log(TestPlugin.LoggingTarget.SERVER, text);
                }
                else if (args[0].equals("plugin")) {
                    TestPlugin.log(TestPlugin.LoggingTarget.PLUGIN, text);
                }
                else {
                    commandSender.sendMessage(Component.text("送信先が無効です").color(TextColor.color(252, 64, 72)));
                    return false;
                }

                commandSender.sendMessage("ログに書き込みました: " + text);

                break;
            case "clear":
                if (args[0].equals("server")) {
                    if (args.length == 2) {
                        commandSender.sendMessage(Component.text("引数が不足しています").color(TextColor.color(252, 64, 72)));
                        return false;
                    }

                    if (args[2].equals("archive")) {
                        Arrays.stream(TextFileUtils.getAll("logs"))
                        .filter(path -> path.endsWith(".log.gz"))
                        .forEach(TextFileUtils::delete);
                    }
                    else if (args[2].equals("latest")) {
                        TextFileUtils.erase("logs/latest.log");
                    }
                    else {
                        commandSender.sendMessage(Component.text("無効な引数です").color(TextColor.color(252, 64, 72)));
                        return false;
                    }
                }
                else if (args[0].equals("plugin")) {
                    TextFileUtils.erase("plugins/TestPlugin-1.0-SNAPSHOT.log");
                }
                else {
                    commandSender.sendMessage(Component.text("送信先が無効です").color(TextColor.color(252, 64, 72)));
                    return false;
                }

                commandSender.sendMessage("ログの内容を削除しました");

                break;
            default:
                commandSender.sendMessage(Component.text("無効な引数です").color(TextColor.color(252, 64, 72)));
                return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length <= 1) {
            return Arrays.asList("server", "plugin");
        }
        else if (args.length == 2) {
            return Arrays.asList("read", "write", "clear");
        }
        else if (args.length == 3) {
            if (args[1].equals("read")) {
                if (args[0].equals("plugin")) {
                    return List.of(String.valueOf(TextFileUtils.read("plugins/TestPlugin-1.0-SNAPSHOT.log").size()));
                }
                else if (args[0].equals("server")) {
                    return List.of(String.valueOf(TextFileUtils.read("logs/latest.log").size()));
                }
            }
            else if (args[0].equals("server") && args[1].equals("clear")) {
                return Arrays.asList("latest", "archive");
            }
        }

        return List.of();
    }
}
