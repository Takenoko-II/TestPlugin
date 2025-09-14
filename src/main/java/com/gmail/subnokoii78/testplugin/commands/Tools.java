package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButton;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffect;
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

        final var ui = new ContainerInteraction(Component.text("Plugin Tools"), 1)
            .add(
                ItemButton.item(Material.ENDER_EYE)
                    .name(Component.text("Quick Teleporter").color(NamedTextColor.LIGHT_PURPLE))
                    .onClick(event -> {
                        final var itemStack = new ItemStackBuilder(Material.ENDER_EYE)
                            .itemName(Component.text("Quick Teleporter").color(NamedTextColor.LIGHT_PURPLE))
                            .maxStackSize(1)
                            .customData(MojangsonPath.of("custom_item_tag"), "quick_teleporter");
                        event.getPlayer().getInventory().addItem(itemStack.build());
                    })
            )
            .add(
                ItemButton.lingeringPotion()
                    .name(Component.text("Data Getter").color(NamedTextColor.GREEN))
                    .color(Color.fromRGB(0x2FFF90))
                    .onClick(event -> {
                        final var itemStack = new ItemStackBuilder(Material.LINGERING_POTION)
                            .itemName(Component.text("Data Getter").color(NamedTextColor.GREEN))
                            .potionColor(Color.fromRGB(0x2FFF90))
                            .customData(MojangsonPath.of("custom_item_tag"), "data_getter");

                        event.getPlayer().getInventory().addItem(itemStack.build());
                    })
            )
            .add(
                ItemButton.item(Material.CLOCK)
                    .name(Component.text("Tick Progress Canceler").color(NamedTextColor.GOLD))
                    .glint(true)
                    .onClick(event -> {
                        final var itemStack = new ItemStackBuilder(Material.CLOCK)
                            .maxStackSize(1)
                            .customName(Component.text("Tick Progress Canceler").color(NamedTextColor.GOLD))
                            .glint(true)
                            .customData(MojangsonPath.of("custom_item_tag"), "tick_progress_canceler");

                        event.getPlayer().getInventory().addItem(itemStack.build());
                    })
            )
            .add(
                ItemButton.splashPotion()
                    .name(Component.text("断命のスプラッシュポーション"))
                    .lore(Component.text("即死").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED))
                    .glint(true)
                    .onClick(event -> {
                        final ItemStackBuilder potion = new ItemStackBuilder(Material.SPLASH_POTION)
                            .potionEffect(new PotionEffect(
                                PotionEffectType.INSTANT_DAMAGE,
                                1,
                                29,
                                false,
                                false
                            ))
                            .potionEffect(new PotionEffect(
                                PotionEffectType.INSTANT_HEALTH,
                                1,
                                29,
                                false,
                                false
                            ))
                            .potionColor(Color.BLACK)
                            .hideComponent(DataComponentTypes.POTION_CONTENTS)
                            .itemName(Component.text("即死のスプラッシュポーション"))
                            .lore(Component.text("回復 -(2 ^ 32)").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.RED));

                        event.getPlayer().getInventory().addItem(potion.build());
                    })
            )
            .add(
                ItemButton.item(Material.GOLDEN_SWORD)
                    .name(Component.text("Sword of Overwrite").color(NamedTextColor.GOLD))
                    .lore(Component.empty())
                    .lore(Component.text("利き手に持ったとき：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY))
                    .lore(Component.text(" - 攻撃力").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN))
                    .lore(Component.text(" 1.6 攻撃速度").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN))
                    .onClick(event -> {
                        final ItemStackBuilder sword = new ItemStackBuilder(Material.GOLDEN_SWORD)
                            .attributeModifier(
                                Attribute.ATTACK_DAMAGE,
                                new NamespacedKey("tpl", "attack_damage"),
                                0.1,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND,
                                AttributeModifierDisplay.reset()
                            )
                            .attributeModifier(
                                Attribute.ATTACK_SPEED,
                                new NamespacedKey("tpl", "attack_speed"),
                                0.1,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND,
                                AttributeModifierDisplay.reset()
                            )
                            .unbreakable(true)
                            .hideFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .hideFlag(ItemFlag.HIDE_UNBREAKABLE)
                            .itemName(Component.text("Sword of Overwrite").color(NamedTextColor.GOLD))
                            .lore(Component.empty())
                            .lore(Component.text("利き手に持ったとき：").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY))
                            .lore(Component.text(" - 攻撃力").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN))
                            .lore(Component.text(" 1.6 攻撃速度").decoration(TextDecoration.ITALIC, false).color(NamedTextColor.DARK_GREEN))
                            .customData(MojangsonPath.of("custom_item_tag"), "sword_of_overwrite");

                        event.getPlayer().getInventory().addItem(sword.build());
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
