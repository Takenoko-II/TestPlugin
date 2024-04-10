package com.gmail.subnokoii.testplugin.lib.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ChestUIBuilder {
    private final Inventory inventory;

    private final ChestUIButtonBuilder[] itemStacks;

    public ChestUIBuilder(String name, int line) {
        final Component component = Component.text(name);

        inventory = Bukkit.createInventory(null, line * 9, component);
        itemStacks = new ChestUIButtonBuilder[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, TextDecoration decoration, int line) {
        final Component component = Component.text(name).decorate(decoration);

        inventory = Bukkit.createInventory(null, line * 9, component);
        itemStacks = new ChestUIButtonBuilder[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, Color color, int line) {
        final Component component = Component.text(name).color(TextColor.color(color.asRGB()));

        inventory = Bukkit.createInventory(null, line * 9, component);
        itemStacks = new ChestUIButtonBuilder[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, TextDecoration decoration, Color color, int line) {
        final Component component = Component.text(name).decorate(decoration).color(TextColor.color(color.asRGB()));

        inventory = Bukkit.createInventory(null, line * 9, component);
        itemStacks = new ChestUIButtonBuilder[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder set(int index, UnaryOperator<ChestUIButtonBuilder> builder) {
        final ChestUIButtonBuilder itemStackBuilder = new ChestUIButtonBuilder();

        final ChestUIButtonBuilder result = builder.apply(itemStackBuilder);

        itemStacks[index] = result;
        inventory.setItem(index, result.getItemStack());

        return this;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ChestUIButtonBuilder[] getItemStacks() {
        return itemStacks;
    }

    private static final List<ChestUIBuilder> builders = new ArrayList<>();

    public static List<ChestUIBuilder> getBuilders() {
        return builders;
    }
}
