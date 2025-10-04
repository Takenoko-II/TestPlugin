package com.gmail.subnokoii78.testplugin.commands;

import com.gmail.subnokoii78.testplugin.TestPlugin;
import com.gmail.subnokoii78.testplugin.system.field.GameFieldRestorer;
import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.commands.AbstractCommand;
import com.gmail.subnokoii78.tplcore.vector.BlockPositionBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

import java.util.Map;

@NullMarked
public class DatabaseCommand extends AbstractCommand {
    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("database")
            .requires(stack -> {
                return stack.getSender().isOp();
            })
            .then(
                Commands.literal("game_field_restorer")
                    .then(
                        Commands.literal("get_context_dimension")
                            .executes(this::getDataOfGameFieldRestorer)
                    )
            )
            .build();
    }

    @Override
    protected String getDescription() {
        return "データベース確認用コマンド";
    }

    private int getDataOfGameFieldRestorer(CommandContext<CommandSourceStack> context) {
        if (!GameFieldRestorer.hasRestorer(context.getSource().getLocation().getWorld())) {
            return failure(context.getSource(), new IllegalStateException(
                "指定のディメンションにはGameFieldRestorerが存在しません"
            ));
        }

        Bukkit.getScheduler().runTaskAsynchronously(TPLCore.getPlugin(), () -> {
            final Map<BlockPositionBuilder, String> data = GameFieldRestorer.getRestorer(context.getSource().getLocation().getWorld()).get();
            final TextComponent.Builder builder = Component.text();

            builder.append(Component.text(
                data.keySet().size() + "件のデータが見つかりました: "
            ));

            data.forEach((k, v) -> {
                builder.appendNewline();
                builder.append(Component.text(
                    k + ": " + v
                ));
            });

            context.getSource().getSender().sendMessage(builder);
        });

        return Command.SINGLE_SUCCESS;
    }

    public static final DatabaseCommand DATABASE_COMMAND = new DatabaseCommand();
}
