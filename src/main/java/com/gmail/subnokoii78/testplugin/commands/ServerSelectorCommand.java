package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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

                TPLCore.paperVelocityManager.getServerSelectorInteraction().open(player);

                return Command.SINGLE_SUCCESS;
            })
            .build();
    }

    public static final ServerSelectorCommand SERVER_SELECTOR_COMMAND = new ServerSelectorCommand();
}
