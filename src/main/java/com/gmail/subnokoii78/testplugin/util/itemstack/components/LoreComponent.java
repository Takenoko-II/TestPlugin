package com.gmail.subnokoii78.testplugin.util.itemstack.components;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class LoreComponent implements ItemStackComponent {
    private final ItemMeta itemMeta;

    private LoreComponent(ItemMeta itemMeta) {
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

    public Component[] getLore() {
        final List<Component> list = Objects.requireNonNullElse(itemMeta.lore(), new ArrayList<>());

        return list.toArray(Component[]::new);
    }

    public void setLore(Component[] array) {
        itemMeta.lore(List.of(array));
    }

    public void addLore(int index, Component component) {
        final List<Component> components = new ArrayList<>(Arrays.asList(getLore()));
        components.add(index, component);

        itemMeta.lore(components);
    }

    public void addLore(Component component) {
        final List<Component> components = new ArrayList<>(Arrays.asList(getLore()));
        components.add(component);

        itemMeta.lore(components);
    }

    public void removeLore(int index) {
        final List<Component> components = Arrays.asList(getLore());
        components.remove(index);

        itemMeta.lore(components);
    }

    @Override
    public boolean getShowInTooltip() {
        return false;
    }

    @Override
    public void setShowInTooltip(boolean flag) {
        throw new InvalidComponentTypeException();
    }

    public static final String COMPONENT_ID = "minecraft:lore";
}
