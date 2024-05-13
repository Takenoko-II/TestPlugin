package com.gmail.subnokoii.testplugin.lib.ui;

import com.gmail.subnokoii.testplugin.lib.itemstack.ItemDataContainerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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

    public ChestUIBuilder(String name, Color color, int line) {
        final Component component = Component.text(name).color(TextColor.color(color.asRGB()));

        inventory = Bukkit.createInventory(null, line * 9, component);
        buttons = new Button[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder(String name, TextDecoration decoration, Color color, int line) {
        final Component component = Component.text(name).decorate(decoration).color(TextColor.color(color.asRGB()));

        inventory = Bukkit.createInventory(null, line * 9, component);
        buttons = new Button[line * 9];

        builders.add(this);
    }

    public ChestUIBuilder set(int index, UnaryOperator<Button> modifier) {
        final Button button = modifier.apply(new Button());

        buttons[index] = button;
        inventory.setItem(index, button.getItemStack());

        return this;
    }

    public ChestUIBuilder add(UnaryOperator<Button> builder) {
        final Button button = builder.apply(new Button());

        final int index = Arrays.asList(buttons).indexOf(null);

        if (index == -1) return this;

        buttons[index] = button;
        inventory.setItem(index, button.getItemStack());

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
        private ItemStack itemStack;

        private Consumer<ChestUIClickEvent> listener = response -> {};

        public Button() {
            itemStack = new ItemDataContainerAccessor(new ItemStack(Material.IRON_BARS)).set("id", UUID.randomUUID().toString());
        }

        public boolean matchId(ItemStack itemStack) {
            final String id = new ItemDataContainerAccessor(itemStack).getString("id");
            final String thisId = new ItemDataContainerAccessor(getItemStack()).getString("id");

            if (id == null) return false;

            return id.equals(thisId);
        }

        public Button type(Material material) {
            itemStack.setType(material);

            return this;
        }

        public Button type(String id) {
            final Material material = Material.getMaterial(id);

            if (material == null) {
                throw new NullPointerException();
            }

            itemStack.setType(material);

            return this;
        }

        public Button count(int count) {
            itemStack.setAmount(count);

            return this;
        }

        public Button name(String name) {
            final ItemMeta meta = itemStack.getItemMeta();

            meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button name(String name, TextDecoration decoration) {
            final ItemMeta meta = itemStack.getItemMeta();

            final Component component = Component.text(name).decoration(TextDecoration.ITALIC, false);

            meta.displayName(component.decorate(decoration));
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button name(String name, Color color) {
            final ItemMeta meta = itemStack.getItemMeta();

            final Component component = Component.text(name).decoration(TextDecoration.ITALIC, false);

            meta.displayName(component.color(TextColor.color(color.asRGB())));
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button name(String name, TextDecoration decoration, Color color) {
            final ItemMeta meta = itemStack.getItemMeta();

            final Component component = Component.text(name).decoration(TextDecoration.ITALIC, false);

            meta.displayName(component.decorate(decoration).color(TextColor.color(color.asRGB())));
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button lore(String lore) {
            final ItemMeta meta = itemStack.getItemMeta();
            List<Component> list = meta.lore();
            if (list == null) list = new ArrayList<>();

            list.add(Component.text(lore).decoration(TextDecoration.ITALIC, false));
            meta.lore(list);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button lore(String lore, TextDecoration decoration) {
            final ItemMeta meta = itemStack.getItemMeta();

            final Component component = Component.text(lore).decoration(TextDecoration.ITALIC, false);

            List<Component> list = meta.lore();
            if (list == null) list = new ArrayList<>();

            list.add(component.decorate(decoration));
            meta.lore(list);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button lore(String lore, Color color) {
            final ItemMeta meta = itemStack.getItemMeta();

            final Component component = Component.text(lore).decoration(TextDecoration.ITALIC, false);

            List<Component> list = meta.lore();
            if (list == null) list = new ArrayList<>();

            list.add(component.color(TextColor.color(color.asRGB())));
            meta.lore(list);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button lore(String lore, TextDecoration decoration, Color color) {
            final ItemMeta meta = itemStack.getItemMeta();

            final Component component = Component.text(lore).decoration(TextDecoration.ITALIC, false);

            List<Component> list = meta.lore();
            if (list == null) list = new ArrayList<>();

            list.add(component.decorate(decoration).color(TextColor.color(color.asRGB())));
            meta.lore(list);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button glint(boolean flag) {
            final ItemMeta meta = itemStack.getItemMeta();

            meta.setEnchantmentGlintOverride(flag);

            itemStack.setItemMeta(meta);

            return this;
        }

        public Button customModelData(int data) {
            final ItemMeta meta = itemStack.getItemMeta();

            meta.setCustomModelData(data);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button damage(int damage) {
            final ItemMeta meta = itemStack.getItemMeta();

            ((Damageable) meta).setDamage(damage);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button potion(PotionType type) {
            final PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

            meta.setBasePotionType(type);
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button potionColor(Color color) {
            final PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

            meta.setColor(color);
            meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button playerHead(Player player) {
            final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

            meta.setOwningPlayer(player);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button leatherArmorColor(Color color) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

            meta.setColor(color);
            meta.addItemFlags(ItemFlag.HIDE_DYE);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button crossbowArrow(boolean flag) {
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

        public Button lodestoneCompass(boolean flag) {
            final CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
            meta.setLodestoneTracked(flag);
            itemStack.setItemMeta(meta);

            return this;
        }

        public Button modify(UnaryOperator<ItemMeta> operator) {
            final ItemMeta meta = itemStack.getItemMeta();
            itemStack.setItemMeta(operator.apply(meta));

            return this;
        }

        public Button dataContainer(String key, Object value) {
            itemStack = new ItemDataContainerAccessor(itemStack).set(key, value);

            return this;
        }

        public Button onClick(Consumer<ChestUIClickEvent> listener) {
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
}
