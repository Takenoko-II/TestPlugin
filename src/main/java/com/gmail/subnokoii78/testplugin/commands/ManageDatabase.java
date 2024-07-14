package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.util.datacontainer.DataContainerManager;
import com.gmail.subnokoii78.testplugin.util.datacontainer.InvalidContainerKeyException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ManageDatabase implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "get":
                    if (args.length == 1) {
                        sender.sendMessage(
                            Component.text("TestPlugin Databaseは以下のデータを持っています:\n", NamedTextColor.WHITE)
                            .append(TestPlugin.database().toJson())
                        );

                        return true;
                    }
                    else if (args.length == 2) {
                        Object value;

                        try {
                            value = TestPlugin.database().getObject(args[1]);
                        }
                        catch (InvalidContainerKeyException e) {
                            sender.sendMessage(Component.text("キーの形式が無効です").color(NamedTextColor.RED));
                            return false;
                        }

                        if (value == null) {
                            sender.sendMessage(Component.text("そのパスには有効なデータが存在しません").color(NamedTextColor.RED));
                            return false;
                        }

                        sender.sendMessage(
                        Component.text("TestPlugin Databaseは以下のデータを持っています:\n", NamedTextColor.WHITE)
                        .append(DataContainerManager.stringify(value))
                        );

                        return true;
                    }
                    else {
                        sender.sendMessage(Component.text("引数の数が無効です").color(NamedTextColor.RED));
                        return false;
                    }
                case "set":
                    if (args.length == 3) {
                        sender.sendMessage(Component.text("まだ作れてないんだっ...まってくれっ...").color(NamedTextColor.RED));
                        return true;
                    }
                    else {
                        sender.sendMessage(Component.text("引数の数が無効です").color(NamedTextColor.RED));
                        return false;
                    }
            }
        }

        sender.sendMessage(Component.text("引数が無効です").color(NamedTextColor.RED));
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("get", "set");
        }
        else if (args.length == 2) {
            if (args[1].equals("get")) {
                return List.of("all");
            }
        }

        return List.of();
    }
}
