package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.gmail.subnokoii78.tplcore.network.PaperVelocityManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class LobbyCommand extends AbstractCommand {
    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("lobby")
            .then(
                Commands.argument("target", ArgumentTypes.players())
                    .requires(stack -> {
                        return stack.getSender().isOp();
                    })
                    .executes(this::other)
            )
            .executes(this::self)
            .build();
    }

    @Override
    protected String getDescription() {
        return "プレイヤーをロビーにテレポートさせます";
    }

    private int other(CommandContext<CommandSourceStack> context) {
        final List<Player> players;
        try {
            players = context.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
        }
        catch (CommandSyntaxException e) {
            return failure(context.getSource(), e);
        }

        for (final Player player : players) {
            TPLCore.paperVelocityManager.transfer(player, PaperVelocityManager.BoAServer.LOBBY);
        }

        context.getSource().getSender().sendMessage(Component.text(
             players.size() + " 人のプレイヤーのロビーへの転送を試行します"
        ));

        return Command.SINGLE_SUCCESS;
    }

    private int self(CommandContext<CommandSourceStack> context) {
        final Entity executor = context.getSource().getExecutor();

        if (!(executor instanceof Player player)) {
            return failure(context.getSource(), new IllegalStateException(
                "実行者がプレイヤーではありません"
            ));
        }

        TPLCore.paperVelocityManager.transfer(player, PaperVelocityManager.BoAServer.LOBBY);

        context.getSource().getSender().sendMessage(Component.text(
            player.getName() + " のロビーへの転送を試行します"
        ));

        return Command.SINGLE_SUCCESS;
    }

    public static final LobbyCommand LOBBY_COMMAND = new LobbyCommand();
}
