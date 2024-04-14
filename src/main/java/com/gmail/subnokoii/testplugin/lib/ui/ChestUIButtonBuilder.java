package com.gmail.subnokoii.testplugin.lib.ui;

import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ChestUIButtonBuilder {
    private final ItemStack itemStack;

    private Consumer<ChestUIClickEvent> listener = response -> {};

    public ChestUIButtonBuilder() {
        itemStack = new ItemStack(Material.AIR);
    }

    public ChestUIButtonBuilder type(Material material) {
        itemStack.setType(material);

        return this;
    }

    public ChestUIButtonBuilder type(String id) {
        final Material material = Material.getMaterial(id);

        if (material == null) {
            throw new NullPointerException();
        }

        itemStack.setType(material);

        return this;
    }

    public ChestUIButtonBuilder count(int count) {
        itemStack.setAmount(count);

        return this;
    }

    public ChestUIButtonBuilder name(String name) {
        final ItemMeta meta = itemStack.getItemMeta();

        meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder name(String name, TextDecoration decoration) {
        final ItemMeta meta = itemStack.getItemMeta();

        final Component component = Component.text(name).decoration(TextDecoration.ITALIC, false);

        meta.displayName(component.decorate(decoration));
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder name(String name, Color color) {
        final ItemMeta meta = itemStack.getItemMeta();

        final Component component = Component.text(name).decoration(TextDecoration.ITALIC, false);

        meta.displayName(component.color(TextColor.color(color.asRGB())));
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder name(String name, TextDecoration decoration, Color color) {
        final ItemMeta meta = itemStack.getItemMeta();

        final Component component = Component.text(name).decoration(TextDecoration.ITALIC, false);

        meta.displayName(component.decorate(decoration).color(TextColor.color(color.asRGB())));
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder lore(String lore) {
        final ItemMeta meta = itemStack.getItemMeta();
        List<Component> list = meta.lore();
        if (list == null) list = new ArrayList<>();

        list.add(Component.text(lore).decoration(TextDecoration.ITALIC, false));
        meta.lore(list);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder lore(String lore, TextDecoration decoration) {
        final ItemMeta meta = itemStack.getItemMeta();

        final Component component = Component.text(lore).decoration(TextDecoration.ITALIC, false);

        List<Component> list = meta.lore();
        if (list == null) list = new ArrayList<>();

        list.add(component.decorate(decoration));
        meta.lore(list);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder lore(String lore, Color color) {
        final ItemMeta meta = itemStack.getItemMeta();

        final Component component = Component.text(lore).decoration(TextDecoration.ITALIC, false);

        List<Component> list = meta.lore();
        if (list == null) list = new ArrayList<>();

        list.add(component.color(TextColor.color(color.asRGB())));
        meta.lore(list);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder lore(String lore, TextDecoration decoration, Color color) {
        final ItemMeta meta = itemStack.getItemMeta();

        final Component component = Component.text(lore).decoration(TextDecoration.ITALIC, false);

        List<Component> list = meta.lore();
        if (list == null) list = new ArrayList<>();

        list.add(component.decorate(decoration).color(TextColor.color(color.asRGB())));
        meta.lore(list);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder glint(boolean flag) {
        final ItemMeta meta = itemStack.getItemMeta();

        if (flag) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        else {
            meta.removeEnchantments();
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder customModelData(int data) {
        final ItemMeta meta = itemStack.getItemMeta();

        meta.setCustomModelData(data);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder damage(int damage) {
        final ItemMeta meta = itemStack.getItemMeta();

        ((Damageable) meta).setDamage(damage);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder potion(PotionType type) {
        final PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

        meta.setBasePotionType(type);
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder potionColor(Color color) {
        final PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

        meta.setColor(color);
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder playerHead(Player player) {
        final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

        meta.setOwningPlayer(player);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder leatherArmorColor(Color color) {
        final LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

        meta.setColor(color);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder crossbowArrow(boolean flag) {
        final CrossbowMeta meta = (CrossbowMeta) itemStack.getItemMeta();

        final List<ItemStack> arrows = new ArrayList<>();

        if (flag) {
            arrows.add(new ItemStack(Material.ARROW));
            meta.setChargedProjectiles(arrows);
        }
        else {
            meta.setChargedProjectiles(arrows);
        }

        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder lodestoneCompass(boolean flag) {
        final CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
        meta.setLodestoneTracked(flag);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ChestUIButtonBuilder modify(UnaryOperator<ItemMeta> operator) {
        final ItemMeta meta = itemStack.getItemMeta();
        itemStack.setItemMeta(operator.apply(meta));

        return this;
    }

    public ChestUIButtonBuilder onClick(Consumer<ChestUIClickEvent> listener) {
        this.listener = listener;

        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void click(Player clicker) {
        listener.accept(new ChestUIClickEvent(clicker, this));
    }
}
