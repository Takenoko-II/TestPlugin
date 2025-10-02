package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.field.GameFieldChangeObserver;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GameFieldCommand extends AbstractCommand {
    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("gamefield")
            .requires(stack -> {
                return stack.getSender().isOp();
            })
            .then(
                Commands.literal("observe").executes(this::observe)
            )
            .then(
                Commands.literal("flush").executes(this::flush)
            )
            .then(
                Commands.literal("restore").executes(this::restore)
            )
            .build();
    }

    @Override
    protected String getDescription() {
        return "ゲームフィールド操作コマンド";
    }

    private int observe(CommandContext<CommandSourceStack> context) {
        if (GameFieldChangeObserver.INSTANCE.isEnabled) {
            return failure(context.getSource(), new IllegalStateException(
                "既に有効化されています"
            ));
        }
        else {
            GameFieldChangeObserver.INSTANCE.isEnabled = true;
            return Command.SINGLE_SUCCESS;
        }
    }

    private int flush(CommandContext<CommandSourceStack> context) {
        try {
            TestPlugin.getGameFieldRestorer().flush();
        }
        catch (IllegalStateException e) {
            return failure(context.getSource(), e);
        }
        return Command.SINGLE_SUCCESS;
    }

    private int restore(CommandContext<CommandSourceStack> context) {
        if (!GameFieldChangeObserver.INSTANCE.isEnabled) return failure(context.getSource(), new IllegalStateException(
            "監視が有効化されていません"
        ));

        try {
            TestPlugin.getGameFieldRestorer().restore();
        }
        catch (IllegalStateException e) {
            return failure(context.getSource(), e);
        }
        GameFieldChangeObserver.INSTANCE.isEnabled = false;
        return Command.SINGLE_SUCCESS;
    }

    public static final GameFieldCommand GAME_FIELD_COMMAND = new GameFieldCommand();
}
