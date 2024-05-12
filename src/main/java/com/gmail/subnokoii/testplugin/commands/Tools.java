package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.lib.ui.ChestUIBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Tools implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
            return false;
        }

        ChestUIBuilder tools = new ChestUIBuilder("Plugin Tools", 1)
        .add(button -> {
            return button.type(Material.ENDER_EYE)
            .name("Quick Teleporter", Color.fromRGB(0xA200FF))
            .dataContainer("custom_item_tag", "quick_teleporter")
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(button.getItemStack().clone());
            });
        })
        .add(button -> {
            return button.type(Material.LINGERING_POTION)
            .name("Data Getter", Color.fromRGB(0x2FFF90))
            .potionColor(Color.fromRGB(0x2FFF90))
            .dataContainer("custom_item_tag", "data_getter")
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(button.getItemStack().clone());
            });
            // これクリックしたとき保存されているものがtick progress cancelerになってる
        })
        .add(button -> {
            return button.type(Material.CLOCK)
            .name("Tick Progress Canceler", Color.fromRGB(0xFFF928))
            .glint(true)
            .dataContainer("custom_item_tag", "tick_progress_canceler")
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(button.getItemStack().clone());
            });
        });

        tools.open((Player) sender);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return List.of();
    }
}
