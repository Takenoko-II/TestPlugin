package com.gmail.subnokoii78.util.ui;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class PlayerHeadButton extends ItemButton {
    public PlayerHeadButton() {
        super(Material.PLAYER_HEAD);
    }

    @Override
    public @NotNull PlayerHeadButton name(@NotNull TextComponent component) {
        return (PlayerHeadButton) super.name(component);
    }

    @Override
    public @NotNull PlayerHeadButton addLore(@NotNull TextComponent component) {
        return (PlayerHeadButton) super.addLore(component);
    }

    @Override
    public @NotNull PlayerHeadButton setLore(@NotNull List<TextComponent> components) {
        return (PlayerHeadButton) super.setLore(components);
    }

    @Override
    public @NotNull PlayerHeadButton amount(int amount) {
        return (PlayerHeadButton) super.amount(amount);
    }

    @Override
    public @NotNull PlayerHeadButton glint(boolean flag) {
        return (PlayerHeadButton) super.glint(flag);
    }

    @Override
    public @NotNull PlayerHeadButton customModelData(int data) {
        return (PlayerHeadButton) super.customModelData(data);
    }

    @Override
    public @NotNull PlayerHeadButton onClick(Consumer<ItemButtonClickEvent> listener) {
        return (PlayerHeadButton) super.onClick(listener);
    }

    public PlayerHeadButton player(@NotNull String name) {
        itemStackBuilder.profile().setOwner(Bukkit.getOfflinePlayer(name));
        return this;
    }
}
