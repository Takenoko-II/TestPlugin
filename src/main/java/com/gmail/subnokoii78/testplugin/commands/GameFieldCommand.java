package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.testplugin.system.field.GameFieldRestorer;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
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
                Commands.literal("restore").executes(this::restore)
            )
            .build();
    }

    @Override
    protected String getDescription() {
        return "ゲームフィールド操作コマンド";
    }

    private int observe(CommandContext<CommandSourceStack> context) {
        final GameFieldRestorer restorer = GameFieldRestorer.getOrCreateRestorer(context.getSource().getLocation().getWorld(), true);

        if (restorer.isEnabled()) {
            return failure(context.getSource(), new IllegalStateException(
                "既に有効化されています"
            ));
        }
        else {
            restorer.setEnabled(true);
            context.getSource().getSender().sendMessage(Component.text(
                "ゲームフィールドの監視を開始しました"
            ));
            return Command.SINGLE_SUCCESS;
        }
    }

    private int restore(CommandContext<CommandSourceStack> context) {
        if (!GameFieldRestorer.hasRestorer(context.getSource().getLocation().getWorld())) {
            return failure(context.getSource(), new IllegalStateException(
                "指定のディメンションにはGameFieldRestorerが存在しません"
            ));
        }

        final GameFieldRestorer restorer = GameFieldRestorer.getRestorer(context.getSource().getLocation().getWorld());

        if (!restorer.isEnabled()) return failure(context.getSource(), new IllegalStateException(
            "監視が有効化されていません"
        ));

        final int sum;
        try {
            sum = restorer.flush();
        }
        catch (IllegalStateException e) {
            return failure(context.getSource(), e);
        }
        context.getSource().getSender().sendMessage(Component.text(
            sum+ "件のバッチをデータベース書きだしました"
        ));

        try {
            restorer.restore();
        }
        catch (IllegalStateException e) {
            return failure(context.getSource(), e);
        }
        restorer.setEnabled(false);
        context.getSource().getSender().sendMessage(Component.text(
            "ゲームフィールドを修復し、監視を終了しました"
        ));
        return Command.SINGLE_SUCCESS;
    }

    public static final GameFieldCommand GAME_FIELD_COMMAND = new GameFieldCommand();
}
