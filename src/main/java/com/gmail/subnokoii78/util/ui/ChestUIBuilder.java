package com.gmail.subnokoii78.util.ui;

import com.gmail.subnokoii78.util.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.util.datacontainer.ItemStackDataContainerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class ChestUIBuilder {
    private final Inventory inventory;

    private final Button[] buttons;

    public ChestUIBuilder(String name, int line) {
        final Component component = Component.text(name);

        inventory = Bukkit.createInventory(null, line * 9, component);
        buttons = new Button[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, TextDecoration decoration, int line) {
        final Component component = Component.text(name).decorate(decoration);

        inventory = Bukkit.createInventory(null, line * 9, component);
        buttons = new Button[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, TextColor color, int line) {
        final Component component = Component.text(name).color(color);

        inventory = Bukkit.createInventory(null, line * 9, component);
        buttons = new Button[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, TextDecoration decoration, TextColor color, int line) {
        final Component component = Component.text(name).decorate(decoration).color(color);

        inventory = Bukkit.createInventory(null, line * 9, component);
        buttons = new Button[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder set(int index, UnaryOperator<Button> modifier) {
        final Button button = modifier.apply(new Button());

        buttons[index] = button;
        inventory.setItem(index, button.itemStackBuilder.build());

        return this;
    }

    public ChestUIBuilder add(UnaryOperator<Button> builder) {
        final Button button = builder.apply(new Button());

        final int index = Arrays.asList(buttons).indexOf(null);

        if (index == -1) return this;

        buttons[index] = button;
        inventory.setItem(index, button.itemStackBuilder.build());

        return this;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Button[] getAllButtons() {
        return buttons;
    }

    private static final List<ChestUIBuilder> builders = new ArrayList<>();

    public static ChestUIBuilder[] getAll() {
        return builders.toArray(new ChestUIBuilder[0]);
    }

    public static final class Button {
        private ItemStackBuilder itemStackBuilder = new ItemStackBuilder(Material.APPLE);

        private Consumer<ChestUIClickEvent> listener = response -> {};

        public Button() {
            itemStackBuilder.dataContainer("id", UUID.randomUUID().toString());
        }

        public boolean match(ItemStack itemStack) {
            final String id = new ItemStackDataContainerManager(itemStack).getString("id");
            final String thisId = new ItemStackDataContainerManager(itemStackBuilder.build()).getString("id");

            if (id == null) return false;

            return id.equals(thisId);
        }

        public Button type(Material material) {
            itemStackBuilder.type(material);

            return this;
        }

        public Button type(String id) {
            final Material material = Material.getMaterial(id);

            if (material == null) {
                throw new IllegalArgumentException();
            }

            itemStackBuilder.type(material);

            return this;
        }

        public Button count(int count) {
            itemStackBuilder.count(count);

            return this;
        }

        public Button maxCount(int count) {
            itemStackBuilder.maxCount(count);

            return this;
        }

        public Button customName(String name) {
            itemStackBuilder.customName(name);

            return this;
        }

        public Button customName(String name, TextDecoration decoration) {
            itemStackBuilder.customName(name, decoration);

            return this;
        }

        public Button customName(String name, TextColor color) {
            itemStackBuilder.customName(name, color);

            return this;
        }

        public Button customName(String name, TextDecoration decoration, TextColor color) {
            itemStackBuilder.customName(name, decoration, color);

            return this;
        }

        public Button lore(String lore) {
            itemStackBuilder.lore(lore);

            return this;
        }

        public Button lore(String lore, TextDecoration decoration) {
            itemStackBuilder.lore(lore, decoration);

            return this;
        }

        public Button lore(String lore, Color color) {
            itemStackBuilder.lore(lore, color);

            return this;
        }

        public Button lore(String lore, TextDecoration decoration, Color color) {
            itemStackBuilder.lore(lore, decoration, color);

            return this;
        }

        public Button glint(boolean flag) {
            itemStackBuilder.glint(flag);

            return this;
        }

        public Button customModelData(int data) {
            itemStackBuilder.customModelData(data);

            return this;
        }

        public Button damage(int damage) {
            itemStackBuilder.damage(damage);

            return this;
        }

        public Button potion(PotionType type) {
            itemStackBuilder.potionType(type);

            return this;
        }

        public Button potionColor(Color color) {
            itemStackBuilder.potionColor(color);

            return this;
        }

        public Button playerHead(Player player) {
            itemStackBuilder.playerHead(player);

            return this;
        }

        public Button leatherArmorColor(Color color) {
            itemStackBuilder.leatherArmorColor(color);

            return this;
        }

        public Button crossbowArrow() {
            itemStackBuilder.chargedProjectile(new ItemStack(Material.ARROW));

            return this;
        }

        public Button hideFlag(ItemFlag flag) {
            itemStackBuilder.hideFlag(flag);

            return this;
        }

        public Button attributeModifier(Attribute attribute, EquipmentSlot slot, double amount, AttributeModifier.Operation operation) {
            itemStackBuilder.attributeModifier(attribute, slot, amount, operation, UUID.randomUUID());

            return this;
        }

        public Button unbreakable() {
            itemStackBuilder.unBreakable(true);

            return this;
        }

        public Button dataContainer(String path, Object value) {
            itemStackBuilder.dataContainer(path, value);

            return this;
        }

        public Button onClick(Consumer<ChestUIClickEvent> listener) {
            this.listener = listener;

            return this;
        }

        public Button from(ItemStackBuilder itemStackBuilder) {
            final String id = new ItemStackDataContainerManager(this.itemStackBuilder.build()).getString("id");

            this.itemStackBuilder = itemStackBuilder;

            if (id == null) itemStackBuilder.dataContainer("id", UUID.randomUUID().toString());
            else itemStackBuilder.dataContainer("id", id);

            return this;
        }

        public void click(Player clicker) {
            listener.accept(new ChestUIClickEvent(clicker, itemStackBuilder.build()));
        }
    }
}
