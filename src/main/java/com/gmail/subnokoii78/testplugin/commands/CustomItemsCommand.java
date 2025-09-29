package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.gmail.subnokoii78.tplcore.commands.arguments.AbstractEnumerationArgument;
import com.gmail.subnokoii78.tplcore.commands.arguments.CommandArgumentableEnumeration;
import com.gmail.subnokoii78.tplcore.itemstack.ItemStackBuilder;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomItemsCommand extends AbstractCommand {
    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("customitems")
            .requires(stack -> {
                return stack.getSender().isOp();
            })
            .then(
                Commands.argument("name", CustomItemArgument.customItem())
                    .executes(ctx -> {
                        final Entity entity = ctx.getSource().getExecutor();

                        if (!(entity instanceof Player player)) return failure(ctx.getSource(), new IllegalStateException(
                            "実行者がプレイヤーではありません"
                        ));

                        player.getInventory().addItem(ctx.getArgument("name", CustomItem.class).getItem());

                        return Command.SINGLE_SUCCESS;
                    })
            )
            .build();
    }

    @Override
    protected String getDescription() {
        return "カスタムアイテムを出すためだけのコマンド";
    }

    @NullMarked
    public static final class CustomItemArgument extends AbstractEnumerationArgument<CustomItem> {
        private CustomItemArgument() {}

        @Override
        protected Class<CustomItem> getEnumClass() {
            return CustomItem.class;
        }

        @Override
        protected String getErrorMessage(String s) {
            return s + " is not a valid custom item name";
        }

        public static CustomItemArgument customItem() {
            return new CustomItemArgument();
        }
    }

    public enum CustomItem implements CommandArgumentableEnumeration {
        GRAPPLING_HOOK() {
            @Override
            protected ItemStack getItem() {
                return new ItemStackBuilder(Material.FISHING_ROD)
                    .itemName(Component.text("Grappling Hook").color(NamedTextColor.GOLD))
                    .customData(MojangsonPath.of("custom_item_tag"), "grappling_hook")
                    .build();
            }
        },

        INSTANT_SHOOT_BOW() {
            @Override
            protected ItemStack getItem() {
                return new ItemStackBuilder(Material.BOW)
                    .customName(Component.text("Instant Shoot Bow").color(NamedTextColor.GOLD))
                    .customData(MojangsonPath.of("custom_item_tag"), "instant_shoot_bow")
                    .build();
            }
        };

        CustomItem() {

        }

        @ApiStatus.OverrideOnly
        protected ItemStack getItem() {
            return new ItemStack(Material.AIR);
        }
    }

    public static final CustomItemsCommand CUSTOM_ITEMS = new CustomItemsCommand();
}
