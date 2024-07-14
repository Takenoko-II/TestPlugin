package com.gmail.subnokoii78.util.itemstack;

import com.gmail.subnokoii78.util.datacontainer.ItemStackDataContainerManager;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.format.*;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.*;
import org.bukkit.potion.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ItemStackBuilder {
    private ItemStack itemStack;

    private void edit(UnaryOperator<ItemMeta> builder) {
        final ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) return;

        itemStack.setItemMeta(builder.apply(itemMeta));
    }

    private TextComponent createUnItalicText(String text) {
        return Component.text(text).decoration(TextDecoration.ITALIC, false);
    }

    public ItemStackBuilder() {
        itemStack = new ItemStack(Material.AIR);
    }

    public ItemStackBuilder(Material material) {
        itemStack = new ItemStack(material);
    }

    public ItemStackBuilder(String id) {
        final Material material = Material.getMaterial(id);

        itemStack = (material == null) ? new ItemStack(Material.AIR) : new ItemStack(material);
    }

    public ItemStackBuilder type(Material material) {
        itemStack = itemStack.withType(material);

        return this;
    }

    public ItemStackBuilder type(String id) {
        final Material material = Material.getMaterial(id);

        if (material == null) return this;

        itemStack = itemStack.withType(material);

        return this;
    }

    public ItemStackBuilder count(int count) {
        itemStack.setAmount(count);

        return this;
    }

    public ItemStackBuilder maxCount(int count) {
        edit(meta -> {
            meta.setMaxStackSize(count);
            return meta;
        });

        return this;
    }

    public ItemStackBuilder itemName(String text, TextDecoration decoration, TextColor color) {
        edit(meta -> {
            meta.itemName(Component.text(text).decorate(decoration).color(color));
            return meta;
        });

        return this;
    }

    public ItemStackBuilder customName(String text) {
        edit(meta -> {
            final TextComponent component = createUnItalicText(text);
            meta.displayName(component);
            return meta;
        });

        return this;
    }

    public ItemStackBuilder customName(String text, TextDecoration decoration) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder customName(String text, TextColor color) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text).color(color);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder customName(String text, TextDecoration decoration, TextColor color) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration).color(color);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text);

            final List<TextComponent> list = new ArrayList<>();
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, TextDecoration decoration) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration);

            final List<Component> list = Objects.requireNonNullElse(builder.lore(), new ArrayList<>());
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, Color color) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text).color(TextColor.color(color.asRGB()));

            final List<Component> list = Objects.requireNonNullElse(builder.lore(), new ArrayList<>());
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, TextDecoration decoration, Color color) {
        edit(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration).color(TextColor.color(color.asRGB()));

            final List<Component> list = Objects.requireNonNullElse(builder.lore(), new ArrayList<>());
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        edit(builder -> {
            builder.addEnchant(enchantment, level, false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder hideFlag(ItemFlag itemFlag) {
        edit(builder -> {
            builder.addItemFlags(itemFlag);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder damage(int damage) {
        edit(builder -> {
            if (!(builder instanceof Damageable)) {
                return builder;
            }

            ((Damageable) builder).setDamage(damage);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder unBreakable(boolean flag) {
        edit(builder -> {
            builder.setUnbreakable(flag);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder repairCost(int cost) {
        edit(builder -> {
            if (!(builder instanceof Repairable)) {
                return builder;
            }

            ((Repairable) builder).setRepairCost(cost);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder playerHead(Player player) {
        edit(builder -> {
            if (!(builder instanceof SkullMeta)) {
                return builder;
            }

            ((SkullMeta) builder).setOwningPlayer((OfflinePlayer) player);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, EquipmentSlot slot, double amount) {
        edit(builder -> {
            builder.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, AttributeModifier.Operation.ADD_NUMBER, slot));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, EquipmentSlot slot, double amount, AttributeModifier.Operation operation) {
        edit(builder -> {
            builder.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, operation, slot));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, EquipmentSlot slot, double amount, AttributeModifier.Operation operation, UUID uuid) {
        edit(builder -> {
            builder.addAttributeModifier(attribute, new AttributeModifier(uuid, attribute.name(), amount, operation, slot));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder customModelData(int value) {
        edit(builder -> {
            builder.setCustomModelData(value);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionType(PotionType type) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).setBasePotionType(type);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, 20 * 30, 0), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, 0), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean showParticles) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, showParticles), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionColor(Color color) {
        edit(builder -> {
            if (!(builder instanceof PotionMeta)) {
                return builder;
            }

            ((PotionMeta) builder).setColor(color);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder chargedProjectile(ItemStack itemStack) {
        edit(builder -> {
            if (!(builder instanceof CrossbowMeta)) {
                return builder;
            }

            ((CrossbowMeta) builder).addChargedProjectile(itemStack);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder leatherArmorColor(Color color) {
        edit(builder -> {
            if (!(builder instanceof LeatherArmorMeta)) {
                return builder;
            }

            ((LeatherArmorMeta) builder).setColor(color);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lodeStoneTracked(boolean flag) {
        edit(builder -> {
            if (!(builder instanceof CompassMeta)) {
                return builder;
            }

            ((CompassMeta) builder).setLodestoneTracked(true);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lodeStoneLocation(Location location) {
        edit(builder -> {
            if (!(builder instanceof CompassMeta)) {
                return builder;
            }

            ((CompassMeta) builder).setLodestoneTracked(true);
            ((CompassMeta) builder).setLodestone(location);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder armorTrim(TrimMaterial material, TrimPattern pattern) {
        edit(builder -> {
            if (!(builder instanceof ColorableArmorMeta)) {
                return builder;
            }

            ((ColorableArmorMeta) builder).setTrim(new ArmorTrim(material, pattern));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookAuthor(String name) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).setAuthor(name);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookTitle(String name) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).setTitle(name);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookGeneration(BookMeta.Generation generation) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).setGeneration(generation);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookPage(Component component) {
        edit(builder -> {
            if (!(builder instanceof BookMeta)) {
                return builder;
            }

            ((BookMeta) builder).addPages(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder fireworkPower(int power) {
        edit(builder -> {
            if (!(builder instanceof FireworkMeta)) {
                return builder;
            }

            ((FireworkMeta) builder).setPower(power);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder fireworkEffect(FireworkEffect effect) {
        edit(builder -> {
            if (!(builder instanceof FireworkMeta)) {
                return builder;
            }

            ((FireworkMeta) builder).addEffect(effect);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder storedEnchantment(Enchantment enchantment, int level) {
        edit(builder -> {
            if (!(builder instanceof EnchantmentStorageMeta)) {
                return builder;
            }

            ((EnchantmentStorageMeta) builder).addStoredEnchant(enchantment, level, false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder glint(boolean flag) {
        edit(meta -> {
            meta.setEnchantmentGlintOverride(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder hideTooltip(boolean flag) {
        edit(meta -> {
            meta.setHideTooltip(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder fireResistant(boolean flag) {
        edit(meta -> {
            meta.setFireResistant(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder dataContainer(String path, Object value) {
        new ItemStackDataContainerManager(itemStack).set(path, value);

        return this;
    }

    public ItemStack build() {
        return itemStack.clone();
    }

    public static ItemStackBuilder from(ItemStack itemStack) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack.getType());

        itemStackBuilder.edit(meta -> itemStack.getItemMeta());

        return itemStackBuilder;
    }
}
