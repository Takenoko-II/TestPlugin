package com.gmail.subnokoii.testplugin.lib.itemstack.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class LoreComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    public LoreComponent(ItemMeta itemMeta) {
        if (itemMeta == null) {
            throw new IllegalArgumentException();
        }

        this.itemMeta = itemMeta;
    }

    @Override
    public boolean getEnabled() {
        return itemMeta.hasLore();
    }

    @Override
    public void disable() {
        itemMeta.lore(null);
    }

    public TextComponent[] getLore() {
        final List<Component> list = Objects.requireNonNullElse(itemMeta.lore(), new ArrayList<>());

        return (TextComponent[]) list.toArray(Component[]::new);
    }

    public void setLore(TextComponent[] array) {
        itemMeta.lore(List.of(array));
    }

    public void addLore(int index, TextComponent component) {
        final List<TextComponent> components = Arrays.asList(getLore());
        components.add(index, component);

        itemMeta.lore(components);
    }

    public void removeLore(int index) {
        final List<TextComponent> components = Arrays.asList(getLore());
        components.remove(index);

        itemMeta.lore(components);
    }

    @Override
    public boolean getShowInTooltip(boolean flag) {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:lore";
}
