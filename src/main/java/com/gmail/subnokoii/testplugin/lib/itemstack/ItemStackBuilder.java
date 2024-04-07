package com.gmail.subnokoii.testplugin.lib.itemstack;

import com.gmail.subnokoii.testplugin.lib.other.NBTEditor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.UnaryOperator;

public class ItemStackBuilder {
    private final ItemStack itemStack;

    private void modifyItemMeta(UnaryOperator<ItemMeta> builder) {
        final ItemMeta itemMeta = builder.apply(itemStack.getItemMeta());
        itemStack.setItemMeta(itemMeta);
    }

    private TextComponent createUnItalicText(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }

    public ItemStackBuilder(Material material) {
        itemStack = new ItemStack(material);
    }

    public ItemStackBuilder count(int count) {
        itemStack.setAmount(count);

        return this;
    }

    public ItemStackBuilder name(String text) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder name(String text, TextDecoration decoration) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder name(String text, Color color) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).color(TextColor.color(color.asRGB()));
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder name(String text, TextDecoration decoration, Color color) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration).color(TextColor.color(color.asRGB()));
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, TextDecoration decoration) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, TextDecoration decoration, Color color) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration).color(TextColor.color(color.asRGB()));
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        modifyItemMeta(builder -> {
            builder.addEnchant(enchantment, level, true);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder hideFlag(ItemFlag itemFlag) {
        modifyItemMeta(builder -> {
            builder.addItemFlags(itemFlag);
            return builder;
        });

        return this;
    }
}
