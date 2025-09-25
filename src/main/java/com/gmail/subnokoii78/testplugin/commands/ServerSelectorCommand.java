package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class ServerSelectorCommand extends AbstractCommand {
    @Override
    public @NotNull LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("serverselector")
            .executes(ctx -> {
                final Entity executor = ctx.getSource().getExecutor();

                if (!(executor instanceof Player player)) {
                    return failure(ctx.getSource(), new IllegalStateException(
                        "実行者はプレイヤーである必要があります"
                    ));
                }

                final PlayerInventory inventory = player.getInventory();
                final ItemStack serverSelector = TPLCore.paperVelocityManager.getServerSelectorItemStack();
                if (inventory.contains(serverSelector)) {
                    return failure(ctx.getSource(), new IllegalStateException(
                        "既にサーバーセレクタを所持しています"
                    ));
                }
                else {
                    for (final ItemStack itemStack : inventory.getStorageContents()) {
                        if (itemStack == null) {
                            player.getInventory().addItem(serverSelector);
                            return Command.SINGLE_SUCCESS;
                        }
                    }

                    return failure(ctx.getSource(), new IllegalStateException(
                        "インベントリがいっぱいです"
                    ));
                }
            })
            .build();
    }

    public static final ServerSelectorCommand SERVER_SELECTOR_COMMAND = new ServerSelectorCommand();
}
