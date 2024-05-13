package com.gmail.subnokoii.testplugin.lib.itemstack;

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
import java.util.UUID;
import java.util.function.UnaryOperator;

public class ItemStackBuilder {
    private ItemStack itemStack;

    private void modifyItemMeta(UnaryOperator<ItemMeta> builder) {
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
        modifyItemMeta(meta -> {
            meta.setMaxStackSize(count);
            return meta;
        });

        return this;
    }

    public ItemStackBuilder customName(String text) {
        modifyItemMeta(meta -> {
            final TextComponent component = createUnItalicText(text);
            meta.displayName(component);
            return meta;
        });

        return this;
    }

    public ItemStackBuilder customName(String text, TextDecoration decoration) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration);
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder customName(String text, Color color) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).color(TextColor.color(color.asRGB()));
            builder.displayName(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder customName(String text, TextDecoration decoration, Color color) {
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

            final List<TextComponent> list = new ArrayList<>();
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, TextDecoration decoration) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration);

            final List<TextComponent> list = new ArrayList<>();
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, Color color) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).color(TextColor.color(color.asRGB()));

            final List<TextComponent> list = new ArrayList<>();
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lore(String text, TextDecoration decoration, Color color) {
        modifyItemMeta(builder -> {
            final TextComponent component = createUnItalicText(text).decorate(decoration).color(TextColor.color(color.asRGB()));

            final List<TextComponent> list = new ArrayList<>();
            list.add(component);
            builder.lore(list);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder enchantment(Enchantment enchantment, int level) {
        modifyItemMeta(builder -> {
            builder.addEnchant(enchantment, level, false);
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

    public ItemStackBuilder damage(int damage) {
        modifyItemMeta(builder -> {
            ((Damageable) builder).setDamage(damage);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder unBreakable(boolean flag) {
        modifyItemMeta(builder -> {
            ((Damageable) builder).setUnbreakable(flag);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder repairCost(int cost) {
        modifyItemMeta(builder -> {
            ((Repairable) builder).setRepairCost(cost);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder playerHead(Player player) {
        modifyItemMeta(builder -> {
            ((SkullMeta) builder).setOwningPlayer((OfflinePlayer) player);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, EquipmentSlot slot, double amount) {
        modifyItemMeta(builder -> {
            builder.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, AttributeModifier.Operation.ADD_NUMBER, slot));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, EquipmentSlot slot, double amount, AttributeModifier.Operation operation) {
        modifyItemMeta(builder -> {
            builder.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, operation, slot));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder attributeModifier(Attribute attribute, EquipmentSlot slot, double amount, AttributeModifier.Operation operation, UUID uuid) {
        modifyItemMeta(builder -> {
            builder.addAttributeModifier(attribute, new AttributeModifier(uuid, attribute.name(), amount, operation, slot));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder customModelData(int value) {
        modifyItemMeta(builder -> {
            builder.setCustomModelData(value);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionType(PotionType type) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).setBasePotionType(type);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, 20 * 30, 0), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, 0), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean showParticles) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, showParticles), false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder potionColor(Color color) {
        modifyItemMeta(builder -> {
            ((PotionMeta) builder).setColor(color);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder chargedProjectile(ItemStack itemStack) {
        modifyItemMeta(builder -> {
            ((CrossbowMeta) builder).addChargedProjectile(itemStack);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder leatherArmorColor(Color color) {
        modifyItemMeta(builder -> {
            ((LeatherArmorMeta) builder).setColor(color);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder lodeStoneCompassLocation(Location location) {
        modifyItemMeta(builder -> {
            ((CompassMeta) builder).setLodestoneTracked(true);
            ((CompassMeta) builder).setLodestone(location);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder armorTrim(TrimMaterial material, TrimPattern pattern) {
        modifyItemMeta(builder -> {
            ((ColorableArmorMeta) builder).setTrim(new ArmorTrim(material, pattern));
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookAuthor(String name) {
        modifyItemMeta(builder -> {
            ((BookMeta) builder).setAuthor(name);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookTitle(String name) {
        modifyItemMeta(builder -> {
            ((BookMeta) builder).setTitle(name);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookGeneration(BookMeta.Generation generation) {
        modifyItemMeta(builder -> {
            ((BookMeta) builder).setGeneration(generation);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder bookPage(Component component) {
        modifyItemMeta(builder -> {
            ((BookMeta) builder).addPages(component);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder fireworkPower(int power) {
        modifyItemMeta(builder -> {
            ((FireworkMeta) builder).setPower(power);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder fireworkEffect(FireworkEffect effect) {
        modifyItemMeta(builder -> {
            ((FireworkMeta) builder).addEffect(effect);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder storedEnchantment(Enchantment enchantment, int level) {
        modifyItemMeta(builder -> {
            ((EnchantmentStorageMeta) builder).addStoredEnchant(enchantment, level, false);
            return builder;
        });

        return this;
    }

    public ItemStackBuilder glint(boolean flag) {
        modifyItemMeta(meta -> {
            meta.setEnchantmentGlintOverride(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder hideTooltip(boolean flag) {
        modifyItemMeta(meta -> {
            meta.setHideTooltip(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder fireResistant(boolean flag) {
        modifyItemMeta(meta -> {
            meta.setFireResistant(flag);

            return meta;
        });

        return this;
    }

    public ItemStackBuilder dataContainer(String path, Object value) {
        itemStack = new ItemStackDataContainerAccessor(itemStack).set(path, value);

        return this;
    }

    public ItemStack build() {
        return itemStack.clone();
    }

    public static ItemStackBuilder from(ItemStack itemStack) {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack.getType());

        itemStackBuilder.modifyItemMeta(meta -> itemStack.getItemMeta());

        return itemStackBuilder;
    }
}
