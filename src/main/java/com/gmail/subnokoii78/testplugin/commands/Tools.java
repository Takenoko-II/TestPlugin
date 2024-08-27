package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.util.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.util.itemstack.PotionContent;
import com.gmail.subnokoii78.util.itemstack.TypedAttributeModifier;
import com.gmail.subnokoii78.util.itemstack.components.ComponentItemStackBuilder;
import com.gmail.subnokoii78.util.ui.ContainerUI;
import com.gmail.subnokoii78.util.ui.ItemButton;
import com.gmail.subnokoii78.util.ui.PotionButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Tools implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("権限がありません").color(TextColor.color(252, 64, 72)));
            return false;
        }

        if (!(sender instanceof Player player)) return false;

        final var ui = new ContainerUI(Component.text("Plugin Tools"), 1)
            .add(
                new ItemButton(Material.ENDER_EYE)
                    .name(Component.text("Quick Teleporter").color(NamedTextColor.LIGHT_PURPLE))
                    .onClick(event -> {
                        final var itemStack = new ItemStackBuilder(Material.ENDER_EYE)
                            .customName("Quick Teleporter", NamedTextColor.LIGHT_PURPLE)
                            .maxCount(1)
                            .dataContainer("custom_item_tag", "quick_teleporter");
                        event.getPlayer().getInventory().addItem(itemStack.build());
                    })
            )
            .add(
                PotionButton.lingeringPotion()
                    .name(Component.text("Data Getter").color(NamedTextColor.GREEN))
                    .color(Color.fromRGB(0x2FFF90))
                    .onClick(event -> {
                        final var itemStack = new ItemStackBuilder(Material.LINGERING_POTION)
                            .customName("Data Getter", NamedTextColor.GREEN)
                            .potionColor(Color.fromRGB(0x2FFF90))
                            .dataContainer("custom_item_tag", "data_getter");

                        event.getPlayer().getInventory().addItem(itemStack.build());
                    })
            )
            .add(
                new ItemButton(Material.CLOCK)
                    .name(Component.text("Tick Progress Canceler").color(NamedTextColor.GOLD))
                    .glint(true)
                    .onClick(event -> {
                        final var itemStack = new ItemStackBuilder(Material.CLOCK)
                            .maxCount(1)
                            .customName("Tick Progress Canceler", NamedTextColor.GOLD)
                            .glint(true)
                            .dataContainer("custom_item_tag", "tick_progress_canceler");

                        event.getPlayer().getInventory().addItem(itemStack.build());
                    })
            )
            .add(
                PotionButton.splashPotion()
                    .name(Component.text("断命のスプラッシュポーション"))
                    .addLore(Component.text("即死").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED))
                    .glint(true)
                    .onClick(event -> {
                        final ComponentItemStackBuilder potion = new ComponentItemStackBuilder(Material.SPLASH_POTION);

                        final PotionContent damage = new PotionContent(PotionEffectType.INSTANT_DAMAGE)
                            .amplifier(29).duration(1).showParticles(false);

                        final PotionContent health = new PotionContent(PotionEffectType.INSTANT_HEALTH)
                            .amplifier(29).duration(1).showParticles(false);

                        potion.potionContents().addContent(damage);
                        potion.potionContents().addContent(health);
                        potion.potionContents().setColor(Color.BLACK);
                        potion.potionContents().setShowInTooltip(false);
                        potion.itemName().setItemName(Component.text("断命のスプラッシュポーション"));
                        potion.lore().addLore(Component.text("即死").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));
                        potion.enchantmentGlintOverride().setGlintOverride(true);

                        event.getPlayer().getInventory().addItem(potion.toItemStackBuilder().build());
                    })
            )
            .add(
                new ItemButton(Material.GOLDEN_SWORD)
                    .name(Component.text("Sword of Overwrite").color(NamedTextColor.GOLD))
                    .addLore(Component.text(""))
                    .addLore(Component.text("利き手に持ったとき：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY))
                    .addLore(Component.text(" - 攻撃力").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN))
                    .addLore(Component.text(" 1.6 攻撃速度").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN))
                    .onClick(event -> {
                        final ComponentItemStackBuilder sword = new ComponentItemStackBuilder(Material.GOLDEN_SWORD);
                        final TypedAttributeModifier attackDamage = new TypedAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE).amount(0.1d);
                        final TypedAttributeModifier attackSpeed = new TypedAttributeModifier(Attribute.GENERIC_ATTACK_SPEED).amount(-2.5d);

                        sword.attributeModifiers().setModifiers(new TypedAttributeModifier[]{attackDamage, attackSpeed});
                        sword.attributeModifiers().setShowInTooltip(false);
                        sword.unbreakable().setUnbreakable(true);
                        sword.unbreakable().setShowInTooltip(false);
                        sword.itemName().setItemName(Component.text("Sword of Overwrite").color(NamedTextColor.GOLD));
                        sword.lore().addLore(Component.text(""));
                        sword.lore().addLore(Component.text("利き手に持ったとき：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
                        sword.lore().addLore(Component.text(" - 攻撃力").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN));
                        sword.lore().addLore(Component.text(" 1.6 攻撃速度").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN));

                        final ItemStackBuilder itemStackBuilder = sword.toItemStackBuilder();
                        itemStackBuilder.dataContainer("custom_item_tag", "sword_of_overwrite");

                        event.getPlayer().getInventory().addItem(itemStackBuilder.build());
                    })
            );

        ui.open(player);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return List.of();
    }
}
