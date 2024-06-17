package com.gmail.subnokoii.testplugin.commands;

import com.gmail.subnokoii.testplugin.lib.itemstack.components.ComponentItemStackBuilder;
import com.gmail.subnokoii.testplugin.lib.itemstack.components.PotionContentsComponent;
import com.gmail.subnokoii.testplugin.lib.ui.ChestUIBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
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

        ChestUIBuilder tools = new ChestUIBuilder("Plugin Tools", 1)
        .add(button -> {
            return button.type(Material.ENDER_EYE)
            .customName("Quick Teleporter", NamedTextColor.LIGHT_PURPLE)
            .maxCount(1)
            .dataContainer("custom_item_tag", "quick_teleporter")
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(event.getClickedItemStack());
            });
        })
        .add(button -> {
            return button.type(Material.LINGERING_POTION)
            .customName("Data Getter", NamedTextColor.GREEN)
            .potionColor(Color.fromRGB(0x2FFF90))
            .dataContainer("custom_item_tag", "data_getter")
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(event.getClickedItemStack());
            });
        })
        .add(button -> {
            return button.type(Material.CLOCK)
            .maxCount(1)
            .customName("Tick Progress Canceler", NamedTextColor.GOLD)
            .glint(true)
            .dataContainer("custom_item_tag", "tick_progress_canceler")
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(event.getClickedItemStack());
            });
        })
        .add(button -> {
            final ComponentItemStackBuilder potion = new ComponentItemStackBuilder(Material.SPLASH_POTION);

            final PotionContentsComponent.PotionContent damage = new PotionContentsComponent.PotionContent(PotionEffectType.INSTANT_DAMAGE)
            .setAmplifier(29).setDuration(1).setShowParticles(false);

            final PotionContentsComponent.PotionContent health = new PotionContentsComponent.PotionContent(PotionEffectType.INSTANT_HEALTH)
            .setAmplifier(29).setDuration(1).setShowParticles(false);

            potion.potionContents().addContent(damage);
            potion.potionContents().addContent(health);
            potion.potionContents().setColor(Color.BLACK);
            potion.potionContents().setShowInTooltip(false);
            potion.itemName().setItemName(Component.text("断命のスプラッシュポーション"));
            potion.lore().addLore(Component.text("即死").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));
            potion.enchantmentGlintOverride().setGlintOverride(true);

            return button.from(potion.toItemStackBuilder())
            .onClick(event -> event.getPlayer().getInventory().addItem(event.getClickedItemStack()));
        })
        .add(button -> {
            return button.type(Material.GOLDEN_SWORD)
            .customName("Sword of Overwrite", NamedTextColor.RED)
            .glint(true)
            .dataContainer("custom_item_tag", "sword_of_overwrite")
            .lore("")
            .lore("利き手に持ったとき：", Color.fromRGB(NamedTextColor.GRAY.value()))
            .lore(" - 攻撃力", Color.fromRGB(NamedTextColor.DARK_GREEN.value()))
            .lore(" 1.6 攻撃速度", Color.fromRGB(NamedTextColor.DARK_GREEN.value()))
            .attributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, EquipmentSlot.HAND, 1, AttributeModifier.Operation.ADD_NUMBER)
            .attributeModifier(Attribute.GENERIC_ATTACK_SPEED, EquipmentSlot.HAND, -2.5, AttributeModifier.Operation.ADD_NUMBER)
            .hideFlag(ItemFlag.HIDE_ATTRIBUTES)
            .hideFlag(ItemFlag.HIDE_UNBREAKABLE)
            .unbreakable()
            .onClick(event -> {
                event.getPlayer().getInventory().addItem(event.getClickedItemStack());
            });
        });

        tools.open((Player) sender);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return List.of();
    }
}
